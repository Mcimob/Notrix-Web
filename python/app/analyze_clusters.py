#!/usr/bin/env python3
"""
Cluster Analysis GPT Summarizer

This script analyzes HMM-based notebook clustering results and generates 
comprehensive summaries using GPT-5. It leverages pre-calculated clustering
data including cluster statistics, state distributions, and complexity features.

Features:
- Loads pre-calculated cluster analysis from CSV files
- Converts integer sequences to text using class mappings
- Calculates transition patterns and workflow statistics
- Generates GPT-5 summaries with four key sections:
  * Cluster Archetypes Identified
  * Top Transition Patterns Discovered  
  * Workflow Recommendations by Project Type
  * Individual Cluster Summaries
- Parses GPT responses into separate sections for structured output

Usage: python analyze_clusters.py --competition_id 18599
Output: {competition_id}_summarized.json with GPT-generated analysis and parsed sections
"""

import argparse
import json
from collections import ChainMap, Counter
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Any

import numpy as np
import pandas as pd
from app.kaggle_types import ClusterColumns, KernelColumns
import app.ai as ai
import pickle as pkl
from tqdm import tqdm

from app.pd_utils import KERNEL_JSON_COLUMNS, load_all_kernels, load_clusters, save_clusters


def load_class_mapping() -> Dict[int, str]:
    """Load the integer-to-text class mapping."""
    mapping_path = Path(__file__).parent / 'class_mapping.json'
    
    if not mapping_path.exists():
        raise FileNotFoundError(f"Class mapping file not found at {mapping_path}")
    
    with open(mapping_path, 'r') as f:
        # The mapping file has text->int, we need int->text
        text_to_int = json.load(f)
        int_to_text = {v: k for k, v in text_to_int.items()}
    
    return int_to_text

def top10_indices(matrices):
    class_mapping = load_class_mapping()
    # Stack matrices in this group
    summed = np.stack(matrices).sum(axis=0)

    total = summed.sum()
    if total == 0:
        return []

    # Find top-10 elements
    flat_idx = np.argpartition(summed.ravel(), -10)[-10:]
    rows, cols = np.unravel_index(flat_idx, summed.shape)

    # Sort descending by count
    order = np.argsort(summed[rows, cols])[::-1]
    rows, cols = rows[order], cols[order]

    # Build result
    result = []
    for r, c in zip(rows, cols):
        count = int(summed[r, c])
        pct = 100 * count / total

        # String representation of index
        key = f"{class_mapping[r]} → {class_mapping[c]}"

        result.append((key, count, float(pct)))

    return result

def get_cluster_avg_cells(kernels: pd.DataFrame) -> pd.DataFrame:
    return (
        kernels
        .groupby(KernelColumns.CLUSTER_ID)[KernelColumns.CELL_COUNT]
        .mean()
        .reset_index(name=ClusterColumns.AVG_CELLS)
    )

def get_cluster_avg_num_lines(kernels: pd.DataFrame) -> pd.DataFrame:
    return (
        kernels
        .groupby(KernelColumns.CLUSTER_ID)[KernelColumns.NUM_LINES]
        .mean()
        .reset_index(name=ClusterColumns.AVG_LINES)
    )

def get_cluster_avg_votes(kernels: pd.DataFrame) -> pd.DataFrame:
    return (
        kernels
        .groupby(KernelColumns.CLUSTER_ID)[KernelColumns.TOTAL_VOTES]
        .mean()
        .reset_index(name=ClusterColumns.AVG_TOTAL_VOTES)
    )
    
def get_cluster_size(kernels: pd.DataFrame) -> pd.DataFrame:
    return (
        kernels
        .groupby(KernelColumns.CLUSTER_ID)
        .size()
        .rename(ClusterColumns.CLUSTER_SIZE)
    )
    
def get_cluster_label_stats(kernels: pd.DataFrame) -> pd.DataFrame:
    return (
        kernels
        .groupby(KernelColumns.CLUSTER_ID)[KernelColumns.LABEL_STATS]
        .apply(lambda s: dict(sum((Counter(d) for d in s if isinstance(d, dict)), Counter())))
        .unstack(fill_value=0)
        .apply(
            lambda r: {str(k): int(v) for k, v in r.items() if v > 0},
            axis=1
        )
        .reset_index(name=ClusterColumns.LABEL_STATS)
    )

def get_cluster_transition_matrix(kernels: pd.DataFrame) -> pd.DataFrame:
    return (
        kernels
        .groupby(KernelColumns.CLUSTER_ID)[KernelColumns.TRANSITION_MATRIX]
        .apply(lambda arrs: sum(arrs))
        .apply(lambda l: l.astype(int))
        .reset_index()
    )

def get_cluster_sample_sequence(kernels: pd.DataFrame) -> pd.DataFrame:
    class_mapping = load_class_mapping()
    return (
        kernels
        .groupby(KernelColumns.CLUSTER_ID)[KernelColumns.LABEL_SEQUENCE]
        .agg("first")
        .apply(lambda lst: " → ".join([class_mapping[i] for i in lst[:10]]) + ("..." if len(lst) > 10 else ""))
        .rename("SampleSequence")
    )

def get_cluster_sample_sequence_length(kernels: pd.DataFrame) -> pd.DataFrame:
    return (
        kernels
        .groupby(KernelColumns.CLUSTER_ID)[KernelColumns.LABEL_SEQUENCE]
        .agg("first")
        .apply(len)
        .rename("SampleSequenceLength")
    )

def get_cluster_competition(kernels: pd.DataFrame) -> pd.DataFrame:
    return (
        kernels.groupby(KernelColumns.CLUSTER_ID)[KernelColumns.SOURCE_COMPETITION_ID]
        .agg("first")
        .rename(ClusterColumns.SOURCE_COMPETITION_ID)
    )

def generate_analysis_prompt(
    clusters: pd.DataFrame, competition_transition_stats
) -> List[str]:
    """Generate the prompt for GPT-5 analysis."""
    clusters = clusters.copy()
    
    clusters["ClusterString"] = clusters.apply(generate_cluster_string, axis=1)
    
    res = (
        clusters
        .merge(competition_transition_stats, on=KernelColumns.SOURCE_COMPETITION_ID)
        .groupby(KernelColumns.SOURCE_COMPETITION_ID)
        .apply(generate_prompt)
    )
    
    return res
        
def generate_cluster_string(cluster: pd.Series) -> str:
    class_mapping = load_class_mapping()
    return f"**Cluster {cluster[KernelColumns.CLUSTER_ID]}** (Size: {cluster["ClusterSize"]}, Type: {class_mapping[int(max(cluster[KernelColumns.LABEL_STATS], key=cluster[KernelColumns.LABEL_STATS].get) if cluster[KernelColumns.LABEL_STATS] else 12)]}): {cluster["SampleSequence"]}"
        
def generate_prompt(df: pd.DataFrame) -> str:
    if len(df) > 500:
        return ""
    prompt = f"""
Analyze the following HMM-based clustering results of {df["ClusterSize"].sum()} Kaggle notebook workflows organized into {len(df)} distinct clusters.

## Dataset Overview
- **Total Notebooks**: {df["ClusterSize"].sum()}
- **Total Clusters**: {len(df)}
- **Average Cluster Size**: {df["ClusterSize"].mean():.1f}

## ML Workflow States
The sequences consist of these machine learning lifecycle stages:
- **Environment**: Setup and imports
- **Data_Extraction**: Loading and importing data
- **Data_Transform**: Data cleaning and preprocessing  
- **EDA**: Exploratory data analysis
- **Visualization**: Data visualization and plotting
- **Feature_Engineering**: Feature creation and selection
- **Hyperparam_Tuning**: Hyperparameter optimization
- **Model_Train**: Model training and fitting
- **Model_Evaluation**: Model assessment and validation
- **Data_Export**: Saving results and outputs

## Sample Cluster Patterns
{chr(10).join(df["ClusterString"])}

## Top Transition Patterns  
{chr(10).join([f"{i+1}. **{pattern[0]}**: {pattern[1]} occurrences ({pattern[2]:.1f}%)" for i, pattern in enumerate(df["TransitionStats"].iloc[0])])}


---

Based on this comprehensive clustering analysis, provide a detailed report with exactly this section:

## Individual Cluster Summaries
Provide a one-sentence summary for EACH AND EVERY cluster from the {len(df)} clusters shown above (cluster IDs: {', '.join(map(str, sorted(df[KernelColumns.CLUSTER_ID])))}):
- Cluster X: Brief description of the workflow pattern, dominant states, and typical use case
- Format each summary as: "Cluster [ID]: [One sentence describing the cluster's characteristics and purpose]"
- IMPORTANT: Include ALL {len(df)} clusters - do not skip any cluster numbers

Keep the analysis data-driven, referencing specific transition percentages, and workflow patterns from the provided data.
"""
    
    return prompt.strip()


def query_gpt5(prompt: str) -> str:
    """Query GPT-5 with the analysis prompt."""
    client = ai.get_client()
    
    try:
        response = client.chat.completions.create(
            model="gpt-4o",  # Using GPT-4o as GPT-5 may not be available
            messages=[
                {
                    "role": "system", 
                    "content": "You are an expert data scientist specializing in machine learning workflow analysis and clustering. Provide detailed, data-driven insights based on HMM clustering results of notebook sequences."
                },
                {
                    "role": "user", 
                    "content": prompt
                }
            ],
            max_tokens=16384,  # Increased to accommodate summaries for all clusters
            temperature=0.3  # Lower temperature for more consistent, analytical responses
        )
        
        return response.choices[0].message.content
        
    except Exception as e:
        raise Exception(f"GPT API call failed: {e}")


def parse_individual_summaries(individual_summaries_text: str) -> Dict[int, str]:
    """Parse individual cluster summaries into a dictionary."""
    summaries = {}
    lines = individual_summaries_text.split('\n')
    
    for line in lines:
        line = line.strip()
        if line.startswith('- **Cluster') or line.startswith('**Cluster'):
            # Extract cluster number and description
            try:
                # Handle both formats: "- **Cluster X**:" and "**Cluster X**:"
                if line.startswith('- **Cluster'):
                    cluster_part = line[3:]  # Remove "- "
                else:
                    cluster_part = line
                
                # Find the cluster number
                cluster_start = cluster_part.find('Cluster ') + 8
                cluster_end = cluster_part.find('**:', cluster_start)
                if cluster_end == -1:
                    cluster_end = cluster_part.find(':', cluster_start)
                
                cluster_id = int(cluster_part[cluster_start:cluster_end])
                description = cluster_part[cluster_end+4:].strip()
                if description.startswith(': '):
                    description = description[2:]
                
                summaries[cluster_id] = description
            except (ValueError, IndexError):
                continue
    
    return summaries


def parse_gpt_response(gpt_response: str) -> Dict[str, Any]:
    """Parse the GPT response into separate sections with structured data."""
    sections = {
        "individual_summaries": ""
    }
    # Split the response into sections based on markdown headers
    lines = gpt_response.split('\n')
    current_section = None
    current_content = []
    
    for line in lines:
        # Check for section headers
        if line.strip().startswith('## Individual Cluster Summaries'):
            if current_section and current_content:
                sections[current_section] = '\n'.join(current_content).strip()
            current_section = "individual_summaries"
            current_content = []
        else:
            # Add content to current section
            if current_section:
                current_content.append(line)
    
    # Don't forget the last section
    if current_section and current_content:
        sections[current_section] = '\n'.join(current_content).strip()
    
    # Parse structured data from sections
    parsed_data = {
        "individual_summaries": {
            "raw_text": sections["individual_summaries"],
            "structured": parse_individual_summaries(sections["individual_summaries"])
        }
    }
    
    return parsed_data


def save_analysis_result(
    gpt_response: str
) -> dict:
    """Save the analysis results to a JSON file."""
    
    # Parse the GPT response into separate sections
    parsed_sections = parse_gpt_response(gpt_response)
    
    # Structure the output data
    output_data = {
        "generated_at": datetime.now().isoformat(),
        "analysis_sections": parsed_sections,
        "gpt_analysis_full": gpt_response
    }
    
    return output_data

def add_cluster_data(kernels: pd.DataFrame, clusters: pd.DataFrame):
    kernels = kernels.copy().dropna(subset=KERNEL_JSON_COLUMNS + [KernelColumns.SOURCE_COMPETITION_ID, KernelColumns.CLUSTER_ID])
    
    competition_transition_stats = (
        kernels
        .groupby(KernelColumns.SOURCE_COMPETITION_ID)[KernelColumns.TRANSITION_MATRIX]
        .apply(top10_indices)
        .rename("TransitionStats")
        .reset_index()    
    )
    
    for df in [
        get_cluster_avg_cells(kernels),
        get_cluster_avg_num_lines(kernels),
        get_cluster_avg_votes(kernels),
        get_cluster_size(kernels),
        get_cluster_label_stats(kernels),
        get_cluster_transition_matrix(kernels),
        get_cluster_sample_sequence(kernels),
        get_cluster_sample_sequence_length(kernels),
        get_cluster_competition(kernels)
    ]:
        clusters = clusters.merge(df, on=KernelColumns.CLUSTER_ID)
    
    print("✍️  Generating GPT analysis prompt...")
    prompts = generate_analysis_prompt(clusters, competition_transition_stats)
    
    print("🤖 Querying GPT for analysis (this may take a moment)...")
    responses = []
    
    response_index = 0
    try:
        for p in tqdm(prompts, desc="Querying..."):
            if p:
                gpt_response = query_gpt5(p)
                result = save_analysis_result(gpt_response)
                responses.append(result)
                response_index += 1
    except Exception as e:
        print(f"Encountered exception {e}")
        
    with open("gpt_responses.pkl", "wb") as f:
        pkl.dump(responses, f)
    print(f"Saved {len(responses)} responses so far for further manual processing")
        
    summaries = dict(ChainMap(*[r["analysis_sections"]["individual_summaries"]["structured"] for r in responses]))    
        
    clusters[ClusterColumns.SUMMARY] = clusters[KernelColumns.CLUSTER_ID].map(summaries)
    clusters.drop(labels=["SampleSequence", "SampleSequenceLength"], axis=1, inplace=True)
    
    return clusters

def main():
    parser = argparse.ArgumentParser(
        prog="ClusterStats",
        description="Takes clusters and kernels and adds descriptions and stats to clusters",
    )
    parser.add_argument("inputDir")
    parser.add_argument("outputFile")
    args = parser.parse_args()
    
    kernels = load_all_kernels(args.inputDir + "AllCompetitionKernels.csv")
    clusters = load_clusters(args.inputDir + "Clusters.csv")
    
    clusters = add_cluster_data(kernels, clusters)
    
    save_clusters(clusters, args.outputFile)
    
    print("✅ Analysis complete!")

if __name__ == "__main__":
    main()