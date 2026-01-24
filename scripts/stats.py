import pandas as pd
import numpy as np
import json
from collections import Counter

FILE_BASE = "/media/tim/Data/Thesis/"
MAX_LABEL = 13

KERNEL_VERSION_ID = "KernelVersionId"
SOURCE_COMPETITION_ID = "SourceCompetitionId"

class CellColumns:
    MAIN_LABEL = "MainLabel"
    SOURCE_LINES_COUNT = "SourceLineCount"
class KernelColumns:
    LABEL_STATS = "MainLabelStats"
    LABEL_SEQUENCE = "LabelSequence"
    TRANSITION_MATRIX = "TransitionMatrix"
    NUM_LINES = "NumLines"
    CELL_COUNT = "CellCount"
    TOTAL_VOTES = "TotalVotes"

class CompetitionColumns:
    LABEL_STATS = KernelColumns.LABEL_STATS
    TRANSITION_MATRIX = KernelColumns.TRANSITION_MATRIX
    AVG_LINES = "AvgLinesPerKernel"
    AVG_CELLS = "AvgCellsPerKernel"
    AVG_TOTAL_VOTES = "AvgTotalVotes"

def add_stats_to_cells(cells: pd.DataFrame) -> pd.DataFrame:
    cells['SourceLineCount'] = (
        cells['Source']
        .fillna('')
        .str.count('\n')
        .add(1)
    )
    
    return cells

def add_stats_to_kernels(cells: pd.DataFrame, kernels: pd.DataFrame) -> pd.DataFrame:
    label_stats = get_kernel_label_stats(cells)
    label_sequences = get_kernel_label_sequences(cells)
    label_transitions = get_kernel_label_transition_stats(label_sequences)
    line_counts = get_kernel_num_lines(cells)
    cell_counts = get_kernel_num_cells(cells)
    
    for df, col_name, lmda in [
        (label_stats, KernelColumns.LABEL_STATS, fillna_dict), 
        (label_sequences, KernelColumns.LABEL_SEQUENCE, fillna_list), 
        (label_transitions, KernelColumns.TRANSITION_MATRIX, fillna_matrix), 
        (line_counts, KernelColumns.NUM_LINES, fillna_zero), 
        (cell_counts, KernelColumns.CELL_COUNT, fillna_zero)]:
        kernels = kernels.merge(
            df,
            on=KERNEL_VERSION_ID,
            how="left"
        )
        kernels[col_name] = (
            kernels[col_name]
            .apply(lmda)
        )
    
    return kernels
    
def add_stats_to_competitions(kernels: pd.DataFrame, competitions: pd.DataFrame) -> pd.DataFrame:
    label_stats = get_competition_label_stats(kernels)
    label_transitions = get_competition_label_transition_stats(kernels)
    avg_cells = get_competition_avg_cells(kernels)
    avg_lines = get_competition_avg_num_lines(kernels)
    avg_total_votes = get_competition_avg_votes(kernels)

    for df, col_name, lmda in [
        (label_stats, CompetitionColumns.LABEL_STATS, fillna_dict),
        (label_transitions, CompetitionColumns.TRANSITION_MATRIX, fillna_matrix),
        (avg_cells, CompetitionColumns.AVG_CELLS, fillna_zero),
        (avg_lines, CompetitionColumns.AVG_LINES, fillna_zero),
        (avg_total_votes, CompetitionColumns.AVG_TOTAL_VOTES, fillna_zero)]:
        competitions = competitions.merge(
            df,
            left_on="Id",
            right_on=SOURCE_COMPETITION_ID,
            how="left"
        )
        competitions.drop(columns=[SOURCE_COMPETITION_ID], inplace=True)
        competitions[col_name] = (
            competitions[col_name]
            .apply(lmda)
        )
    
    return competitions

def get_kernel_label_stats(cells: pd.DataFrame) -> pd.DataFrame:
    return (
        cells
        .groupby(KERNEL_VERSION_ID)[CellColumns.MAIN_LABEL]
        .value_counts()
        .unstack(fill_value=0)
        .apply(
            lambda r: {str(k): int(v) for k, v in r.items() if v > 0},
            axis=1
        )
        .reset_index(name=KernelColumns.LABEL_STATS)
    )

def get_kernel_label_sequences(cells: pd.DataFrame) -> pd.DataFrame:
    cells_valid = cells.dropna(subset=[CellColumns.MAIN_LABEL])

    cells_sorted = cells_valid.sort_values([KERNEL_VERSION_ID, "CellId"])

    is_duplicate = cells_sorted[CellColumns.MAIN_LABEL] == cells_sorted.groupby(KERNEL_VERSION_ID)[CellColumns.MAIN_LABEL].shift()
    
    # Keep the first row of each kernel and remove other consecutive duplicates
    cells_dedup = cells_sorted[~is_duplicate | cells_sorted.groupby(KERNEL_VERSION_ID).cumcount().eq(0)]
    
    # Build sequence per kernel
    kernel_sequences = (
        cells_dedup
        .groupby(KERNEL_VERSION_ID)[CellColumns.MAIN_LABEL]
        .agg(list)
        .reset_index(name=KernelColumns.LABEL_SEQUENCE)
    )

    kernel_sequences[KernelColumns.LABEL_SEQUENCE] = kernel_sequences[KernelColumns.LABEL_SEQUENCE].apply(lambda lst: [int(x) for x in lst])
    
    return kernel_sequences

def get_kernel_label_transition_stats(label_sequences: pd.DataFrame) -> dict[int, list[list[int]]]:
    label_sequences = label_sequences[
        label_sequences[KernelColumns.LABEL_SEQUENCE].apply(
            lambda x: isinstance(x, (list, tuple)) and len(x) > 1
        )
    ].copy()
    
    label_sequences["Transitions"] = label_sequences[KernelColumns.LABEL_SEQUENCE].apply(
        lambda x: list(zip(x[:-1], x[1:]))
    )
    
    transitions = label_sequences.explode("Transitions").dropna(subset=["Transitions"])
    transitions[["From", "To"]] = pd.DataFrame(
        transitions["Transitions"].tolist(), index=transitions.index
    )
    
    stats = (
        transitions
        .groupby([KERNEL_VERSION_ID, "From", "To"])
        .size()
        .reset_index(name="Count")
    )
    
    dense_matrices_kernel = {}
    for kernel_id, g in stats.groupby(KERNEL_VERSION_ID):
        mat = np.zeros((MAX_LABEL, MAX_LABEL), dtype=int)
        rows = g[["From", "To", "Count"]].astype(int).values
        for f, t, c in rows:
            mat[f][t] = c.item()
        dense_matrices_kernel[kernel_id] = mat.tolist()

    # convert to kernel-level DataFrame
    return pd.DataFrame({
        KERNEL_VERSION_ID: list(dense_matrices_kernel.keys()),
        KernelColumns.TRANSITION_MATRIX: list(dense_matrices_kernel.values())
    })
    
def get_competition_label_transition_stats(kernels: pd.DataFrame) -> dict[int, list[list[int]]]:    
    kernels = kernels.copy()
    kernels["TransitionMatrixArr"] = kernels[KernelColumns.TRANSITION_MATRIX].apply(lambda x: np.array(x, dtype=int))

    comp_df = (
        kernels
        .groupby(SOURCE_COMPETITION_ID)["TransitionMatrixArr"]
        .apply(lambda arrs: sum(arrs))  # element-wise sum of arrays
        .reset_index()
    )

    comp_df[CompetitionColumns.TRANSITION_MATRIX] = comp_df["TransitionMatrixArr"].apply(lambda x: x.tolist())
    comp_df = comp_df.drop(columns="TransitionMatrixArr")
    
    return comp_df

def get_competition_label_stats(kernels: pd.DataFrame) -> pd.DataFrame:

    competition_label_stats = (
        kernels
        .groupby(SOURCE_COMPETITION_ID)[KernelColumns.LABEL_STATS]
        .apply(lambda s: dict(sum((Counter(d) for d in s if isinstance(d, dict)), Counter())))
        .unstack(fill_value=0)
        .apply(
            lambda r: {str(k): int(v) for k, v in r.items() if v > 0},
            axis=1
        )
        .reset_index(name=CompetitionColumns.LABEL_STATS)
    )
    
    return competition_label_stats

def get_kernel_num_cells(cells: pd.DataFrame) -> pd.DataFrame:
    return (
        cells
        .groupby(KERNEL_VERSION_ID)
        .size()
        .reset_index(name=KernelColumns.CELL_COUNT)
    )
    
def get_kernel_num_lines(cells: pd.DataFrame) -> pd.DataFrame:
    return (
        cells
        .groupby(KERNEL_VERSION_ID)[CellColumns.SOURCE_LINES_COUNT]
        .sum()
        .reset_index(name=KernelColumns.NUM_LINES)
    )

def get_competition_avg_cells(kernels: pd.DataFrame) -> pd.DataFrame:
    return (
        kernels
        .groupby(SOURCE_COMPETITION_ID)[KernelColumns.CELL_COUNT]
        .mean()
        .reset_index(name=CompetitionColumns.AVG_CELLS)
    )

def get_competition_avg_num_lines(kernels: pd.DataFrame) -> pd.DataFrame:
    return (
        kernels
        .groupby(SOURCE_COMPETITION_ID)[KernelColumns.NUM_LINES]
        .mean()
        .reset_index(name=CompetitionColumns.AVG_LINES)
    )

def get_competition_avg_votes(kernels: pd.DataFrame) -> pd.DataFrame:
    return (
        kernels
        .groupby(SOURCE_COMPETITION_ID)[KernelColumns.TOTAL_VOTES]
        .mean()
        .reset_index(name=CompetitionColumns.AVG_TOTAL_VOTES)
    )

def fillna_list(x):
    return x if isinstance(x, list) else []

def fillna_dict(x):
    return x if isinstance(x, dict) else {}

def fillna_matrix(x):
    return x if isinstance(x, list) else [[0] * MAX_LABEL for _ in range(MAX_LABEL)]

def fillna_zero(x):
    return x if pd.notna(x) else 0

def main():
    print("Reading CSV files...")
    cells = pd.read_csv(FILE_BASE + "Cells_predicted.csv", dtype={CellColumns.MAIN_LABEL: "Int32"})
    kernels = pd.read_csv("AllCompetitionKernels.csv")
    competitions = pd.read_csv("Competitions.csv")
    print("Finished reading CSV files")

    print("=" * 30)

    print("Adding stats to DFs...")
    cells = add_stats_to_cells(cells)
    kernels = add_stats_to_kernels(cells, kernels)
    competitions = add_stats_to_competitions(kernels, competitions)
    print("Finished adding stats to DFs")
    
    print("=" * 30)
    
    print("Dumping JSON columns...")
    for col_name in [KernelColumns.TRANSITION_MATRIX, KernelColumns.LABEL_SEQUENCE, KernelColumns.LABEL_STATS]:
        kernels[col_name] = kernels[col_name].apply(json.dumps)
    
    for col_name in [CompetitionColumns.LABEL_STATS, CompetitionColumns.TRANSITION_MATRIX]:
        competitions[col_name] = competitions[col_name].apply(json.dumps)
    print("Finished dumping JSON columns")
    
    print("=" * 30)
    
    print("Saving CSVs...")
    competitions.to_csv("Competitions_stats_tmp.csv", index=False)
    kernels.to_csv("AllCompetitionKernels_tmp.csv", index=False)
    cells.to_csv(FILE_BASE + "Cells_predicted_tmp.csv", index=False)
    print("Finished saving CSVs")
    

if __name__ == "__main__":
    main()