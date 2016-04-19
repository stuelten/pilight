#!/usr/bin/env bash
#
# Starts the lamp's logic

# import config
. $( dirname "$0" )/pilight-config.sh

MAIN_CLASS=de.ckc.agwa.pilight.logic.MotherMain

java -cp ${LIB_DIR}/pilight-light-*-jar-with-dependencies.jar ${MAIN_CLASS}
