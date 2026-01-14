import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification
from torch.utils.data import Dataset, DataLoader
import pandas as pd
from tqdm import tqdm

FILE_BASE = "/media/tim/Data/Thesis/"

MODEL_ID = "SShiny/Notrix"
DEVICE = 'cuda' if torch.cuda.is_available() else 'cpu'

class CodeCellDataset(Dataset):
    def __init__(self, cells: list[str], tokenizer, max_length=512):
        self.cells = cells
        self.tokenizer = tokenizer
        self.max_length = max_length

    def __len__(self):
        return len(self.cells)

    def __getitem__(self, idx):
        return self.cells[idx].replace("</s>", "<slash_s>")

class Predictor():

    def __init__(self):
        self.tokenizer = AutoTokenizer.from_pretrained(MODEL_ID)
        self.model = AutoModelForSequenceClassification.from_pretrained(MODEL_ID, num_labels=13, local_files_only=True)
        self.model.to(DEVICE)
        
    def prefict(self, code_text: list[str]) -> list[int]:
        dataset = CodeCellDataset(code_text, self.tokenizer)
        
        dataloader = DataLoader(dataset, 
                                batch_size=32, 
                                collate_fn=self.collate_fn,
                                num_workers=4,
                                pin_memory=True)
        preds = []
        for batch in tqdm(dataloader, unit=" batches"):
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

def main():
    print("Reading Cells.csv ...")
    cells = pd.read_csv(FILE_BASE + "Cells.csv")
    code_mask = cells["CellType"] == 0
    code_cells = cells.loc[code_mask, "Source"].astype("str")
    
    predictor = Predictor()
    preds = predictor.prefict(code_cells.to_list())
    
    cells.loc[code_mask, "MainLabel"] = preds
    print("Saving Cells_predicted.csv ...")
    cells.to_csv(FILE_BASE + "Cells_predicted.csv", index=False)
    

if __name__ == "__main__":
    main()