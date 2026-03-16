import argparse

import pandas as pd
from app.kaggle_types import CompetitionClusterColumns, CompetitionColumns
from app.pd_utils import load_competitions, save_competition_clusters, save_competitions
from sentence_transformers import SentenceTransformer
import umap
from sklearn.preprocessing import normalize
from sklearn.cluster import KMeans
from sklearn.feature_extraction.text import TfidfVectorizer
import numpy as np

def add_umap_coordinates(df: pd.DataFrame) -> tuple[pd.DataFrame, pd.DataFrame]:
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

    # -----------------------------
    # 4️⃣ Attach coordinates
    # -----------------------------
    df[CompetitionColumns.COORDINATE_X] = coords[:, 0]
    df[CompetitionColumns.COORDINATE_Y] = coords[:, 1]

    # -----------------------------
    # 5️⃣ Clustering
    # -----------------------------
    print("Running KMeans clustering...")

    kmeans = KMeans(
        n_clusters=12,
        random_state=42
    )

    clusters = kmeans.fit_predict(coords)
    df[CompetitionColumns.CLUSTER_ID] = clusters
    
    centroids = kmeans.cluster_centers_
    
    clusters = pd.DataFrame(data={
            CompetitionClusterColumns.CLUSTER_ID: sorted(np.unique(clusters)),
            CompetitionClusterColumns.CENTROID_X: centroids[:, 0],
            CompetitionClusterColumns.CENTROID_Y: centroids[:, 1]
        })
    
    # Merge competitions with cluster centroids
    cluster_sizes = (
        df.merge(clusters, on=CompetitionClusterColumns.CLUSTER_ID, how="left")
        .groupby(CompetitionClusterColumns.CLUSTER_ID)
        .apply(lambda x: pd.Series({
            CompetitionClusterColumns.RADIUS_X: np.mean(np.abs(x[CompetitionColumns.COORDINATE_X] - x[CompetitionClusterColumns.CENTROID_X])),
            CompetitionClusterColumns.RADIUS_Y: np.mean(np.abs(x[CompetitionColumns.COORDINATE_Y] - x[CompetitionClusterColumns.CENTROID_Y])),
            CompetitionClusterColumns.STD_X: np.std(x[CompetitionColumns.COORDINATE_X] - x[CompetitionClusterColumns.CENTROID_X]),
            CompetitionClusterColumns.STD_Y: np.std(x[CompetitionColumns.COORDINATE_Y] - x[CompetitionClusterColumns.CENTROID_Y])
        }))
    ).reset_index()

    clusters = clusters.merge(cluster_sizes, on=CompetitionClusterColumns.CLUSTER_ID)
    
    labels = {}

    for cluster_id in clusters[CompetitionClusterColumns.CLUSTER_ID]:
        clutser_competitions = df[df[CompetitionColumns.CLUSTER_ID] == cluster_id]
        texts = (
            clutser_competitions[CompetitionColumns.TITLE].fillna("")
            + " " + 
            clutser_competitions[CompetitionColumns.SUBTITLE].fillna("")
        )
        

        vectorizer = TfidfVectorizer(
            stop_words="english",
            max_features=5
        )

        X = vectorizer.fit_transform(texts)

        words = vectorizer.get_feature_names_out()
        labels[cluster_id] = ", ".join(words[:3])

    clusters[CompetitionClusterColumns.DESCRIPTION] = clusters[CompetitionClusterColumns.CLUSTER_ID].map(labels)

    df.drop(columns=["full_text"], inplace=True)

    return df, clusters

def main():
    parser = argparse.ArgumentParser(
        prog="Embedding",
        description="Takes competitions and embeds them into two dimensions",
    )
    parser.add_argument("inputFile")
    parser.add_argument("outputDir")
    args = parser.parse_args()
    
    competitions = load_competitions(args.inputFile)
    competitions, clusters = add_umap_coordinates(competitions)
    save_competitions(competitions, args.outputDir + "Competitions_clustered.csv")
    save_competition_clusters(clusters, args.outputDir + "CompetitionClusters.csv")

if __name__ == "__main__":
    main()