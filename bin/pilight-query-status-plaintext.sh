#!/usr/bin/env bash
#
# Queries the status
#

if [ "$1" == "-b" -o "$1" == "--baseurl" ]
then
    BASE_URL="$2"
    shift
    shift
else
    BASE_URL=http://kannkeule.de:9997/pilight
fi

# build service path
url="${BASE_URL}/status"

# call service
curl -i  --header "Accept: text/plain" \
    "${url}"
