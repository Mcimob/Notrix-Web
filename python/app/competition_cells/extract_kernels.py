import zipfile
import os
import shutil

def extract_kernels(kids, zip_path: str, out_dir: str):
    # Generate list of files to extract
    print('Matching kernel paths with zip contents...')
    to_extract = list(map(create_filename, kids))

    print(f'Total files to extract: {len(to_extract)}')

    failed_files = []
    success_files = []
    # Extract files
    with zipfile.ZipFile(zip_path, "r") as z:
        for fname in to_extract:
            try:
                z.getinfo(fname)
            except KeyError:
                failed_files.append(fname)
                continue
            target_path = os.path.join(out_dir, fname.split("/")[-1])
            os.makedirs(os.path.dirname(target_path), exist_ok=True)
            with z.open(fname) as src, open(target_path, 'wb') as dst:
                shutil.copyfileobj(src, dst)
            success_files.append(fname.split("/")[-1].split(".")[0])
    
    print(f"Failed to extract a total of {len(failed_files)} files")
    return success_files

def create_filename(kid):
    top = f'{int(kid) // 1_000_000:04d}'
    sub = f'{(int(kid) // 1_000) % 1_000:03d}'
    return f'{top}/{sub}/{kid}.ipynb'