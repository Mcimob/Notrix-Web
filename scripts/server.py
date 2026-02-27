import os
from fastapi import FastAPI, BackgroundTasks
from pydantic import BaseModel
import uuid
import pandas as pd
from competition_cells.extract_cells import extract_cells_from_dict
from kaggle_types import KernelColumns
from pd_utils import dump_kernels_to_python
from predict import predcit_cells
from stats import add_stats_to_cells, add_stats_to_kernels

os.environ["TOKENIZERS_PARALLELISM"] = "true"

app = FastAPI()

PROGRESS = {}

class Cell(BaseModel):
    cell_type: str
    source: list[str]

class Notebook(BaseModel):
    cells: list[Cell]
    

@app.post("/process_notebook")
def start_process_notebook(notebook: Notebook, background_tasks: BackgroundTasks):
    job_id = str(uuid.uuid4())
    PROGRESS[job_id] = {
        "status": "INIT",
        "result": None
    }
    background_tasks.add_task(process_notebook, job_id, notebook)
    return {"job_id": job_id}

@app.get("/process_notebook/{job_id}")
def get_status(job_id: str):
    result = PROGRESS.get(job_id, {"error": "job not found"})
    if job_id in PROGRESS.keys() and result["status"] == "DONE":
        del PROGRESS[job_id]
    return result

def process_notebook(job_id: str, notebook: Notebook):
    print(f"{job_id}: Starting progress")
    
    print(f"{job_id}: Extracting Cells")
    PROGRESS[job_id]["status"] = "EXTRACTING_CELLS"
    notebook = notebook.model_dump()
    cells = pd.DataFrame(extract_cells_from_dict(notebook, 0))
    cells[KernelColumns.KERNEL_VERSION_ID] = 0
    
    print(f"{job_id}: Predicting Cells")
    PROGRESS[job_id]["status"] = "PREDICTING_CELLS"
    predcit_cells(cells)
    
    print(f"{job_id}: Adding stats")
    PROGRESS[job_id]["status"] = "STATS"
    cells = add_stats_to_cells(cells)
    
    kernels = pd.DataFrame({KernelColumns.KERNEL_VERSION_ID: 0}, index=[0])
    kernels = add_stats_to_kernels(cells, kernels)
    
    dump_kernels_to_python(kernels)
    
    print(f"{job_id}: Finished processing Notebook")
    kernel_dict = kernels.to_dict("records")[0]
    kernel_dict["cells"] = cells.to_dict("records")
    PROGRESS[job_id]["result"] = kernel_dict
    PROGRESS[job_id]["status"] = "DONE"