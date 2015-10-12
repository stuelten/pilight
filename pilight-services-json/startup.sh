#!/usr/bin/env bash
# Starts Restful service in embedded grizzly server

LIBDIR=$( dirname "$0" )/target

java -cp ${LIBDIR}/pilight-services-json-*-jar-with-dependencies.jar de.ckc.agwa.pilight.services.json.PiLightRestfulServiceMain
