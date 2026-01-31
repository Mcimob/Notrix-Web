class CellColumns:
    MAIN_LABEL = "MainLabel"
    SOURCE_LINES_COUNT = "SourceLineCount"
    
class KernelColumns:
    LABEL_STATS = "MainLabelStats"
    LABEL_STATS_NORM = "MainLabelStatsNorm"
    LABEL_SEQUENCE = "LabelSequence"
    TRANSITION_MATRIX = "TransitionMatrix"
    TRANSITION_MATRIX_NORM = "TransitionMatrixNorm"
    NUM_LINES = "NumLines"
    CELL_COUNT = "CellCount"
    TOTAL_VOTES = "TotalVotes"
    COMPLEXITY_FEATURES_NORM = "ComplexitiFeaturesNorm"

class CompetitionColumns:
    LABEL_STATS = KernelColumns.LABEL_STATS
    TRANSITION_MATRIX = KernelColumns.TRANSITION_MATRIX
    AVG_LINES = "AvgLinesPerKernel"
    AVG_CELLS = "AvgCellsPerKernel"
    AVG_TOTAL_VOTES = "AvgTotalVotes"