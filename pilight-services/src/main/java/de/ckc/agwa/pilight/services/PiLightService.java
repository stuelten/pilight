/*
 * Copyright (c) 2014 Timo Stülten <timo.stuelten@googlemail.com>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package de.ckc.agwa.pilight.services;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Timo Stülten
 */
@Path("/pilight")
public class PiLightService {
    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightService.class);

    // ----------------------------------------------------------------------

    /**
     * Contains all lights
     */
    protected static Map<String, Map<String, Boolean>> lights = new ConcurrentHashMap<>();

    // ----------------------------------------------------------------------

    /**
     * This service's status.
     */
    public static class Status {
        /**
         * Number of families
         */
        public int familiesCount;
        /**
         * Number of lights.
         */
        public int lightsCount;

        @Override
        public String toString() {
            String ret = "Serving " + familiesCount + " families with " + lightsCount + " lights.";
            return ret;
        }
    }

    /**
     * Get the service's status.
     *
     * @return a String giving the number of served families and lights.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String serviceStatusPlain() {
        LOGGER.debug("serviceStatusPlain(): Called");
        Status status = serviceStatus();
        return status.toString();
    }

    /**
     * Get the service's status.
     *
     * @return a {@link Status} giving the number of served families and lights.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Status serviceStatus() {
        LOGGER.debug("serviceStatus(): Called");
        int lightsCount = 0;
        for (Map<String, Boolean> familylights : lights.values()) {
            lightsCount += familylights.size();
        }
        Status ret = new Status();
        ret.familiesCount = lights.size();
        ret.lightsCount = lightsCount;
        return ret;
    }

    /**
     * Get the status for some light.
     *
     * @param family the name of the familiy
     * @param light  the name of the light
     * @return {@code true} for a burning light, {@code false} otherwise
     */
    @GET
    @Path("{family}/{light}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public String getStatus(@PathParam("family") String family,
                            @PathParam("light") String light) {
        LOGGER.debug("getStatus('{}','{}'): called", family, light);
        Boolean status = null;

        Map<String, Boolean> familylights = lights.get(family);
        if (familylights == null) {
            status = Boolean.FALSE;
        } else {
            status = familylights.get(light);
        }

        LOGGER.debug("getStatus('{}'): return '{}'", light, status);
        return "" + status;
    }

    // ----------------------------------------------------------------------

    /**
     * Set the status for some light.
     *
     * @param light  the light
     * @param status {@code true} for a burning light, {@code false} otherwise
     */
    @PUT
    @Path("{family}/{light}/status/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String setStatus(@PathParam("family") String family,
                            @PathParam("light") String light,
                            String status) {
        LOGGER.debug("setStatus('{}'.'{}'): called", light, status);
        Boolean checkedStatus = Boolean.valueOf(status);

        Map<String, Boolean> familylights = lights.get(family);
        if (familylights == null) {
            familylights = new ConcurrentHashMap<>();
            lights.put(family, familylights);
        }
        familylights.put(light, checkedStatus);

        return "" + checkedStatus;
    }

    // ----------------------------------------------------------------------

    public String toString() {
        String ret = new ToStringBuilder(this)
                .append("lights", lights)
                .toString();
        return ret;
    }

}
