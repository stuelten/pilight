#!/usr/bin/env bash
#
# Set a light's status
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
light=$2
state=$3
url="${BASE_URL}/families/${family}/lights/${light}/status"

curl -v --header "Accept:application/de.ckc.agwa.pilight.services.json.json" \
    --header "Content-Type:application/de.ckc.agwa.pilight.services.json.json" \
    --request PUT \
    --data "${state}" \
    "$url"
