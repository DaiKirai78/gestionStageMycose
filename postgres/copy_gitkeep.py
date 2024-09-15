import os
import shutil

def copy_gitkeep_to_empty_dirs(root_dir, gitkeep_path):
    # Walk through the directory tree
    for dirpath, dirnames, filenames in os.walk(root_dir):
        # If the directory is empty (no files and no subdirectories)
        if not dirnames and not filenames:
            # Define the destination path for .gitkeep
            destination = os.path.join(dirpath, '.gitkeep')
            # Copy .gitkeep file to the empty directory
            shutil.copy2(gitkeep_path, destination)
            print(f"Copied .gitkeep to: {destination}")

if __name__ == "__main__":
    # Specify the relative root directory to start checking
    root_directory = './'  # Adjust this as needed
    # Path to the .gitkeep file to copy, relative to the script's location
    gitkeep_file_path = './projet_local_pgdata/pg_commit_ts/.gitkeep'
    
    # Get the absolute paths
    script_dir = os.path.dirname(os.path.abspath(__file__))
    root_directory = os.path.join(script_dir, root_directory)
    gitkeep_file_path = os.path.join(script_dir, gitkeep_file_path)
    
    copy_gitkeep_to_empty_dirs(root_directory, gitkeep_file_path)