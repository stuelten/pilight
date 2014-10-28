/*
 * Copyright 2014 Timo Stülten <timo.stuelten@googlemail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package de.ckc.agwa.pilight.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.builder.ToStringBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Timo Stülten
 */
@Path("/pilight")
public class PiLightService {
    /** The logger for this class only. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightService.class);

    // ----------------------------------------------------------------------

    /** Contains all lights */
    protected static ConcurrentHashMap<String, Boolean> lights = new ConcurrentHashMap<>();

    // ----------------------------------------------------------------------

    /**
     * Get the service's status.
     *
     * @return a String denoting the number of known lights.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String serviceStatus() {
        LOGGER.debug("serviceStatus(): Called");
        return "Serving " + lights.size() + " lights.";
    }

    /**
     * Get the status for some light.
     * @param light the light
     * @return {@code true} for a burning light, {@code false} otherwise
     */
    @GET
    @Path("/{light}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public String getStatus(@PathParam("light") String light) {
        LOGGER.debug("getStatus('{}'): called", light);
        Boolean status = lights.get(light);

        LOGGER.debug("getStatus('{}'): return '{}'", light, status);
        return "" + status;
    }

    /**
     * Set the status for some light.
     * @param light the light
     * @param status {@code true} for a burning light, {@code false} otherwise
     */
    @PUT
    @Path("/{light}/status/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String setStatus(@PathParam("light") String light, String status) {
        LOGGER.debug("setStatus('{}'.'{}'): called", light, status);
        Boolean checkedStatus = Boolean.valueOf(status);
        lights.put(light, checkedStatus);
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
