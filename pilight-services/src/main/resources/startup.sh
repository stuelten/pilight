#!/usr/bin/env bash
# Starts Restful service in embedded grizzly server

LIBDIR=..

java -cp ${LIBDIR}/pilight-services-de.ckc.agwa.pilight.services.json.json-*-jar-with-dependencies.jar de.ckc.agwa.pilight.services.de.ckc.agwa.pilight.services.json.json.PiLightRestfulServiceMain
