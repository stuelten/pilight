#!/usr/bin/env bash
# Starts the lamp's logic

LIB_DIR=$( dirname "$0" )/../pilight-logic/target
MAIN_CLASS=de.ckc.agwa.pilight.logic.MotherMain

if [ "$1" == "-b" -o "$1" == "--baseurl" ]
then
    BASE_URL="$2"
    shift
    shift
else
    BASE_URL=http://kannkeule.de:9997/pilight
fi
PILIGHT_SERVICE=${BASE_URL}

java -cp ${LIB_DIR}/pilight-light-*-jar-with-dependencies.jar ${MAIN_CLASS}
