#!/usr/bin/env bash
# basic config for pilight scripts

# base dir for scripts
export BASE_DIR="$( dirname "$0" )"

# dir with jars
export LIB_DIR="${BASE_DIR}/../pilight-services/target"
# construct class path
for i in ${LIB_DIR}/pilight-services-*-jar-with-dependencies.jar
do
    CLASS_PATH="${i}"
done
export CLASS_PATH
unset i

# main class for java code
export MAIN_CLASS=de.ckc.agwa.pilight.services.rest.PiLightRestfulServiceMain

# URL to use if none given
export PILIGHT_BASE_URL="http://kannkeule.de:9997/pilight"

# interpret common parameters
if [ "$1" == "-b" -o "$1" == "--baseurl" ]
then
    BASE_URL="$2"
    shift
    shift
else
    BASE_URL="${PILIGHT_BASE_URL}"
fi
export BASE_URL
