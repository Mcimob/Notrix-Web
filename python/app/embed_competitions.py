import argparse

import pandas as pd
from kaggle_types import CompetitionColumns
from pd_utils import load_competitions, save_competitions
from sentence_transformers import SentenceTransformer
import umap
from sklearn.preprocessing import normalize

def add_umap_coordinates(df: pd.DataFrame) -> pd.DataFrame:
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

    df.drop(columns=["full_text"], inplace=True)

    return df

def main():
    parser = argparse.ArgumentParser(
        prog="Embedding",
        description="Takes competitions and embeds them into two dimensions",
    )
    parser.add_argument("inputFile")
    parser.add_argument("outputFile")
    args = parser.parse_args()
    
    competitions = load_competitions(args.inputFile)
    competitions = add_umap_coordinates(competitions)
    save_competitions(competitions, args.outputFile)

if __name__ == "__main__":
    main()