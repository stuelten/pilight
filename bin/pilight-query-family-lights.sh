#!/usr/bin/env bash
#
# Queries the status

# import config
. $( dirname "$0" )/pilight-config.sh

# build service path
family=$1
url="${BASE_URL}/families/${family}/info/lights"

# call service
curl -v --header "Accept:application/json" \
    "$url"
