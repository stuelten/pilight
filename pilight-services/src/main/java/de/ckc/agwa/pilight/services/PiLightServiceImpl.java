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
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This service receives and serves the status of lights for some families.
 *
 * @author Timo Stülten
 */
@Path("/pilight")
public class PiLightServiceImpl implements PiLightService {
    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightServiceImpl.class);

    // ----------------------------------------------------------------------

    /**
     * Template for family parameter
     */
    public static final String PATH_PARAM_FAMILY = "family";
    public static final String TEMPLATE_PARAM_FAMILY = "{" + PATH_PARAM_FAMILY + "}";
    /**
     * Template for light parameter
     */
    public static final String PATH_PARAM_LIGHT = "light";
    public static final String TEMPLATE_PARAM_LIGHT = "{" + PATH_PARAM_LIGHT + "}";

    /**
     * Base URI for Service
     */
    public static final String SERVICE_PREFIX = "pilight/";
    /**
     * Base template for path for lamp state service.
     */
    public static final String SERVICE_STATUS_PATH = "status";
    /**
     * Base template for path for families info service.
     */
    public static final String SERVICE_INFO_FAMILIES_PATH = "info/families";
    /**
     * Base template for path for "family info about lights" service.
     */
    public static final String SERVICE_FAMILY_INFO_LIGHTS_TEMPLATE = "families/{family}/info/lights";
    /**
     * Base template for path for lamp state service.
     */
    public static final String SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE = "families/{family}/lights/{light}/status";

    // ----------------------------------------------------------------------

    /**
     * Contains all lights.
     */
    protected static Map<String, Map<String, Boolean>> lights = new ConcurrentHashMap<>();

    // ----------------------------------------------------------------------

    @Override
    @GET
    @Path(SERVICE_STATUS_PATH)
    @Produces(MediaType.TEXT_PLAIN)
    public String serviceStatusPlain() {
        LOGGER.debug("serviceStatusPlain(): Called");

        PiLightServiceStatus status = serviceStatus();

        String ret = status.toString();

        LOGGER.info("serviceStatusPlain(): return '{}'", ret);
        return ret;
    }

    @Override
    @GET
    @Path(SERVICE_STATUS_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public PiLightServiceStatus serviceStatus() {
        LOGGER.debug("serviceStatus(): Called");

        int familiesCount = lights.size();
        int lightsCount = lights.values().stream()
                .mapToInt(Map::size)
                .sum();

        PiLightServiceStatus ret = new PiLightServiceStatus();
        ret.setFamiliesCount(familiesCount);
        ret.setLightsCount(lightsCount);

        LOGGER.debug("serviceStatus(): return '{}'", ret);
        return ret;
    }

    @Override
    @GET
    @Path(SERVICE_INFO_FAMILIES_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<String> serviceInfoFamilies() {
        LOGGER.debug("serviceInfoFamilies(): Called");
        Collection<String> ret = lights.keySet();

        LOGGER.info("serviceInfoFamilies(): return '{}'", ret);
        return ret;
    }

    // ----------------------------------------------------------------------

    @Override
    @GET
    @Path(SERVICE_FAMILY_INFO_LIGHTS_TEMPLATE)
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<String> serviceFamilyInfoLights(@PathParam(PATH_PARAM_FAMILY) String family) {
        LOGGER.debug("serviceFamilyInfoLights('{}'): called", family);

        Map<String, Boolean> familyLights = lights.get(family);
        Collection<String> ret = Collections.emptyList();
        if (familyLights != null) {
            ret = familyLights.keySet();
        }

        LOGGER.info("serviceFamilyInfoLights('{}'): return '{}'", family, ret);
        return ret;
    }

    @Override
    @GET
    @Path(SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE)
    @Produces(MediaType.APPLICATION_JSON)
    public String serviceFamilyLightStatusGet(@PathParam(PATH_PARAM_FAMILY) String family,
                                              @PathParam(PATH_PARAM_LIGHT) String light) {
        LOGGER.debug("serviceFamilyLightStatusGet('{}','{}'): called", family, light);
        String status;

        Map<String, Boolean> familyLights = lights.get(family);
        if (null == familyLights) {
            status = Boolean.FALSE.toString();
        } else {
            Boolean maybeNull = familyLights.get(light);
            status = "" + (maybeNull == null ? Boolean.FALSE : maybeNull);
        }

        LOGGER.info("serviceFamilyLightStatusGet('{}', '{}'): return '{}'", family, light, status);
        return status;
    }

    @Override
    @PUT
    @Path(SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String serviceFamilyLightStatusPut(@PathParam(PATH_PARAM_FAMILY) String family,
                                              @PathParam(PATH_PARAM_LIGHT) String light,
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

}
