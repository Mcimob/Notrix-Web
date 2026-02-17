import pandas as pd
import numpy as np
import json
from collections import Counter
from pandarallel import pandarallel

from kaggle_types import CellColumns, KernelColumns, CompetitionColumns
from pd_utils import apply_safe, is_valid_val, save_kernels

pandarallel.initialize(progress_bar=True)

FILE_BASE = "/media/tim/Data/Thesis/"
MAX_LABEL = 13

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
    
    for df in [
        label_stats, 
        label_sequences, 
        label_transitions, 
        line_counts, 
        cell_counts]:
        kernels = kernels.merge(
            df,
            on=KernelColumns.KERNEL_VERSION_ID,
            how="left"
        )
    
    kernels[KernelColumns.TRANSITION_MATRIX_NORM] = (
        kernels[KernelColumns.TRANSITION_MATRIX]
        .apply(apply_safe(np.array))
        .apply(apply_safe(normalize_transition_matrix)))
    kernels[KernelColumns.LABEL_STATS_NORM] = kernels[KernelColumns.LABEL_STATS].apply(apply_safe(normalize_stats))
    kernels[KernelColumns.COMPLEXITY_FEATURES_NORM] = (
        kernels[[KernelColumns.LABEL_SEQUENCE, KernelColumns.TRANSITION_MATRIX_NORM, KernelColumns.LABEL_STATS_NORM]]
        .parallel_apply(compute_complexity_features, axis=1)
        .apply(apply_safe(lambda row: row / (np.linalg.norm(row) + 1e-10))))
    kernels[KernelColumns.N_GRAMS] = kernels[KernelColumns.LABEL_SEQUENCE].apply(apply_safe(compute_ngrams))
    
    return kernels
    
def add_stats_to_competitions(kernels: pd.DataFrame, competitions: pd.DataFrame) -> pd.DataFrame:
    label_stats = get_competition_label_stats(kernels)
    label_transitions = get_competition_label_transition_stats(kernels)
    avg_cells = get_competition_avg_cells(kernels)
    avg_lines = get_competition_avg_num_lines(kernels)
    avg_total_votes = get_competition_avg_votes(kernels)

    for df in [
        label_stats,
        label_transitions,
        avg_cells,
        avg_lines,
        avg_total_votes]:
        competitions = competitions.merge(
            df,
            left_on="Id",
            right_on=KernelColumns.SOURCE_COMPETITION_ID,
            how="left"
        )
        competitions.drop(columns=[KernelColumns.SOURCE_COMPETITION_ID], inplace=True)
    
    return competitions

def get_kernel_label_stats(cells: pd.DataFrame) -> pd.DataFrame:
    return (
        cells
        .groupby(KernelColumns.KERNEL_VERSION_ID)[CellColumns.MAIN_LABEL]
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

    cells_sorted = cells_valid.sort_values([KernelColumns.KERNEL_VERSION_ID, "CellId"])
    
    # Build sequence per kernel
    kernel_sequences = (
        cells_sorted
        .groupby(KernelColumns.KERNEL_VERSION_ID)[CellColumns.MAIN_LABEL]
        .apply(lambda x: x.to_numpy(dtype=np.int32))
        .reset_index(name=KernelColumns.LABEL_SEQUENCE)
    )
    
    return kernel_sequences

def get_kernel_label_transition_stats(label_sequences: pd.DataFrame) -> pd.DataFrame:
    label_sequences = label_sequences[
        label_sequences[KernelColumns.LABEL_SEQUENCE].apply(
            lambda x: isinstance(x, (list, tuple, np.ndarray)) and len(x) > 1
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
        .groupby([KernelColumns.KERNEL_VERSION_ID, "From", "To"])
        .size()
        .reset_index(name="Count")
    )
    
    dense_matrices_kernel = {}
    for kernel_id, g in stats.groupby(KernelColumns.KERNEL_VERSION_ID):
        mat = np.zeros((MAX_LABEL, MAX_LABEL), dtype=int)
        rows = g[["From", "To", "Count"]].astype(int).values
        for f, t, c in rows:
            mat[f][t] = c.item()
        dense_matrices_kernel[kernel_id] = mat.astype(int)

    # convert to kernel-level DataFrame
    return pd.DataFrame({
        KernelColumns.KERNEL_VERSION_ID: list(dense_matrices_kernel.keys()),
        KernelColumns.TRANSITION_MATRIX: list(dense_matrices_kernel.values())
    })
    
def normalize_transition_matrix(m: np.array) -> np.array:
    """Normalize TransitionMatrix"""

    m = np.array(m)
    row_sums = m.sum(axis=1, keepdims=True)

    total_sum = row_sums.sum()
    if total_sum == 0:
        return m
    row_sums[row_sums == 0] = 1        
    
    smoothing = 1e-6#self.config['processing']['laplace_smoothing']
    smoothed = m + smoothing
    normalized = smoothed / row_sums
    
    return normalized

def normalize_stats(stats: dict[str, int]) -> np.array:
    stats_array = np.zeros(13)
    for key, value in stats.items():
        stats_array[int(key)] = value
        
    normalized = stats_array / stats_array.sum()
    return normalized
    
def compute_complexity_features(row):
    sequence = row[KernelColumns.LABEL_SEQUENCE]
    matrix = row[KernelColumns.TRANSITION_MATRIX_NORM]
    stats_normalized = row[KernelColumns.LABEL_STATS_NORM]
    if (not (is_valid_val(sequence) and is_valid_val(matrix) and is_valid_val(stats_normalized))):
        return np.nan
    if len(sequence) == 0:
        return np.nan
    
    features = []
    
    # 1. Sequence length (normalized)
    features.append(len(sequence))
    
    # 2. Number of unique states
    unique_states = len(np.unique(sequence))
    features.append(unique_states)
    
    # 3. State diversity ratio (unique states / total length)
    state_diversity = unique_states / len(sequence)
    features.append(state_diversity)
    
    # 4. Entropy of state distribution
    epsilon = 1e-10#self.config['processing']['entropy_epsilon']
    entropy = -np.sum(stats_normalized * np.log(stats_normalized + epsilon))
    features.append(entropy)
    
    # 5. Number of state changes
    state_changes = np.sum(sequence[:-1] != sequence[1:])
    features.append(state_changes)
    
    # 6. Change ratio (state changes / total length)
    change_ratio = state_changes / len(sequence) if len(sequence) > 0 else 0
    features.append(change_ratio)
    
    # 7. Average run length (consecutive same states)
    run_lengths = []
    current_run = 1
    for i in range(1, len(sequence)):
        if sequence[i] == sequence[i-1]:
            current_run += 1
        else:
            run_lengths.append(current_run)
            current_run = 1
    run_lengths.append(current_run)
    avg_run_length = np.mean(run_lengths) if run_lengths else 1
    features.append(avg_run_length)
    
    # 8. Maximum run length
    max_run_length = np.max(run_lengths) if run_lengths else 1
    features.append(max_run_length)
    
    # 9. Run length variance
    run_length_var = np.var(run_lengths) if len(run_lengths) > 1 else 0
    features.append(run_length_var)
    
    # 10. Pattern complexity (Lempel-Ziv complexity approximation)
    pattern_complexity = compute_pattern_complexity(sequence)
    features.append(pattern_complexity)
    
    # 11. Repetition score (how repetitive the sequence is)
    repetition_score = compute_repetition_score(sequence)
    features.append(repetition_score)
    
    # 12. Transition entropy
    if matrix is not None:
        epsilon = 1e-10#self.config['processing']['entropy_epsilon']
        trans_entropy = -np.sum(matrix * np.log(matrix + epsilon))
    else:
        trans_entropy = 0
    features.append(trans_entropy)
    
    # 13. Self-transition ratio
    self_transitions = np.sum(sequence[:-1] == sequence[1:])
    self_transition_ratio = self_transitions / len(sequence) if len(sequence) > 0 else 0
    features.append(self_transition_ratio)
    
    # 14. Periodicity score (how often elements repeat after a fixed distance/period)
    periodicity = compute_periodicity(sequence)
    features.append(periodicity)
    
    # 15. Workflow phase dominance (most frequent state ratio)
    most_frequent_ratio = np.max(stats_normalized) if len(stats_normalized) > 0 else 0
    features.append(most_frequent_ratio)
    
    return features
    
def compute_ngrams(sequence):
    
    def get_ngrams(s, n=2):
        if len(s) < n:
            return [str(s)]
        return [str(s[i:i+n]) for i in range(len(s) - n + 1)]
    
    return set([x for l in [get_ngrams(sequence, n) for n in [2, 3]] for x in l])
    
def compute_pattern_complexity(sequence):
    """Approximation of Lempel-Ziv complexity"""
    if len(sequence) <= 1:
        return 0
    
    substrings = set()
    for i in range(len(sequence)):
        for j in range(i+1, len(sequence)+1):
            substrings.add(tuple(sequence[i:j]))
    
    return len(substrings) / (len(sequence) ** 2)
    
def compute_repetition_score(sequence):
    """Compute how repetitive the sequence is"""
    if len(sequence) <= 2:
        return 0
    
    # Look for repeated subsequences
    max_repetition = 0
    for length in range(1, len(sequence) // 2 + 1):
        for start in range(len(sequence) - length + 1):
            pattern = tuple(sequence[start:start + length])
            count = 0
            for i in range(len(sequence) - length + 1):
                if tuple(sequence[i:i + length]) == pattern:
                    count += 1
            repetition_ratio = (count * length) / len(sequence)
            max_repetition = max(max_repetition, repetition_ratio)
    
    return max_repetition
    
def compute_periodicity(sequence):
    """Compute periodicity of the sequence"""
    if len(sequence) <= 2:
        return 0
    
    max_periodicity = 0
    for period in range(1, len(sequence) // 2 + 1):
        matches = 0
        comparisons = 0
        for i in range(len(sequence) - period):
            if sequence[i] == sequence[i + period]:
                matches += 1
            comparisons += 1
        
        if comparisons > 0:
            periodicity = matches / comparisons
            max_periodicity = max(max_periodicity, periodicity)
    
    return max_periodicity
    
def get_competition_label_transition_stats(kernels: pd.DataFrame) -> dict[int, list[list[int]]]:    
    kernels = kernels.copy()

    def sum_matrices(arrs: pd.Series):
        arrs = arrs.dropna()
        if len(arrs) == 0:
            return np.nan
        stacked = np.stack(arrs.values)
        return np.nan_to_num(stacked).sum(axis=0).astype(int)


    comp_df = (
        kernels
        .groupby(KernelColumns.SOURCE_COMPETITION_ID)[KernelColumns.TRANSITION_MATRIX]
        .apply(apply_safe(sum_matrices))
        .reset_index()
    )
    
    return comp_df

def get_competition_label_stats(kernels: pd.DataFrame) -> pd.DataFrame:

    competition_label_stats = (
        kernels
        .groupby(KernelColumns.SOURCE_COMPETITION_ID)[KernelColumns.LABEL_STATS]
        .apply(lambda s: dict(sum((Counter(d) for d in s if isinstance(d, dict)), Counter())))
        .unstack(fill_value=0)
        .apply(
            apply_safe(lambda r: {str(k): int(v) for k, v in r.items() if v > 0}),
            axis=1
        )
        .reset_index(name=CompetitionColumns.LABEL_STATS)
    )
    
    return competition_label_stats

def get_kernel_num_cells(cells: pd.DataFrame) -> pd.DataFrame:
    return (
        cells
        .groupby(KernelColumns.KERNEL_VERSION_ID)
        .size()
        .reset_index(name=KernelColumns.CELL_COUNT)
    )
    
def get_kernel_num_lines(cells: pd.DataFrame) -> pd.DataFrame:
    return (
        cells
        .groupby(KernelColumns.KERNEL_VERSION_ID)[CellColumns.SOURCE_LINES_COUNT]
        .sum()
        .reset_index(name=KernelColumns.NUM_LINES)
    )

def get_competition_avg_cells(kernels: pd.DataFrame) -> pd.DataFrame:
    return (
        kernels
        .groupby(KernelColumns.SOURCE_COMPETITION_ID)[KernelColumns.CELL_COUNT]
        .mean()
        .reset_index(name=CompetitionColumns.AVG_CELLS)
    )

def get_competition_avg_num_lines(kernels: pd.DataFrame) -> pd.DataFrame:
    return (
        kernels
        .groupby(KernelColumns.SOURCE_COMPETITION_ID)[KernelColumns.NUM_LINES]
        .mean()
        .reset_index(name=CompetitionColumns.AVG_LINES)
    )

def get_competition_avg_votes(kernels: pd.DataFrame) -> pd.DataFrame:
    return (
        kernels
        .groupby(KernelColumns.SOURCE_COMPETITION_ID)[KernelColumns.TOTAL_VOTES]
        .mean()
        .reset_index(name=CompetitionColumns.AVG_TOTAL_VOTES)
    )

def fillna_list(x):
    return x if isinstance(x, list) else []

def fillna_dict(x):
    return x if isinstance(x, dict) else {}

def fillna_matrix(x):
    return x if isinstance(x, np.ndarray) else np.zeros((MAX_LABEL, MAX_LABEL))

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
    competitions[CompetitionColumns.TRANSITION_MATRIX] = competitions[CompetitionColumns.TRANSITION_MATRIX].apply(apply_safe(lambda x: x.tolist()))
    
    for col_name in [CompetitionColumns.LABEL_STATS, CompetitionColumns.TRANSITION_MATRIX]:
        competitions[col_name] = competitions[col_name].apply(lambda l: json.dumps(l) if is_valid_val(l) else "")
    print("Finished dumping JSON columns")
    
    print("=" * 30)
    
    print("Saving CSVs...")
    competitions.to_csv("Competitions_stats_tmp.csv", index=False)
    #save_kernels(kernels, "AllCompetitionKernels_tmp.csv")
    #cells.to_csv(FILE_BASE + "Cells_predicted_tmp.csv", index=False)
    print("Finished saving CSVs")
    

if __name__ == "__main__":
    main()