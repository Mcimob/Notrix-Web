class CellColumns:
    MAIN_LABEL = "MainLabel"
    SOURCE_LINES_COUNT = "SourceLineCount"
    
class KernelColumns:
    KERNEL_VERSION_ID = "KernelVersionId"
    SOURCE_COMPETITION_ID = "SourceCompetitionId"
    LABEL_STATS = "MainLabelStats"
    LABEL_STATS_NORM = "MainLabelStatsNorm"
    LABEL_SEQUENCE = "LabelSequence"
    TRANSITION_MATRIX = "TransitionMatrix"
    TRANSITION_MATRIX_NORM = "TransitionMatrixNorm"
    NUM_LINES = "NumLines"
    CELL_COUNT = "CellCount"
    TOTAL_VOTES = "TotalVotes"
    COMPLEXITY_FEATURES_NORM = "ComplexitiFeaturesNorm"
    N_GRAMS = "NGrams"
    LOCAL_CLUSTER_ID = "LocalClusterId"
    CLUSTER_ID = "ClusterId"

class ClusterColumns:
    CLUSTER_ID = KernelColumns.CLUSTER_ID
    LOCAL_CLUSTER_ID = "LocalClusterId"
    CLUSTER_SIZE = "ClusterSize"
    LABEL_STATS = KernelColumns.LABEL_STATS
    TRANSITION_MATRIX = KernelColumns.TRANSITION_MATRIX

class CompetitionColumns:
    LABEL_STATS = KernelColumns.LABEL_STATS
    TRANSITION_MATRIX = KernelColumns.TRANSITION_MATRIX
    AVG_LINES = "AvgLinesPerKernel"
    AVG_CELLS = "AvgCellsPerKernel"
    AVG_TOTAL_VOTES = "AvgTotalVotes"