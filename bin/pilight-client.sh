#!/usr/bin/env bash
#
# Calls the java client

# import config
. $( dirname "$0" )/pilight-config.sh

java -cp ${CLASS_PATH} \
    ${MAIN_CLASS} \
    -b ${BASE_URL} \
    $@
