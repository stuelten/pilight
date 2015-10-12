#!/usr/bin/env bash
#
# Queries a light's status
#

if [ "$1" == "-b" -o "$1" == "---baseurl" ]
then
    BASE_URL="$1"
    shift
else
    BASE_URL=http://kannkeule.de:9997/pilight
fi

# build service path
family=$1
light=$2
url="${BASE_URL}/families/${family}/lights/${light}/status"

curl -v --header "Accept: application/json" \
    "$url"
