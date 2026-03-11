import argparse

import torch
from tqdm import tqdm
from transformers import AutoTokenizer, AutoModelForSequenceClassification
from torch.utils.data import Dataset, DataLoader
import pandas as pd
import multiprocessing as mp

from app.pd_utils import save_cells

mp.set_start_method('spawn', force=True)

MODEL_ID = "SShiny/Notrix"
DEVICE = 'cuda' if torch.cuda.is_available() else 'cpu'

class Predictor():

    def __init__(self):
        self.tokenizer = AutoTokenizer.from_pretrained(MODEL_ID)
        self.model = AutoModelForSequenceClassification.from_pretrained(MODEL_ID, num_labels=13)
        self.model.to(DEVICE)
        
    def predict(self, code_text: list[str], show_progress: bool) -> list[int]:
        dataset = CodeCellDataset(code_text, self.tokenizer)
        
        dataloader = DataLoader(dataset, 
                                batch_size=32, 
                                collate_fn=self.collate_fn,
                                num_workers=4 if show_progress else 0,
                                pin_memory=True)
        preds = []
        for batch in tqdm(dataloader, disable=not show_progress):
            batch = {k: v.to(DEVICE) for k, v in batch.items()}
            
            with torch.no_grad():
                outputs = self.model(**batch)
                predictions = outputs.logits.argmax(dim=1)
                preds.extend(predictions.cpu().tolist())
        
        return preds
    
    def collate_fn(self, batch):
        return self.tokenizer(
            batch,
            padding=True,
            truncation=True,
            max_length=512,
            return_tensors="pt"
        )

_predictor_instance = None

def get_predictor():
    global _predictor_instance
    if _predictor_instance is None:
        print("Loading model...")
        _predictor_instance = Predictor()
        print("Model loaded")
    return _predictor_instance

class CodeCellDataset(Dataset):
    def __init__(self, cells: list[str], tokenizer, max_length=512):
        self.cells = cells
        self.tokenizer = tokenizer
        self.max_length = max_length

    def __len__(self):
        return len(self.cells)

    def __getitem__(self, idx):
        return self.cells[idx].replace("</s>", "<slash_s>")

def predcit_cells(cells, show_progress: bool):
    code_mask = cells["CellType"] == 0
    code_cells = cells.loc[code_mask, "Source"].astype("str")
    code_cells = code_cells.fillna("")
    
    predictor = get_predictor()
    preds = predictor.predict(code_cells.to_list(), show_progress)
    
    cells.loc[code_mask, "MainLabel"] = preds
    cells["MainLabel"] = cells["MainLabel"].astype("Int32")

def main():
    parser = argparse.ArgumentParser(
        prog="PredictCells",
        description="Takes the xtracted cells and predicts a label using a pretrained model",
    )
    parser.add_argument("input")
    parser.add_argument("output")
    
    args = parser.parse_args()
    
    print("Reading Cells.csv ...")
    cells = pd.read_csv(args.input)
    
    predcit_cells(cells, True)
    
    print("Saving Cells predicted...")
    save_cells(cells, args.output)

if __name__ == "__main__":
    main()