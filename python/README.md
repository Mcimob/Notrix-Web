# Python 
In this directory you can find all the python scripts used to extract the data from the available mete-kaggle and process it further into usable database tables. The same scripts are also used by the server to process uploaded competitions/notebooks

## Usage

- `kernels.py`: This script takes a meta-kaggle directory, meta-kaggle-code zipfile and an output directory.
  - `python -m app.kernels <meta-kaggle-dir> <meta-kaggle-code-zip> <outDir>`
  - Produces three files in `outDir`: `Competitions.csv`, `AllCompetitionKernels.csv`, `Cells.csv`
- `predict.py`: This script takes the cells and predicts the label for each cell using a pretrained model. This will take a very long time, if run on the whole dataset.
  - `python -m app.predict <cells-csv> <output-path>`
  - Produces a csv file, where each code cell has an additional column MainLabel
- `stats.py`: This scripts adds stats to cells, kernels and compettions. Some of these stats are later used to cluster the notebooks within the competitions. Other stats will be displayed to the user in the application.
  - `python -m app.stats <input-dir> <output-dir>` 
  - Expects input-dir to contain files `Competitions.csv`, `AllCompetitionKernels.csv` and `Cells.csv`. `Cells.csv` has to already contain the MainLabel column. Produces the same files in the output directory
- `hmm_clustering.py`: This script clusters the kernels within each competition according to the stats added in the last step.
  - `python -m app.hmm_clustering <input-file> <output-dir>`
  - Input file has to be kernels. Output dir will contain `Clusters.csv` and `AllCompetitionKernels.csv`.
- `analyze_clusters.py`: Adds descriptions and stats to clusters.
  - `python -m app.analyze_clusters <input-dir> <output-file>`
  - The input dir is expected to contain `Clusters.csv` and `AllCompetitionKernels.csv`. The output will have the stats and description added.
- `embed_competitions.py`: Adds an embedding to the competitions, based on their title, subtitle and overview.
  - `python -m app.embed_competitions <input-file> <output-file>`
  - Output file will have fields `CoordinateX` and `CoordinateY`

The complete pipeline is: `kernels.py` -> `predict.py` -> `stats.py` -> `hmm_clustering.py` -> `analyze_clusters.py` -> `embed_competitions.py`
