#!/usr/bin/env bash
#
# Queries the status
#

if [ "$1" == "-b" -o "$1" == "---baseurl" ]
then
    BASE_URL="$2"
    shift
    shift
else
    BASE_URL=http://kannkeule.de:9997/pilight
fi

# build service path
family=$1
url="${BASE_URL}/families/${family}/info/lights"

url="${BASE_URL}/status"
curl -v --header "Accept:application/de.ckc.agwa.pilight.services.json.json" \
    "$url"
