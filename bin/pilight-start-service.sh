#!/usr/bin/env bash
#
# Starts Restful service in embedded grizzly server

# import config
. $( dirname "$0" )/pilight-config.sh

java -cp "${CLASS_PATH}" "${MAIN_CLASS}" -b "${BASE_URL}"
