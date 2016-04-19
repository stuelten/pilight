#!/usr/bin/env bash
#
# Starts the lamp's logic

# import config
. $( dirname "$0" )/pilight-config.sh

java -cp "${CLASS_PATH}" "${MAIN_CLASS_MOTHER}" \
    -b "${BASE_URL}" \
    $@
