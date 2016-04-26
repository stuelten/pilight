#!/usr/bin/env bash
#
# Set a light's status

# import config
. $( dirname "$0" )/pilight-config.sh

# build service path
family="$1"
light="$2"
state="$3"

url="${BASE_URL}/families/${family}/lights/${light}/status"
data="{\"on\":\"${state}\"}"

# call service
curl -v --header "Accept: application/json" \
    --header "Content-Type: application/json" \
    --request PUT \
    --data "${data}" \
    "$url"
