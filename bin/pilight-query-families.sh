#!/usr/bin/env bash
#
# Queries the names of all families
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
url="${BASE_URL}/info/families"

# call service
curl -v --header "Accept:application/json" \
    "$url"
