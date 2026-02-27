import json
import numpy as np
import pandas as pd

from kaggle_types import ClusterColumns, KernelColumns

def apply_safe(func):
    return lambda x: np.nan if not is_valid_val(x) else func(x)

def json_dumps_safe(l):
    return json.dumps(l) if is_valid_val(l) else ""
    

def is_valid_val(x):
    if x is None:
        return False
    if isinstance(x, (np.ndarray, list, dict, pd.Series)):
        return True
    return not pd.isna(x)

def str_to_np(str):
    return np.array(json.loads(str))

KERNEL_NP_COLUMNS = [
    KernelColumns.LABEL_SEQUENCE,
    KernelColumns.LABEL_STATS_NORM, 
    KernelColumns.COMPLEXITY_FEATURES_NORM, 
    KernelColumns.TRANSITION_MATRIX, 
    KernelColumns.TRANSITION_MATRIX_NORM
]
KERNEL_JSON_COLUMNS = KERNEL_NP_COLUMNS + [ 
    KernelColumns.LABEL_STATS,
    KernelColumns.N_GRAMS
]

KERNEL_JSON_CONVERTERS = {
    KernelColumns.LABEL_SEQUENCE: str_to_np,
    KernelColumns.TRANSITION_MATRIX: str_to_np,
    KernelColumns.TRANSITION_MATRIX_NORM: str_to_np,
    KernelColumns.LABEL_STATS_NORM: str_to_np,
    KernelColumns.COMPLEXITY_FEATURES_NORM: str_to_np,
    KernelColumns.N_GRAMS: lambda str: set(json.loads(str)),
    KernelColumns.LABEL_STATS: json.loads
}

CLUSTER_NP_COLUMNS = [
    ClusterColumns.TRANSITION_MATRIX
]

CLUSTER_JSON_COLUMNS = CLUSTER_NP_COLUMNS + [
    ClusterColumns.LABEL_STATS
]

CLUSTER_JSON_CONVERTERS = KERNEL_JSON_CONVERTERS

def load_all_kernels(filename: str) -> pd.DataFrame:
    kernels =  pd.read_csv(filename,
        dtype={
            KernelColumns.KERNEL_VERSION_ID: "Int32",
            KernelColumns.SOURCE_COMPETITION_ID: "Int32",
            KernelColumns.CLUSTER_ID: "Int32"
        }
    )
    for col, converter in KERNEL_JSON_CONVERTERS.items():
        if col in kernels.columns:
            kernels[col] = kernels[col].apply(apply_safe(converter))
            
    return kernels
    
def dump_kernels_to_python(kernels: pd.DataFrame):
    for col_name in KERNEL_NP_COLUMNS:
        if (col_name in kernels.columns):
            kernels[col_name] = kernels[col_name].apply(apply_safe(lambda l: l.tolist()))
    
    if (KernelColumns.N_GRAMS in kernels.columns):        
        kernels[KernelColumns.N_GRAMS] = kernels[KernelColumns.N_GRAMS].apply(apply_safe(list))
    
def dump_kernels(kernels: pd.DataFrame):
    dump_kernels_to_python(kernels)
    for col_name in KERNEL_JSON_COLUMNS:
        if (col_name in kernels.columns):
            kernels[col_name] = kernels[col_name].apply(json_dumps_safe)
    
def save_kernels(kernels: pd.DataFrame, filename: str):    
    dump_kernels(kernels)
    
    kernels.to_csv(filename, index=False)
    
def load_clusters(filename: str):
    clusters =  pd.read_csv(filename,
        dtype={
            KernelColumns.CLUSTER_ID: "Int32",
            ClusterColumns.LOCAL_CLUSTER_ID: "Int32"
        }
    )
    for col, converter in CLUSTER_JSON_CONVERTERS.items():
        if col in clusters.columns:
            clusters[col] = clusters[col].apply(apply_safe(converter))
            
    return clusters
    
def save_clusters(clusters: pd.DataFrame, filename: str):
    clusters = clusters.copy()
    for col in CLUSTER_NP_COLUMNS:
        if col in clusters.columns:
            clusters[col] = clusters[col].apply(apply_safe(lambda l: l.tolist()))
            
    for col in CLUSTER_JSON_COLUMNS:
        if col in clusters.columns:
            clusters[col] = clusters[col].apply(json_dumps_safe)
            
    clusters.to_csv(filename, index=False)
    
    pass