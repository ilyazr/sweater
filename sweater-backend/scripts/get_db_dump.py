import datetime
import pathlib
import platform
import subprocess

git_bash = "C:\\Program Files\\Git\\bin\\bash.exe"
container_name = input("Enter container name: ")
db_name = "sweater_backend"
d = datetime.datetime.now().strftime("%d-%m-%yT%H-%M-%S")
filename = f'{db_name}-db_dump_{d}.sql'
command = f'docker exec -it {container_name} pg_dump -U postgres {db_name}' \
              f' > {filename}'
args: list
if platform.system().lower() == "windows":
    args = [git_bash, "-c", command]
else:
    args = [command]
subprocess.run(args, shell=True, text=True, stdout=subprocess.PIPE)
print(f'Executed: {command}')
if not pathlib.Path(filename).exists():
    print(f'Error during creating dump!')
else:
    print(f'Dump has been created: {filename}')
