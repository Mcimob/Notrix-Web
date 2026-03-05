import os
from fastapi import FastAPI, BackgroundTasks
from pydantic import BaseModel
import uuid
import pandas as pd
from analyze_clusters import add_cluster_data
from competition_cells.extract_cells import extract_cells_from_dict
from kaggle_types import ClusterColumns, CompetitionColumns, KernelColumns
from pd_utils import dump_clusters_to_python, dump_competitions_to_python, dump_kernels_to_python
from predict import predcit_cells
from stats import add_stats_to_cells, add_stats_to_competitions, add_stats_to_kernels
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
    title: str
    
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
    names = list(map(lambda n: n["title"], notebooks))
    
    print(f"{job_id}: Predicting Cells")
    COMPETITION_PROGRESS[job_id]["status"] = "PREDICTING_CELLS"
    predcit_cells(cells)
    
    print(f"{job_id}: Adding stats")
    COMPETITION_PROGRESS[job_id]["status"] = "STATS"
    cells = add_stats_to_cells(cells)
    
    kernels = pd.DataFrame({KernelColumns.KERNEL_VERSION_ID: ids, KernelColumns.TITLE: names}, index=ids)
    kernels = add_stats_to_kernels(cells, kernels)
    kernels[KernelColumns.SOURCE_COMPETITION_ID] = 0
    kernels[KernelColumns.TOTAL_VOTES] = 0
    
    competition = pd.DataFrame({"Id": 0}, index=[0])
    competition = add_stats_to_competitions(kernels, competition)
    
    print(f"{job_id}: Clustering notebooks")
    COMPETITION_PROGRESS[job_id]["status"] = "CLUSTERING"
    kernels, clusters = add_clusters_to_kernels(kernels)
    
    print(f"{job_id}: Adding GPT analysis")
    COMPETITION_PROGRESS[job_id]["status"] = "GPT"
    clusters = add_cluster_data(kernels, clusters)
    clusters.fillna({ClusterColumns.SUMMARY: ""}, inplace=True)
    
    dump_kernels_to_python(kernels)
    dump_clusters_to_python(clusters)
    dump_competitions_to_python(competition)
    
    print(f"{job_id}: Finished processing Notebook")
    
    kernel_base_cols = [
        KernelColumns.KERNEL_VERSION_ID,
        KernelColumns.CLUSTER_ID
    ]
    kernel_detail_cols = [
        c for c in kernels.columns
        if c not in kernel_base_cols
    ]
    
    kernels_merged = (kernels
        .merge(cells,
            on=KernelColumns.KERNEL_VERSION_ID,
            how="left")
        .groupby(kernel_base_cols, as_index=False)
        .apply(lambda g: pd.Series({
            KernelColumns.KERNEL_VERSION_ID: g.name[0],
            KernelColumns.CLUSTER_ID: g.name[1],
            "kernel": {
                **g.iloc[0][kernel_detail_cols].to_dict(),
                "cells": g[cells.columns].to_dict("records")
            }
        }))
        .reset_index(drop=True)
    )
    
    clusters_merged = (clusters
        .merge(kernels_merged,
               on=ClusterColumns.CLUSTER_ID,
               how="left")
        .groupby(ClusterColumns.CLUSTER_ID)
        .apply(lambda g: pd.Series({
            **g.iloc[0][[c for c in clusters.columns if c != ClusterColumns.CLUSTER_ID]].to_dict(),
            "kernels": g["kernel"].tolist()
        }))
    )
    
    cluster_list = clusters_merged.to_dict("records")
    
    competition_dict = competition.to_dict("records")[0]
    competition_dict["clusters"] = cluster_list
    COMPETITION_PROGRESS[job_id]["result"] = competition_dict
    COMPETITION_PROGRESS[job_id]["status"] = "DONE"