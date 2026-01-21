import pandas as pd
import json

FILE_BASE = "/media/tim/Data/Thesis/"

def add_stats_to_competitions(cells: pd.DataFrame, kernels: pd.DataFrame, competitions: pd.DataFrame) -> tuple[pd.DataFrame, pd.DataFrame]:
    competition_label_stats = get_label_stats(cells, kernels)
    label_sequences = get_label_sequences(cells)
    label_matrices = get_label_transition_stats(label_sequences, kernels)
    avg_cells_per_kernel = get_avg_cells_per_kernel(cells, kernels)
    avg_total_votes = get_avg_votes(kernels)

    competitions = competitions.merge(
        competition_label_stats,
        left_on='Id',
        right_on="SourceCompetitionId",
        how='left'
    )
    competitions.drop(columns=["SourceCompetitionId"], inplace=True)
    competitions['MainLabelStats'] = competitions['MainLabelStats'].fillna('{}')
    
    kernels = kernels.merge(
        label_sequences,
        on="KernelVersionId",
        how="left"
    )

    kernels["LabelSequence"] = (
        kernels["LabelSequence"]
        .apply(lambda x: x if isinstance(x, list) else [])
        .apply(json.dumps)
    )
    
    competitions["TransitionMatrix"] = (
        competitions["Id"]
        .map(label_matrices)
        .apply(lambda x: x if isinstance(x, list) else [[0] * 13 for _ in range(13)])
        .apply(json.dumps)
    )
    
    competitions = competitions.merge(
        avg_cells_per_kernel,
        left_on='Id',
        right_on="SourceCompetitionId",
        how='left'
    )
    competitions.drop(columns=["SourceCompetitionId"], inplace=True)
    competitions['AvgCellsPerKernel'] = competitions['AvgCellsPerKernel'].fillna(0)

    competitions = competitions.merge(
        avg_total_votes,
        left_on='Id',
        right_on="SourceCompetitionId",
        how='left'
    )
    competitions.drop(columns=["SourceCompetitionId"], inplace=True)
    competitions['AvgTotalVotes'] = competitions['AvgTotalVotes'].fillna(0)
    
    return (competitions, kernels)
    
def add_stats_to_cells(cells: pd.DataFrame) -> pd.DataFrame:
    cells['SourceLineCount'] = (
        cells['Source']
        .fillna('')
        .str.count('\n')
        .add(1)
    )
    
    return cells

def get_label_stats(cells: pd.DataFrame, kernels: pd.DataFrame) -> pd.DataFrame:
    cells_with_comp = cells.merge(
        kernels[['KernelVersionId', 'SourceCompetitionId']],
        on='KernelVersionId',
        how='left'
    )

    competition_label_stats = (
        cells_with_comp
        .groupby('SourceCompetitionId')['MainLabel']
        .value_counts()
        .groupby(level=0)
        .apply(lambda s: json.dumps(
            {str(k): int(v) for k, v in s.droplevel(0).to_dict().items()}
        ))
        .reset_index(name='MainLabelStats')
    )
    
    return competition_label_stats

def get_label_sequences(cells: pd.DataFrame) -> pd.DataFrame:
    cells_valid = cells.dropna(subset=["MainLabel"])

    cells_sorted = cells_valid.sort_values(["KernelVersionId", "CellId"])

    is_duplicate = cells_sorted["MainLabel"] == cells_sorted.groupby("KernelVersionId")["MainLabel"].shift()
    
    # Keep the first row of each kernel and remove other consecutive duplicates
    cells_dedup = cells_sorted[~is_duplicate | cells_sorted.groupby("KernelVersionId").cumcount().eq(0)]
    
    # Build sequence per kernel
    kernel_sequences = (
        cells_dedup
        .groupby("KernelVersionId")["MainLabel"]
        .agg(list)
        .reset_index(name="LabelSequence")
    )

    kernel_sequences["LabelSequence"] = kernel_sequences["LabelSequence"].apply(lambda lst: [int(x) for x in lst])
    
    return kernel_sequences

def get_label_transition_stats(kernel_sequences: pd.DataFrame, kernels: pd.DataFrame) -> pd.DataFrame:
    kernel_sequences = kernel_sequences.merge(
        kernels,
        on="KernelVersionId",
        how="left"
    )

    kernel_sequences["Transitions"] = kernel_sequences["LabelSequence"].apply(lambda x: list(zip(x[:-1], x[1:])))


    transitions = (
        kernel_sequences
        .explode("Transitions")
        .dropna(subset=["Transitions"])
    )

    transitions[["From", "To"]] = pd.DataFrame(
        transitions["Transitions"].tolist(),
        index=transitions.index
    )
    
    stats = (
        transitions
        .groupby(["SourceCompetitionId", "From", "To"])
        .size()
        .reset_index(name="Count")
    )
    
    max_label = 12

    dense_matrices = {}

    for comp, g in stats.groupby("SourceCompetitionId"):
        mat = [[0] * max_label for _ in range(max_label)]
        for _, r in g.iterrows():
            mat[int(r.From)][int(r.To)] = int(r.Count)
        dense_matrices[comp] = mat

    return dense_matrices
    

def get_avg_cells_per_kernel(cells: pd.DataFrame, kernels: pd.DataFrame) -> pd.DataFrame:
    cells_per_kernel = (
        cells
        .groupby('KernelVersionId')
        .size()
        .reset_index(name='CellCount')
    )
    
    kernels_with_counts = kernels.merge(
        cells_per_kernel,
        on='KernelVersionId',
        how='left'
    )
    kernels_with_counts['CellCount'] = kernels_with_counts['CellCount'].fillna(0)
    
    avg_cells_per_kernel = (
        kernels_with_counts
        .groupby('SourceCompetitionId')['CellCount']
        .mean()
        .reset_index(name='AvgCellsPerKernel')
    )
    
    return avg_cells_per_kernel

def get_avg_votes(kernels: pd.DataFrame) -> pd.DataFrame:
    avg_total_votes = (
        kernels
        .groupby('SourceCompetitionId')['TotalVotes']
        .mean()
        .reset_index(name='AvgTotalVotes')
    )
    avg_total_votes['AvgTotalVotes'] = avg_total_votes['AvgTotalVotes'].fillna(0)
    
    return avg_total_votes

def main():
    cells = pd.read_csv(FILE_BASE + "Cells_predicted.csv", dtype={"MainLabel": "Int32"})
    kernels = pd.read_csv("AllCompetitionKernels.csv")
    competitions = pd.read_csv("Competitions.csv")

    competitions, kernels = add_stats_to_competitions(cells, kernels, competitions)
    cells = add_stats_to_cells(cells)
    
    competitions.to_csv("Competitions_stats.csv", index=False)
    kernels.to_csv("AllCompetitionKernels.csv", index=False)
    cells.to_csv(FILE_BASE + "Cells_predicted.csv", index=False)
    

if __name__ == "__main__":
    main()