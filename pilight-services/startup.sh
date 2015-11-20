#!/usr/bin/env bash
# Starts Restful service in embedded grizzly server

LIB_DIR=$( dirname "$0" )/target
MAIN_CLASS=de.ckc.agwa.pilight.services.rest.PiLightRestfulServiceMain

java -cp ${LIB_DIR}/pilight-services-*-jar-with-dependencies.jar ${MAIN_CLASS}
