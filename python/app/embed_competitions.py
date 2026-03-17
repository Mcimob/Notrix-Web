import argparse

import pandas as pd
from app.kaggle_types import CompetitionClusterColumns, CompetitionColumns
from app.pd_utils import load_competitions, save_competition_clusters, save_competitions
import app.ai as ai
from sentence_transformers import SentenceTransformer
import umap
from sklearn.preprocessing import normalize
from sklearn.cluster import KMeans
import numpy as np

def process_competitions(competitions: pd.DataFrame) -> tuple[pd.DataFrame, pd.DataFrame]:
    
    coords = get_umap_coordinates(competitions)
    
    competitions[CompetitionColumns.COORDINATE_X] = coords[:, 0]
    competitions[CompetitionColumns.COORDINATE_Y] = coords[:, 1]
    
    return cluster_competitions(competitions, coords)

def get_umap_coordinates(df: pd.DataFrame) -> list[tuple[float, float]]:
    """
    Adds 2D UMAP coordinates to a competition dataframe.
    Returns a new dataframe with 'umap_x' and 'umap_y' columns.
    """

    df = df.copy()

    # -----------------------------
    # 1️⃣ Combine text fields
    # -----------------------------
    df["full_text"] = (
        df[CompetitionColumns.TITLE].fillna("") + ". " +
        df[CompetitionColumns.SUBTITLE].fillna("") + ". " +
        df[CompetitionColumns.OVERVIEW].fillna("")
    )

    # -----------------------------
    # 2️⃣ Generate embeddings
    # -----------------------------
    print("Loading embedding model...")
    model = SentenceTransformer("all-MiniLM-L6-v2")

    print("Generating embeddings...")
    embeddings = model.encode(
        df["full_text"].tolist(),
        show_progress_bar=True
    )
    df.drop(columns=["full_text"], inplace=True)

    # Normalize (important when using cosine metric)
    embeddings = normalize(embeddings)

    # -----------------------------
    # 3️⃣ UMAP projection
    # -----------------------------
    print("Running UMAP...")
    reducer = umap.UMAP(
        n_neighbors=20,
        min_dist=0.15,
        metric="cosine",
        random_state=42
    )

    coords = reducer.fit_transform(embeddings)

    return coords

def cluster_competitions(competitions: pd.DataFrame, coords: list[tuple[float, float]]):

    print("Running KMeans clustering...")

    kmeans = KMeans(
        n_clusters=12,
        random_state=42
    )

    clusters = kmeans.fit_predict(coords)
    competitions[CompetitionColumns.CLUSTER_ID] = clusters
    
    centroids = kmeans.cluster_centers_
    
    clusters = pd.DataFrame(data={
            CompetitionClusterColumns.CLUSTER_ID: sorted(np.unique(clusters)),
            CompetitionClusterColumns.CENTROID_X: centroids[:, 0],
            CompetitionClusterColumns.CENTROID_Y: centroids[:, 1]
        })
    
    # Merge competitions with cluster centroids
    cluster_sizes = (
        competitions.merge(clusters, on=CompetitionClusterColumns.CLUSTER_ID, how="left")
        .groupby(CompetitionClusterColumns.CLUSTER_ID)
        .apply(lambda x: pd.Series({
            CompetitionClusterColumns.RADIUS_X: np.mean(np.abs(x[CompetitionColumns.COORDINATE_X] - x[CompetitionClusterColumns.CENTROID_X])),
            CompetitionClusterColumns.RADIUS_Y: np.mean(np.abs(x[CompetitionColumns.COORDINATE_Y] - x[CompetitionClusterColumns.CENTROID_Y])),
            CompetitionClusterColumns.STD_X: np.std(x[CompetitionColumns.COORDINATE_X] - x[CompetitionClusterColumns.CENTROID_X]),
            CompetitionClusterColumns.STD_Y: np.std(x[CompetitionColumns.COORDINATE_Y] - x[CompetitionClusterColumns.CENTROID_Y])
        }))
    ).reset_index()

    clusters = clusters.merge(cluster_sizes, on=CompetitionClusterColumns.CLUSTER_ID)
    
    clusters[CompetitionClusterColumns.DESCRIPTION] = (
        competitions
        .groupby(CompetitionColumns.CLUSTER_ID)
        .apply(get_gpt_summary)
    )

    return competitions, clusters

def get_gpt_summary(competition_group):
    texts = (
        competition_group[CompetitionColumns.TITLE].fillna("")
        + " " + 
        competition_group[CompetitionColumns.SUBTITLE].fillna("")
    )

    prompt = f"""
        Give a short 3-5 word label for this group of competitions:
        {texts[:20].tolist()}
    """
    client = ai.get_client()

    try:
        response = client.chat.completions.create(
            model="gpt-4o",  # Using GPT-4o as GPT-5 may not be available
            messages=[
                {
                    "role": "system", 
                    "content": "You are an expert data scientist specializing in machine learning workflow analysis. Concise answers with no extras"
                },
                {
                    "role": "user", 
                    "content": prompt
                }
            ],
            max_tokens=100,
            temperature=0.3  # Lower temperature for more consistent, analytical responses
        )
        
        return response.choices[0].message.content
        
    except Exception as e:
        raise Exception(f"GPT API call failed: {e}")

def main():
    parser = argparse.ArgumentParser(
        prog="Embedding",
        description="Takes competitions and embeds them into two dimensions",
    )
    parser.add_argument("inputFile")
    parser.add_argument("outputDir")
    args = parser.parse_args()
    
    competitions = load_competitions(args.inputFile)
    competitions, clusters = process_competitions(competitions)
    save_competitions(competitions, args.outputDir + "Competitions_clustered.csv")
    save_competition_clusters(clusters, args.outputDir + "CompetitionClusters.csv")

if __name__ == "__main__":
    main()