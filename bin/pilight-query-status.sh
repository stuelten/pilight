#!/usr/bin/env bash
#
# Queries the status

# import config
. $( dirname "$0" )/pilight-config.sh

# build service path
url="${BASE_URL}/status"

# call service
curl -v --header "Accept:application/json" \
    "$url"
