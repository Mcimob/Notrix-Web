import json
import numpy as np
import pandas as pd

from kaggle_types import KernelColumns

def apply_safe(func):
    return lambda x: np.nan if not is_valid_val(x) else func(x)

def is_valid_val(x):
    return type(x) in [np.ndarray, list, dict, pd.Series] or not pd.isna(x)

def str_to_np(str):
    return np.array(json.loads(str))

NP_COLUMNS = [
    KernelColumns.LABEL_SEQUENCE,
    KernelColumns.LABEL_STATS_NORM, 
    KernelColumns.COMPLEXITY_FEATURES_NORM, 
    KernelColumns.TRANSITION_MATRIX, 
    KernelColumns.TRANSITION_MATRIX_NORM
]
JSON_COLUMNS = NP_COLUMNS + [ 
    KernelColumns.LABEL_STATS,
    KernelColumns.N_GRAMS
]

JSON_CONVERTERS = {
    KernelColumns.LABEL_SEQUENCE: str_to_np,
    KernelColumns.TRANSITION_MATRIX: str_to_np,
    KernelColumns.TRANSITION_MATRIX_NORM: str_to_np,
    KernelColumns.LABEL_STATS_NORM: str_to_np,
    KernelColumns.COMPLEXITY_FEATURES_NORM: str_to_np,
    KernelColumns.N_GRAMS: lambda str: set(json.loads(str)),
    KernelColumns.LABEL_STATS: json.loads
}

def load_all_kernels(filename: str) -> pd.DataFrame:
    kernels =  pd.read_csv(filename,
        dtype={
            KernelColumns.KERNEL_VERSION_ID: "Int32",
            KernelColumns.SOURCE_COMPETITION_ID: "Int32",
        }
    )
    for col, converter in JSON_CONVERTERS.items():
        if col in kernels.columns:
            kernels[col] = kernels[col].apply(apply_safe(converter))
            
    return kernels
    
    
def save_kernels(kernels: pd.DataFrame, filename: str):    
    for col_name in NP_COLUMNS:
        if (col_name in kernels.columns):
            kernels[col_name] = kernels[col_name].apply(apply_safe(lambda l: l.tolist()))
    
    if (KernelColumns.N_GRAMS in kernels.columns):        
        kernels[KernelColumns.N_GRAMS] = kernels[KernelColumns.N_GRAMS].apply(apply_safe(list))
    
    for col_name in JSON_COLUMNS:
        if (col_name in kernels.columns):
            kernels[col_name] = kernels[col_name].apply(lambda l: json.dumps(l) if is_valid_val(l) else "")
    
    kernels.to_csv(filename, index=False)