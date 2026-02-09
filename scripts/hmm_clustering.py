"""
Enhanced HMM-based Notebook Sequence Clustering

This script provides an advanced solution for analyzing notebook sequences using 
Hidden Markov Models with sophisticated complexity features and adaptive clustering.
It creates homogeneous clusters by using multi-metric distance calculation and 
automatic cluster refinement.

Features:
- Enhanced complexity features (15 advanced metrics)
- Multi-metric distance calculation (edit distance, pattern alignment, complexity features)
- Adaptive clustering with automatic refinement
- Maximum cluster size limits to prevent unwieldy large clusters
- Comprehensive visualization and analysis

Usage: python hmm_clustering.py competition_id [--config path]
Output: Enhanced HMM clustering results, analysis, and visualizations
"""

import json
import numpy as np
import pandas as pd
from scipy.spatial.distance import squareform
from scipy.cluster.hierarchy import linkage, fcluster
import Levenshtein
import warnings
from tqdm import tqdm
from pd_utils import KERNEL_JSON_COLUMNS, load_all_kernels, save_clusters, save_kernels
from kaggle_types import ClusterColumns, KernelColumns
from joblib import Parallel, delayed
warnings.filterwarnings('ignore')

# Load configuration
def load_config(config_path='clustering_config.json'):
    """Load clustering configuration from JSON file"""
    try:
        with open(config_path, 'r') as f:
            config = json.load(f)
        print(f"Loaded configuration from {config_path}")
        return config
    except FileNotFoundError:
        print(f"Warning: Configuration file {config_path} not found. Using default values.")
        return get_default_config()

def get_default_config():
    """Return default configuration if config file is missing"""
    return {
        "clustering": {
            "max_cluster_size": 25,
            "min_cluster_size": 3,
            "initial_clusters": 20,
            "refinement_threshold": 0.5,
            "linkage_method": "ward"
        },
        "distance_weights": {
            "individual_notebooks": {
                "edit_distance": 0.25,
                "complexity_features": 0.25,
                "state_distribution": 0.15,
                "transition_matrix": 0.15,
                "length_similarity": 0.10,
                "pattern_similarity": 0.10
            },
            "inter_cluster": {
                "complexity_features": 0.35,
                "state_distribution": 0.25,
                "transition_matrix": 0.25,
                "length_similarity": 0.15
            }
        },
        "similarity_methods": {
            "transition_matrix_method": "frobenius",
            "ngram_sizes": [2, 3]
        },
        "processing": {
            "state_range": {"min": 0, "max": 11},
            "laplace_smoothing": 1e-6,
            "entropy_epsilon": 1e-10,
            "min_sequence_length": 1
        },
        "sorting": {
            "cluster_order_ascending": [True, True, False],
            "cluster_order_columns": ["cluster_order", "cluster_id", "sequence_length"]
        },
        "random_seed": 42
    }

# Global configuration
CONFIG = load_config()

class HMMClusterer:
    """Main clustering class supporting multiple algorithms"""
    
    def __init__(self, config=None):
        # Use provided config or global config
        self.config = config if config is not None else CONFIG
        
        # Extract clustering parameters from config
        clustering_config = self.config['clustering']
        self.max_cluster_size = clustering_config['max_cluster_size']
        self.min_cluster_size = clustering_config['min_cluster_size']
        self.initial_clusters = clustering_config['initial_clusters']
        self.refinement_threshold = clustering_config['refinement_threshold']
        self.linkage_method = clustering_config['linkage_method']
        
        # Extract similarity methods
        similarity_config = self.config['similarity_methods']
        self.transition_method = similarity_config['transition_matrix_method']
        self.ngram_sizes = similarity_config['ngram_sizes']
        
        # Extract processing parameters
        processing_config = self.config['processing']
        self.state_min = processing_config['state_range']['min']
        self.state_max = processing_config['state_range']['max']
        self.laplace_smoothing = processing_config['laplace_smoothing']
        self.entropy_epsilon = processing_config['entropy_epsilon']
        self.min_sequence_length = processing_config['min_sequence_length']
        
        # Extract distance weights
        self.individual_weights = self.config['distance_weights']['individual_notebooks']
        self.cluster_weights = self.config['distance_weights']['inter_cluster']
        
        print(f"Initialized HMMClusterer with:")
        print(f"  Max cluster size: {self.max_cluster_size}")
        print(f"  Initial clusters: {self.initial_clusters}")
        print(f"  Transition method: {self.transition_method}")
        print(f"  State range: {self.state_min}-{self.state_max}")
        
        # Load state names from class_mapping.json
        try:
            with open('./class_mapping.json', 'r') as f:
                class_mapping = json.load(f)
            self.state_names = list(class_mapping.keys())
            print(f"Loaded {len(self.state_names)} state names from class_mapping.json: {self.state_names}")
        except FileNotFoundError:
            # Fallback state names
            self.state_names = [f"state_{i}" for i in range(12)]
            print(f"Warning: Could not load class_mapping.json, using fallback state names: {self.state_names}")
    
    def load_valid_notebook_sequences(self, file_base):
        kernels = (
            load_all_kernels(file_base + "AllCompetitionKernels_tmp.csv")
        )
        
        return kernels.dropna(subset=KERNEL_JSON_COLUMNS)
  
    def calculate_transition_similarity(self, trans1, trans2, method='frobenius'):
        """Calculate similarity between two transition matrices"""
        # Handle None values
        if trans1 is None or trans2 is None:
            return 1.0  # Maximum distance for None matrices
        
        if method == 'frobenius':
            # Frobenius norm distance
            return np.linalg.norm(trans1 - trans2, 'fro')
        elif method == 'kl_divergence':
            # Average KL divergence between rows
            kl_sum = 0
            valid_rows = 0
            for i in range(trans1.shape[0]):
                if np.sum(trans1[i]) > 0 and np.sum(trans2[i]) > 0:
                    # Add small epsilon to avoid log(0)
                    p1 = trans1[i] + 1e-10
                    p2 = trans2[i] + 1e-10
                    p1 = p1 / np.sum(p1)
                    p2 = p2 / np.sum(p2)
                    kl_sum += np.sum(p1 * np.log(p1 / p2))
                    valid_rows += 1
            return kl_sum / valid_rows if valid_rows > 0 else 0
        elif method == 'cosine':
            # Cosine distance between flattened matrices
            flat1 = trans1.flatten()
            flat2 = trans2.flatten()
            dot_product = np.dot(flat1, flat2)
            norm1 = np.linalg.norm(flat1)
            norm2 = np.linalg.norm(flat2)
            if norm1 == 0 or norm2 == 0:
                return 1.0
            return 1 - (dot_product / (norm1 * norm2))
    
    def compute_pattern_similarity(self, ngrams1, ngrams2):
        """Compute pattern similarity using n-grams"""
        
        if len(ngrams1) == 0 and len(ngrams2) == 0:
            return 0
        
        # Jaccard distance
        intersection = len(ngrams1.intersection(ngrams2))
        union = len(ngrams1.union(ngrams2))
        
        if union == 0:
            return 0
        
        return 1 - (intersection / union)
    
    def compute_distance_matrix(self, notebooks: pd.DataFrame):
        """Compute enhanced distance matrix between all notebooks"""
        n = len(notebooks)
        
        block_size = 50
        blocks = [
            (i, min(i + block_size, n))
            for i in range(0, n, block_size)
        ]

        results = Parallel(n_jobs=-1, backend="loky")(
            delayed(self.compute_block)(i0, i1, notebooks)
            for i0, i1 in tqdm(blocks, desc="Distance matrix blocks")
        )

        distance_matrix = np.zeros((n, n))
        for block in results:
            for i, j, d in block:
                distance_matrix[i, j] = d
                distance_matrix[j, i] = d
        
        return distance_matrix
    
    def compute_block(self, i_start, i_end, notebooks):
        complexity = np.stack(notebooks[KernelColumns.COMPLEXITY_FEATURES_NORM].to_numpy())
        state = np.stack(notebooks[KernelColumns.LABEL_STATS_NORM].to_numpy())
        
        n = len(notebooks)
        block = []

        for i in range(i_start, i_end):
            for j in range(i + 1, n):
                n1 = notebooks.iloc[i]
                n2 = notebooks.iloc[j]
                # Enhanced distance calculation with sophisticated features
                
                # 1. Sequence similarity using edit distance
                seq1 = n1[KernelColumns.LABEL_SEQUENCE]
                seq2 = n2[KernelColumns.LABEL_SEQUENCE]
                edit_dist = Levenshtein.distance(seq1, seq2)
                max_len = max(len(seq1), len(seq2))
                normalized_edit_dist = edit_dist / max_len if max_len > 0 else 0
                
                # 2. Complexity feature similarity
                complexity_dist = np.linalg.norm(complexity[i] - complexity[j])
                
                # 3. State distribution similarity
                state_dist = state_dist = np.linalg.norm(state[i] - state[j])
                
                # 4. Transition matrix similarity
                trans1 = n1[KernelColumns.TRANSITION_MATRIX_NORM]
                trans2 = n2[KernelColumns.TRANSITION_MATRIX_NORM]
                trans_dist = self.calculate_transition_similarity(trans1, trans2, method=self.transition_method)
                
                # 5. Length similarity (penalize very different lengths)
                len1, len2 = len(seq1), len(seq2)
                length_ratio = min(len1, len2) / max(len1, len2) if max(len1, len2) > 0 else 1
                length_dist = 1 - length_ratio
                
                # 6. Pattern similarity using n-grams
                ngrams_1 = n1[KernelColumns.N_GRAMS]
                ngrams_2 = n2[KernelColumns.N_GRAMS]
                pattern_dist = self.compute_pattern_similarity(ngrams_1, ngrams_2)
                
                # Combine distances with weights from config
                weights = self.individual_weights
                combined_dist = (weights['edit_distance'] * normalized_edit_dist +
                                weights['complexity_features'] * complexity_dist +
                                weights['state_distribution'] * state_dist +
                                weights['transition_matrix'] * trans_dist +
                                weights['length_similarity'] * length_dist +
                                weights['pattern_similarity'] * pattern_dist)
                
                # compute combined_dist exactly as you already do
                block.append((i, j, combined_dist))

        return block
    
    def cluster_notebooks(self, distance_matrix):
        """Cluster notebooks using adaptive hierarchical clustering with refinement"""
        return self.hierarchical_clustering_with_refinement(distance_matrix)
    
    def hierarchical_clustering_with_refinement(self, distance_matrix):
        """Perform hierarchical clustering with cluster refinement"""
        
        # Converts a square distance matrix into a condensed (1D) format
        condensed_dist = squareform(distance_matrix, checks=False)
        # Performs hierarchical clustering to build a dendrogram tree        
        linkage_matrix = linkage(condensed_dist, method=self.linkage_method)
        # Cuts the hierarchical tree to form flat clusters
        cluster_labels = fcluster(linkage_matrix, self.initial_clusters, criterion='maxclust')
        
        # Refine clusters that have high internal distance
        refined_labels = cluster_labels.copy()
        next_cluster_id = max(cluster_labels) + 1
        
        for cluster_id in np.unique(cluster_labels):
            cluster_indices = np.where(cluster_labels == cluster_id)[0]
            
            if len(cluster_indices) <= 2:  # Skip very small clusters
                continue
            
            # Calculate average internal distance
            cluster_distances = []
            for i in range(len(cluster_indices)):
                for j in range(i + 1, len(cluster_indices)):
                    idx1, idx2 = cluster_indices[i], cluster_indices[j]
                    cluster_distances.append(distance_matrix[idx1, idx2])
            
            avg_internal_distance = np.mean(cluster_distances) if cluster_distances else 0
            
            # If average internal distance is high and cluster is large, try to split
            if (avg_internal_distance > self.refinement_threshold or
                len(cluster_indices) > self.max_cluster_size):
                
                # Extract sub-distance matrix for this cluster
                sub_distance_matrix = distance_matrix[np.ix_(cluster_indices, cluster_indices)]
                
                # Try to split into 2 sub-clusters
                n_subclusters = int(len(cluster_indices) / self.max_cluster_size) + 1
                sub_condensed = squareform(sub_distance_matrix)
                sub_linkage = linkage(sub_condensed, method=self.linkage_method)
                sub_labels = fcluster(sub_linkage, n_subclusters, criterion='maxclust')
                
                # Apply the split - only reassign non-first clusters
                for sub_id in np.unique(sub_labels)[1:]:  # Skip first cluster (ID 1)
                    sub_mask = sub_labels == sub_id
                    refined_labels[cluster_indices[sub_mask]] = next_cluster_id
                    next_cluster_id += 1
        
        return refined_labels

def get_clustered_kernels(kernels: pd.DataFrame, clusterer: HMMClusterer) -> pd.DataFrame:
    groups = [group for _, group in kernels.groupby(KernelColumns.SOURCE_COMPETITION_ID)]
    results = []
    for group in groups:
        clustered_group = cluster_for_competition(group, clusterer)
        results.append(clustered_group)

    kernels_clustered = pd.concat(results, ignore_index=True)
    
    kernels_clustered = kernels_clustered[[KernelColumns.LOCAL_CLUSTER_ID, KernelColumns.KERNEL_VERSION_ID, KernelColumns.SOURCE_COMPETITION_ID]]
    
    kernels_clustered[KernelColumns.CLUSTER_ID] = pd.factorize(list(zip(
        kernels_clustered[KernelColumns.SOURCE_COMPETITION_ID],
        kernels_clustered[KernelColumns.LOCAL_CLUSTER_ID]
    )))[0]
    
    return kernels_clustered

def cluster_for_competition(group: pd.DataFrame, clusterer: HMMClusterer) -> pd.DataFrame:
    group = group.copy()
    print(f"Starting clustering for competition {group[KernelColumns.SOURCE_COMPETITION_ID].iloc[0]}")
    if len(group) >= 2:
        # Compute enhanced distance matrix
        distance_matrix = clusterer.compute_distance_matrix(group)
        distance_matrix = np.nan_to_num(distance_matrix)
        
        # Perform adaptive clustering with refinement
        cluster_labels = clusterer.cluster_notebooks(distance_matrix)
        
        # Save results
        group[KernelColumns.LOCAL_CLUSTER_ID] = cluster_labels
        print(f"Found {len(np.unique(cluster_labels))} clusters")
    else:
        print("Not enough notebooks for clustering analysis")
        
    return group

def main():
    """Main function to run the enhanced HMM clustering analysis"""
    
    # Load configuration
    config = load_config()
    
    # Initialize clusterer with config
    clusterer = HMMClusterer(config=config)
    
    # Set random seed from config
    random_seed = config['random_seed']
    np.random.seed(random_seed)
    print(f"Set random seed to: {random_seed}")
    
    print("="*80)
    print("RUNNING ENHANCED HMM CLUSTERING")
    print("="*80)
    
    # Load notebooks with enhanced features
    print("Loading notebook sequences with enhanced features...")
    kernels = clusterer.load_valid_notebook_sequences("")
    print(f"Loaded {len(kernels)} valid notebook sequences")
    
    kernels_clustered = get_clustered_kernels(kernels, clusterer)
    
    all_kernels = load_all_kernels("AllCompetitionKernels_tmp.csv")
    all_kernels_with_clusters = (
        all_kernels
        .merge(kernels_clustered[[
                KernelColumns.CLUSTER_ID,
                KernelColumns.KERNEL_VERSION_ID]], 
            on=KernelColumns.KERNEL_VERSION_ID,
            how="left")
    )
    
    cluster_map = kernels_clustered[[ClusterColumns.CLUSTER_ID, ClusterColumns.LOCAL_CLUSTER_ID]].drop_duplicates()
    save_clusters(cluster_map, "Clusters.csv")
    
    save_kernels(all_kernels_with_clusters, "AllCompetitionKernels_clustered.csv")

if __name__ == "__main__":
    main()
