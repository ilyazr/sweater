#!/bin/bash
read -r -p "Enter container name: " container_name
db_name="sweater_backend"
file_name=$db_name-db_dump_$(date +"%d-%m-%yT%H-%M-%S").sql
cmd="docker exec -it $container_name pg_dump -U postgres $db_name > $file_name"
eval "$cmd"
echo "Executed: $cmd"
if [ ! -f "$file_name" ]; then
  echo "Error during creating dump!"
  else
    echo "Dump has been created: $file_name"
fi
