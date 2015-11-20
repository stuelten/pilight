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

LIB_DIR=pilight-services/target
MAIN_CLASS=de.ckc.agwa.pilight.services.client.PiLightServiceClientMain

java -cp ${LIB_DIR}/pilight-services-de.ckc.agwa.pilight.services.json.json-*-jar-with-dependencies.jar \
    ${MAIN_CLASS} \
    -b ${BASE_URL} \
    $@
