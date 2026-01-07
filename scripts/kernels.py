import pandas as pd
from competition_cells.extract_kernels import extract_kernels
from competition_cells.extract_cells import extract_all_code_cells

def main():
    competitions = get_competitions()

    print("Reading Kernels...")
    kernels = pd.read_csv('meta-kaggle/Kernels.csv', 
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
    kernel_versions = pd.read_csv('meta-kaggle/KernelVersions.csv',
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
    kernel_competition_sources = pd.read_csv('meta-kaggle/KernelVersionCompetitionSources.csv', dtype={
        "Id": "Int32",
        "KernelVersionId": "Int32",
        "SourceCompetitionId": "Int32"
    })
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
    
    success_files = extract_kernels(final_versions_all["KernelVersionId"])
    success_files = pd.Series(success_files).astype("Int32")
    final_versions = final_versions_all[final_versions_all["KernelVersionId"].isin(success_files)]
    final_versions = join_authors(final_versions)
    final_versions = final_versions[["KernelVersionId", "SourceCompetitionId", "CreationDate", "VersionNumber", "Title", "TotalVotes", "TotalViews", "TotalComments", "CurrentUrlSlug", "AuthorUserName", "AuthorDisplayName"]]
    final_versions = final_versions.fillna({"TotalViews": 0, "TotalComments": 0})
    final_versions.to_csv("AllCompetitionKernels.csv", index=False)
    
    all_cells = extract_all_code_cells(final_versions["KernelVersionId"])
    all_cells.to_csv("/media/tim/Data/Thesis/Cells.csv", index_label="Id")
    

def get_competitions() -> pd.DataFrame:
    competitions = pd.read_csv("meta-kaggle/Competitions.csv", parse_dates=["DeadlineDate"])
    return competitions[
        (competitions["DeadlineDate"] >= "2020-01-01") &
        (competitions["TotalSubmissions"] >= 500)
    ][["Id", "Title", "Subtitle", "Slug", "TotalSubmissions", "DeadlineDate"]]

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

def join_authors(kernels_final: pd.DataFrame) -> pd.DataFrame:
    print("Reading Users...")
    users = pd.read_csv("meta-kaggle/Users.csv", usecols=["Id", "UserName", "DisplayName"],
        dtype={
            "Id": "Int32",
            "UserName": "string",
            "DisplayName": "string"
        })
    users.rename(columns={"Id": "AuthorUserId"}, inplace=True)
    result = kernels_final.merge(users, how="left", on="AuthorUserId")
    result.drop(columns = "AuthorUserId")
    result.rename(columns={"UserName": "AuthorUserName"}, inplace=True)
    result.rename(columns={"DisplayName": "AuthorDisplayName"}, inplace=True)
    return result

if __name__ == "__main__":
    main()