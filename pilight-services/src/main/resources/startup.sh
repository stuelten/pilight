#!/usr/bin/env bash
# Starts Restful service in embedded grizzly server

LIB_DIR=..
MAIN_CLASS=de.ckc.agwa.pilight.services.rest.PiLightRestfulServiceMain

java -cp ${LIB_DIR}/pilight-services-de.ckc.agwa.pilight.services.json.json-*-jar-with-dependencies.jar ${MAIN_CLASS}
