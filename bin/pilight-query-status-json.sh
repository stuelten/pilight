#!/usr/bin/env bash
#
# Queries the status
#

if [ "$1" == "-b" -o "$1" == "---baseurl" ]
then
    BASE_URL="$1"
    shift
else
    BASE_URL=http://kannkeule.de:9997/pilight
fi

url="${BASE_URL}/status"
curl -v --header "Accept: application/json" \
    "$url"
