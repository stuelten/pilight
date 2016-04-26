#!/usr/bin/env bash
#
# Queries the names of all families

# import config
. $( dirname "$0" )/pilight-config.sh

# build service path
url="${BASE_URL}/info/families"

# call service
curl -v --header "Accept:application/json" \
    "$url"
