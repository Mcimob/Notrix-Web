#!/usr/bin/env python3
"""
Extract code and markdown cells from Jupyter notebooks in the specified competition folder.

Usage:
    python extract_codecells.py COMPETITION_ID

Example:
    python extract_codecells.py 18599


for each competition folder, go through all subfolders and collect the jupyter notebook files, 
and extract code cell blocks and store them into a json file. So this python scirpt will take the folder name as the command line argument, 
and output a json file with the same name as the folder name. 
The json file needs to contain an array of notebook objects, each representing a different Jupyter notebook.

Each Notebook Contains:  
kernelVersionId: the same as the file name  
cells: Array of code cells from the notebook

Each Cell Contains:  
cellId: Unique identifier within the notebook  
code: The actual Python code blocks
cellType: Either markdown or code
"""

import os
import sys
import json
import glob
import os
import orjson
import pandas as pd
from joblib import Parallel, delayed
from tqdm import tqdm

CELLTYPE_MAP = {
    "code": 0,
    "markdown": 1,
    "raw": 2,
    "heading": 3
}

BASE_PATH = "/media/tim/Data/Thesis/extracted_kernels"


def extract_code_cells(notebook_path):
    """
    Extract all non-empty code + markdown cells from a single .ipynb file.
    Uses orjson for maximum speed.

    Returns:
        list[dict]
    """

    # Skip missing / empty / corrupt files quickly
    if not os.path.exists(notebook_path):
        return []
    if os.path.getsize(notebook_path) < 50:
        return []

    try:
        with open(notebook_path, "rb") as f:
            nb = orjson.loads(f.read())
    except Exception:
        return []  # skip corrupt JSON notebooks

    kernel_version_id = os.path.basename(notebook_path).split('.')[0]

    rows = extract_cells_from_dict(nb, kernel_version_id)

    return rows

def extract_cells_from_dict(nb, kernel_version_id):
    rows = []

    for i, cell in enumerate(nb.get("cells", [])):
        source = cell.get("source", "")

        if isinstance(source, list):
            source = "".join(source)

        if not source or not source.strip():
            continue

        rows.append({
            "KernelVersionId": kernel_version_id,
            "CellId": str(i),
            "Source": source.strip(),
            "CellType": CELLTYPE_MAP[cell.get("cell_type", "code")] if cell.get("cell_type", "code") in CELLTYPE_MAP.keys() else 2,
        })
        
    return rows

def extract_all_code_cells(kids, n_jobs=8) -> pd.DataFrame:
    """
    Extract cells from many notebooks using parallel processing.
    """

    paths = [f"{BASE_PATH}/{kid}.ipynb" for kid in kids]

    results = Parallel(n_jobs=n_jobs, verbose=0)(
        delayed(extract_code_cells)(path) for path in tqdm(paths)
    )

    # Flatten list of lists into one list of dicts
    flat = [row for sub in results for row in sub]

    return pd.DataFrame(flat).sort_values("KernelVersionId")


def process_competition_folder(competition_id):
    """
    Process all notebooks in the specified competition folder.
    
    Args:
        competition_id (str): The competition ID
    
    Returns:
        list: List of notebooks with their code and markdown cells
    """
    base_path = os.path.join("/cluster/scratch/xiaosu/code4ml/extract_kernels/extracted_kernels", competition_id)
    if not os.path.exists(base_path):
        print(f"Error: Competition folder {competition_id} not found.")
        sys.exit(1)
    
    notebooks = []
    
    # Find all .ipynb files recursively
    ipynb_files = glob.glob(f"{base_path}/**/*.ipynb", recursive=True)
    
    print(f"Found {len(ipynb_files)} notebook files in competition {competition_id}")
    
    for i, notebook_path in enumerate(ipynb_files):
        try:
            notebook_data = extract_code_cells(notebook_path)
            notebooks.append(notebook_data)
            
            # Print progress every 100 files
            if (i + 1) % 100 == 0:
                print(f"Processed {i + 1}/{len(ipynb_files)} notebooks")
                
        except Exception as e:
            print(f"Error processing {notebook_path}: {e}")
    
    return notebooks

def main():
    if len(sys.argv) != 2:
        print("Usage: python extract_codecells.py COMPETITION_ID")
        print("Example: python extract_codecells.py 18599")
        sys.exit(1)
    
    competition_id = sys.argv[1]
    
    try:
        notebooks = process_competition_folder(competition_id)
        
        # Save to JSON file
        output_file = f"{competition_id}_cells.json"
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(notebooks, f, indent=2)
        
        print(f"Successfully extracted cells from {len(notebooks)} notebooks.")
        print(f"Results saved to {output_file}")
        
    except Exception as e:
        print(f"Error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()