import argparse
import tempfile

import pandas as pd
from app.competition_cells.extract_kernels import extract_kernels
from app.competition_cells.extract_cells import extract_all_code_cells

def get_competitions(filename: str) -> pd.DataFrame:
    competitions = pd.read_csv(filename, parse_dates=["DeadlineDate"])
    return competitions[
        (competitions["DeadlineDate"] >= "2020-01-01") &
        (competitions["TotalSubmissions"] >= 500)
    ][["Id", "Title", "Subtitle", "Overview", "Slug", "TotalSubmissions", "DeadlineDate"]]

def get_tags(competitions: pd.DataFrame) -> pd.DataFrame:
    competition_tags = pd.read_csv("meta-kaggle/CompetitionTags.csv")
    tags = pd.read_csv("meta-kaggle/Tags.csv")
    competition_slugs = competition_tags.merge(
        tags,
        left_on="TagId",
        right_on="Id",
        how="left"
    )
    
    filtered = competition_slugs.merge(
        competitions[["Id"]],
        left_on="CompetitionId",
        right_on="Id",
        how="inner"
    )
    
    return filtered[["CompetitionId", "Slug"]]

def precompute_final_versions(kernels: pd.DataFrame, kernel_versions: pd.DataFrame, kernel_competition_sources: pd.DataFrame) -> pd.DataFrame:
    merged = kernel_competition_sources.merge(
        kernel_versions, on='KernelVersionId', how='left'
    ).dropna(subset=['VersionNumber'])

    final = merged.loc[
        merged.groupby('ScriptId')['VersionNumber'].idxmax()
    ].copy()

    final = final.merge(
        kernels[['KernelId', 'CurrentKernelVersionId', 'TotalViews', 'TotalComments', 'CurrentUrlSlug', "AuthorUserId"]],
        left_on='KernelVersionId',
        right_on='CurrentKernelVersionId',
        how='left'
    )
    
    # Make sure language is python notebook (HTML)
    final = final[
        (final["ScriptLanguageId"].isin([8, 9])) &
        (final["TotalVotes"] > 0)
    ]

    return final

def join_authors(kernels_final: pd.DataFrame, users: pd.DataFrame) -> pd.DataFrame:
    users.rename(columns={"Id": "AuthorUserId"}, inplace=True)
    result = kernels_final.merge(users, how="left", on="AuthorUserId")
    result.drop(columns = "AuthorUserId")
    result.rename(columns={"UserName": "AuthorUserName"}, inplace=True)
    result.rename(columns={"DisplayName": "AuthorDisplayName"}, inplace=True)
    return result

def remove_empty(cells: pd.DataFrame, kernels: pd.DataFrame, competitions: pd.DataFrame) -> tuple[pd.DataFrame, pd.DataFrame]:
    kernels = kernels[
        kernels['KernelVersionId'].isin(cells['KernelVersionId'])
    ].copy()

    competitions = competitions[
        competitions['Id'].isin(kernels['SourceCompetitionId'])
    ].copy()

    cells = cells[
        cells['KernelVersionId'].isin(kernels['KernelVersionId'])
    ].copy()

    
    assert cells['KernelVersionId'].isin(kernels['KernelVersionId']).all()
    assert kernels['SourceCompetitionId'].isin(competitions['Id']).all()
    
    return (kernels, competitions)

def transform_data(competitions: pd.DataFrame, kernels: pd.DataFrame, kernel_versions: pd.DataFrame, kernel_competition_sources: pd.DataFrame, users: pd.DataFrame, zipPath: str) -> tuple[pd.DataFrame, pd.DataFrame, pd.DataFrame]:
    kernel_competition_sources = kernel_competition_sources[kernel_competition_sources["SourceCompetitionId"].isin(competitions["Id"])]

    # Rename IDs only once
    kernels.rename(columns={'Id': 'KernelId'}, inplace=True)
    kernel_versions.rename(columns={'Id': 'KernelVersionId'}, inplace=True)

    print("Precomputing all final versions...")
    final_versions_all = precompute_final_versions(
        kernels, kernel_versions, kernel_competition_sources
    )
    print(f"Final Versions All shape: {final_versions_all.shape}")

    final_versions_all.sort_values(["SourceCompetitionId", "TotalVotes"], inplace=True, ascending=[True, False])
    
    with tempfile.TemporaryDirectory() as tmpDir:
        success_files = extract_kernels(final_versions_all["KernelVersionId"], zipPath, tmpDir)
        success_files = pd.Series(success_files).astype("Int32")
        final_versions = final_versions_all[final_versions_all["KernelVersionId"].isin(success_files)]
        final_versions = join_authors(final_versions, users)
        final_versions = final_versions[["KernelVersionId", "SourceCompetitionId", "CreationDate", "VersionNumber", "Title", "TotalVotes", "TotalViews", "TotalComments", "CurrentUrlSlug", "AuthorUserName", "AuthorDisplayName"]]
        final_versions = final_versions.fillna({"TotalViews": 0, "TotalComments": 0})
        
        all_cells = extract_all_code_cells(final_versions["KernelVersionId"], tmpDir)
    
    all_kernels, competitions = remove_empty(all_cells, final_versions, competitions)
    
    return competitions, all_kernels, all_cells

def main():
    parser = argparse.ArgumentParser(
        prog="TransformData",
        description="Takes the meta-kaggle directory and the meta-kaggle-code tipfile and creates three csv containing all Competitions, Kernels and Cells",
    )
    parser.add_argument("metakaggle")
    parser.add_argument("metakagglecode")
    parser.add_argument("outDir")
    
    args = parser.parse_args()
    
    competitions = get_competitions(args.metakaggle + "/Competitions.csv")

    print("Reading Kernels...")
    kernels = pd.read_csv(args.metakaggle + '/Kernels.csv', 
        usecols=["Id", "CurrentKernelVersionId", "TotalViews", "TotalComments", "CurrentUrlSlug", "AuthorUserId"],
        dtype={
            "Id": "Int32",
            "CurrentKernelVersionId": "Int32",
            "TotalViews": "Int32",
            "TotalComments": "Int32",
            "CurrentUrlSlug": "string",
            "AuthorUserId": "Int32"
        }
    )
    
    print("Reading KernelVersions...")
    kernel_versions = pd.read_csv(args.metakaggle + '/KernelVersions.csv',
        usecols = ["Id", "ScriptId", "ScriptLanguageId", "CreationDate", "VersionNumber", "Title", "TotalVotes"],
        dtype={
            "Id": "Int32",
            "ScriptId": "Int32",
            "ScriptLanguageId": "Int32",
            "CreationDate": "string",
            "VersionNumber": "Int32",
            "Title": "string",
            "TotalVotes": "Int32"
        }
    )
    
    print("Reading KernelVersionCompetitionSources")    
    kernel_competition_sources = pd.read_csv(args.metakaggle + '/KernelVersionCompetitionSources.csv', dtype={
        "Id": "Int32",
        "KernelVersionId": "Int32",
        "SourceCompetitionId": "Int32"
    })
    
    print("Reading Users...")
    users = pd.read_csv(args.metakaggle + "/Users.csv", usecols=["Id", "UserName", "DisplayName"],
        dtype={
            "Id": "Int32",
            "UserName": "string",
            "DisplayName": "string"
        })
    
    competitions, final_versions, all_cells = transform_data(competitions, kernels, kernel_versions, kernel_competition_sources, users, args.metakagglecode)
    
    competitions.to_csv(args.outDir + "/Competitions.csv", index=False)
    final_versions.to_csv(args.outDir + "/AllCompetitionKernels.csv", index=False)
    all_cells.to_csv(args.outDir + "/Cells.csv", index_label="Id")
    

if __name__ == "__main__":
    main()