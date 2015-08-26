/*
 * Copyright (c) 2015 Timo Stülten <timo@stuelten.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ckc.agwa.pilight.services;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This service receives and serves the status of lights for some families.
 *
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
     * Contains all lights.
     */
    protected static Map<String, Map<String, Boolean>> lights = new ConcurrentHashMap<>();

    // ----------------------------------------------------------------------

    /**
     * Get this service's status as plain text.
     *
     * @return a String giving the number of served families and lights.
     */
    @GET
    @Path("status")
    @Produces(MediaType.TEXT_PLAIN)
    public String serviceStatusPlain() {
        LOGGER.debug("serviceStatusPlain(): Called");
        Status status = serviceStatus();
        String ret = status.toString();

        LOGGER.info("serviceStatusPlain(): return '{}'", ret);
        return ret;
    }

    /**
     * Get this service's status.
     *
     * @return a {@link Status} giving the number of served families and lights.
     */
    @GET
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public Status serviceStatus() {
        LOGGER.debug("serviceStatus(): Called");
        int lightsCount = 0;
        for (Map<String, Boolean> familyLights : lights.values()) {
            lightsCount += familyLights.size();
        }

        Status ret = new Status();
        int familiesCount = lights.size();
        ret.setFamiliesCount(familiesCount);
        ret.setLightsCount(lightsCount);

        LOGGER.debug("serviceStatus(): return '{}'", ret);
        return ret;
    }

    /**
     * Get all known families.
     *
     * @return a String giving the families.
     */
    @GET
    @Path("info/families")
    @Produces(MediaType.TEXT_PLAIN)
    public String serviceStatusFamilies() {
        LOGGER.debug("serviceStatusFamilies(): Called");
        StringBuilder ret = new StringBuilder();
        for (String family : lights.keySet()) {
            String nameEscaped = StringEscapeUtils.escapeHtml(family);
            ret.append(nameEscaped).append('\n');
        }

        LOGGER.info("serviceStatusFamilies(): return '{}'", ret);
        return ret.toString();
    }

    // ----------------------------------------------------------------------

    /**
     * Get all known lamps for a family..
     *
     * @return a String giving the lamps.
     */
    @GET
    @Path("families/{family}/info/lights")
    @Produces(MediaType.TEXT_PLAIN)
    public String serviceFamilyInfoLamps(@PathParam("family") String family) {
        LOGGER.debug("serviceFamilyInfoLamps('{}'): called", family);

        Map<String, Boolean> lampMap = lights.get(family);
        StringBuilder ret = new StringBuilder();
        if (lampMap != null) {
            for (String lamp : lampMap.keySet()) {
                String nameEscaped = StringEscapeUtils.escapeHtml(lamp);
                ret.append(nameEscaped).append('\n');
            }
        }

        LOGGER.info("serviceFamilyInfoLamps('{}'): return '{}'", family, ret);
        return ret.toString();
    }

    /**
     * Get the status for some light.
     *
     * @param family the name of the family
     * @param light  the name of the light
     * @return {@code true} for a burning light, {@code false} otherwise
     */
    @GET
    @Path("families/{family}/lights/{light}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public String serviceFamilyLightStatusGet(@PathParam("family") String family,
                                              @PathParam("light") String light) {
        LOGGER.debug("serviceFamilyLightStatusGet('{}','{}'): called", family, light);
        Boolean status = null;

        Map<String, Boolean> familyLights = lights.get(family);
        if (null == familyLights) {
            status = Boolean.FALSE;
        } else {
            status = familyLights.get(light);
        }

        LOGGER.info("serviceFamilyLightStatusGet('{}', '{}'): return '{}'", family, light, status);
        return "" + status;
    }

    /**
     * Set the status for some light.
     *
     * @param family the name of the family
     * @param light  the light
     * @param status {@code true} for a burning light, {@code false} otherwise
     */
    @PUT
    @Path("families/{family}/lights/{light}/status")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String serviceFamilyLightStatusPut(@PathParam("family") String family,
                                              @PathParam("light") String light,
                                              String status) {
        LOGGER.info("serviceFamilyLightStatusPut('{}','{}','{}'): called", family, light, status);
        Boolean checkedStatus = Boolean.valueOf(status);

        Map<String, Boolean> familyLights = lights.get(family);
        if (null == familyLights) {
            familyLights = new ConcurrentHashMap<>();
            lights.put(family, familyLights);
        }
        familyLights.put(light, checkedStatus);

        return "" + checkedStatus;
    }

    // ----------------------------------------------------------------------

    public String toString() {
        String ret = new ToStringBuilder(this)
                .append("lights", lights)
                .toString();
        return ret;
    }

    // ----------------------------------------------------------------------

    /**
     * A bean for this service's status.
     */
    public static class Status {
        private int familiesCount = 0;
        private int lightsCount = 0;

        /**
         * Number of families
         */
        public int getFamiliesCount() {
            return familiesCount;
        }

        public void setFamiliesCount(int familiesCount) {
            this.familiesCount = familiesCount;
        }

        /**
         * Number of lights.
         */
        public int getLightsCount() {
            return lightsCount;
        }

        public void setLightsCount(int lightsCount) {
            this.lightsCount = lightsCount;
        }

        @Override
        public String toString() {
            String ret = "Serving " + getFamiliesCount() + " families with " + getLightsCount() + " lights.";
            return ret;
        }

    }

}
