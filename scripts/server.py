import os
from fastapi import FastAPI, BackgroundTasks
from pydantic import BaseModel
import uuid
import pandas as pd
from analyze_clusters import add_cluster_data
from competition_cells.extract_cells import extract_cells_from_dict
from kaggle_types import ClusterColumns, KernelColumns
from pd_utils import dump_clusters_to_python, dump_kernels_to_python
from predict import predcit_cells
from stats import add_stats_to_cells, add_stats_to_kernels
from hmm_clustering import add_clusters_to_kernels

os.environ["TOKENIZERS_PARALLELISM"] = "true"

app = FastAPI()

NOTEBOOK_PROGRESS = {}
COMPETITION_PROGRESS = {}

class Cell(BaseModel):
    cell_type: str
    source: list[str]

class Notebook(BaseModel):
    cells: list[Cell]
    
class Competition(BaseModel):
    notebooks: list[Notebook]

@app.post("/process_notebook")
def start_process_notebook(notebook: Notebook, background_tasks: BackgroundTasks):
    job_id = str(uuid.uuid4())
    NOTEBOOK_PROGRESS[job_id] = {
        "status": "INIT",
        "result": None
    }
    background_tasks.add_task(process_notebook, job_id, notebook)
    return {"job_id": job_id}

@app.get("/process_notebook/{job_id}")
def get_status(job_id: str):
    result = NOTEBOOK_PROGRESS.get(job_id, {"error": "job not found"})
    if job_id in NOTEBOOK_PROGRESS.keys() and result["status"] == "DONE":
        del NOTEBOOK_PROGRESS[job_id]
    return result

@app.post("/process_competition")
def start_process_competition(notebooks: list[Notebook], background_tasks: BackgroundTasks):
    job_id = str(uuid.uuid4())
    COMPETITION_PROGRESS[job_id] = {
        "status": "INIT",
        "result": None
    }
    background_tasks.add_task(process_competition, job_id, notebooks)
    return {"job_id": job_id}

@app.get("/process_competition/{job_id}")
def get_competition_status(job_id: str):
    result = COMPETITION_PROGRESS.get(job_id, {"error": "job not found"})
    if job_id in COMPETITION_PROGRESS.keys() and result["status"] == "DONE":
        del COMPETITION_PROGRESS[job_id]
    return result

def process_notebook(job_id: str, notebook: Notebook):
    print(f"{job_id}: Starting progress")
    
    print(f"{job_id}: Extracting Cells")
    NOTEBOOK_PROGRESS[job_id]["status"] = "EXTRACTING_CELLS"
    notebook = notebook.model_dump()
    cells = pd.DataFrame(extract_cells_from_dict(notebook, 0))
    cells[KernelColumns.KERNEL_VERSION_ID] = 0
    
    print(f"{job_id}: Predicting Cells")
    NOTEBOOK_PROGRESS[job_id]["status"] = "PREDICTING_CELLS"
    predcit_cells(cells)
    
    print(f"{job_id}: Adding stats")
    NOTEBOOK_PROGRESS[job_id]["status"] = "STATS"
    cells = add_stats_to_cells(cells)
    
    kernels = pd.DataFrame({KernelColumns.KERNEL_VERSION_ID: 0}, index=[0])
    kernels = add_stats_to_kernels(cells, kernels)
    
    dump_kernels_to_python(kernels)
    
    print(f"{job_id}: Finished processing Notebook")
    kernel_dict = kernels.to_dict("records")[0]
    kernel_dict["cells"] = cells.to_dict("records")
    NOTEBOOK_PROGRESS[job_id]["result"] = kernel_dict
    NOTEBOOK_PROGRESS[job_id]["status"] = "DONE"
    
def process_competition(job_id: str, notebooks: list[Notebook]):
    print(f"{job_id}: Starting progress")
    
    print(f"{job_id}: Extracting Cells")
    COMPETITION_PROGRESS[job_id]["status"] = "EXTRACTING_CELLS"
    notebooks = list(map(lambda n: n.model_dump(), notebooks))
    
    cells = [extract_cells_from_dict(n, i) for i, n in enumerate(notebooks)]
    cells = pd.DataFrame([row for sub in cells for row in sub])
    
    ids = list(range(len(notebooks)))
    
    print(f"{job_id}: Predicting Cells")
    COMPETITION_PROGRESS[job_id]["status"] = "PREDICTING_CELLS"
    predcit_cells(cells)
    
    print(f"{job_id}: Adding stats")
    COMPETITION_PROGRESS[job_id]["status"] = "STATS"
    cells = add_stats_to_cells(cells)
    
    kernels = pd.DataFrame({KernelColumns.KERNEL_VERSION_ID: ids}, index=ids)
    kernels = add_stats_to_kernels(cells, kernels)
    kernels[KernelColumns.SOURCE_COMPETITION_ID] = 0
    
    print(f"{job_id}: Clustering notebooks")
    COMPETITION_PROGRESS[job_id]["status"] = "CLUSTERING"
    kernels, clusters = add_clusters_to_kernels(kernels)
    
    clusters = add_cluster_data(kernels, clusters)
    clusters.fillna({ClusterColumns.SUMMARY: ""})
    
    dump_kernels_to_python(kernels)
    dump_clusters_to_python(clusters)
    
    print(f"{job_id}: Finished processing Notebook")
    
    kernels_merged = kernels.merge(
        cells,
        on=KernelColumns.KERNEL_VERSION_ID,
        how="left"
    )

    kernel_list = (
        kernels_merged
        .groupby(KernelColumns.KERNEL_VERSION_ID)
        .apply(lambda g: {
            **g.iloc[0][kernels.columns].to_dict(),
            "cells": g[cells.columns].to_dict("records")
        })
        .tolist()
    )
    cluster_list = clusters.to_dict("records")
    COMPETITION_PROGRESS[job_id]["result"] = {
        "kernels": kernel_list,
        "clusters": cluster_list
    }
    COMPETITION_PROGRESS[job_id]["status"] = "DONE"