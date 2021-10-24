#!/bin/bash

# INFO: Run from GitBash to get rid of curl connection refused error
host="http://localhost:9090/api"
default_auth_header="Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZXYxMjMiLCJpZCI6MjIsImF1dGhvcml0aWVzIjpbeyJhdXRob3JpdHkiOiJ1c2VyOnJlYWQifSx7ImF1dGhvcml0eSI6InVzZXI6d3JpdGUifSx7ImF1dGhvcml0eSI6IlJPTEVfQURNSU4ifV0sImlhdCI6MTYzMDM5MTUzMCwiZXhwIjoxNjMwOTU0ODAwfQ.tfxHieYlrhKvRGsnWD8Q53a9s5bJozeDmOe-e0lOSPfAdT9Xfwh3nKrp9ImsNusiNQMPcPdeo513qAdL7hHDpQ"
accept_header="application/json"
content_type_header="application/json"
http_methods=("get" "post" "put" "delete")
default_method="get"
data_file="data.json"

# npm install -g json
json_reader="json"

# Set endpoint
read -r -p "Set endpoint: " endpoint
if [ -z "$endpoint" ]; then
  echo "You should set endpoint!"
  exit 1
fi

read -r -p "Set HTTP method (skip for default=get): " http_method

# If http method != get
if [ -n "$http_method" ] && [ "${http_method,,}" != "${default_method,,}" ]; then
  default_method=$http_method
  # If http method == POST or PUT
  if [ "${default_method,,}" == "${http_methods[1],,}" ] || [ "${default_method,,}" == "${http_methods[2],,}" ]; then
    read -r -p "Enter path of data file (skip for use default=${data_file}), - for empty: " path
    if [ -n "$path" ]; then
      data_file=$path
    fi
    if [ "$data_file" != "-" ] && [ ! -f "$data_file" ]; then
      echo "Error: could not find data file for path $(pwd)/${data_file}"
      exit 1
    fi
  else
    # If http method == DELETE
    if [ "${default_method,,}" != "${http_methods[3],,}" ]; then
      echo "Error: Wrong method \"${default_method}\""
      exit 1
    fi
  fi

  # Method name to uppercase
  default_method=${default_method^^}
fi

read -r -p "Enter auth header (skip for default): " auth_header

# POST || PUT
if [ "${default_method,,}" == "${http_methods[1],,}" ] || [ "${default_method,,}" == "${http_methods[2],,}" ]; then
  if [ "$data_file" = "-" ]; then
    curl -i \
      -H "Accept: $accept_header" \
      -H "Content-Type: $content_type_header" \
      -H "Authorization: ${auth_header:=${default_auth_header}}" \
      -X "$default_method" \
      "${host}${endpoint}" | $json_reader
      else
        curl -i \
          -H "Accept: $accept_header" \
          -H "Content-Type: $content_type_header" \
          -H "Authorization: ${auth_header:=${default_auth_header}}" \
          -X "$default_method" \
          --data "@$data_file" "${host}${endpoint}" | $json_reader
  fi
# DELETE
elif [ "${default_method,,}" == "${http_methods[3],,}" ]; then
  curl -i \
  -H "Accept: $accept_header" \
  -H "Content-Type: $content_type_header" \
  -H "Authorization: ${auth_header:=${default_auth_header}}" \
  -X "$default_method" \
  "${host}${endpoint}" | $json_reader
  # GET
  else
    curl -i \
      -H "Accept: $accept_header" \
      -H "Content-Type: $content_type_header" \
      -H "Authorization: ${auth_header:=${default_auth_header}}" \
      "${host}${endpoint}" | $json_reader
fi
