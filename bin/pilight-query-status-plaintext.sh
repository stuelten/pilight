#!/usr/bin/env bash
#
# Queries the status

# import config
. $( dirname "$0" )/pilight-config.sh

# build service path
url="${BASE_URL}/status"

# call service
curl -i  --header "Accept: text/plain" \
    "${url}"
