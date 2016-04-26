#!/usr/bin/env bash
#
# Queries a light's status

# import config
. $( dirname "$0" )/pilight-config.sh

# build service path
family=$1
light=$2
url="${BASE_URL}/families/${family}/lights/${light}/status"

# call service
curl -v --header "Accept: application/json" \
    "$url"
