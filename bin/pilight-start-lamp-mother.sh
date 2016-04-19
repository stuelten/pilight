#!/usr/bin/env bash
#
# Starts the lamp's logic

# import config
. $( dirname "$0" )/pilight-config.sh

# use non-default main class
MAIN_CLASS=de.ckc.agwa.pilight.logic.MotherMain

java -cp "${CLASS_PATH}" "${MAIN_CLASS}" -b "${BASE_URL}"
