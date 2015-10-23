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

package de.ckc.agwa.pilight.services.rest;

import de.ckc.agwa.pilight.services.Family;
import de.ckc.agwa.pilight.services.PiLightServiceImpl;
import de.ckc.agwa.pilight.services.PiLightServiceStatus;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * This service receives and serves the status of lights for some families.
 *
 * @author Timo Stülten
 */
@Singleton
@Path("/pilight")
public class PiLightRestfulService {
    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightRestfulService.class);

    // ----------------------------------------------------------------------

    /**
     * Base URI for Service
     */
    public static final String SERVICE_PREFIX = "pilight/";

    /**
     * Base template for path for lamp state service.
     */
    public static final String SERVICE_STATUS_PATH = "status";

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
     * Base template for path for families info service.
     */
    public static final String SERVICE_KNOWN_FAMILY_NAMES_PATH = "info/families";
    /**
     * Base template for path for "family info about lights" service.
     */
    public static final String SERVICE_FAMILY_INFO_LIGHTS_TEMPLATE = "families/" + TEMPLATE_PARAM_FAMILY +
            "/info/lights";
    /**
     * Base template for path for lamp state service.
     */
    public static final String SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE = "families/" + TEMPLATE_PARAM_FAMILY +
            "/lights/" + TEMPLATE_PARAM_LIGHT + "/status";

    // ----------------------------------------------------------------------

    /**
     * The service delegate.
     */
    // @Inject
    protected PiLightServiceImpl service;

    // ----------------------------------------------------------------------

    public PiLightRestfulService() {
        // FIXME
        service = new PiLightServiceImpl();
    }

    // ----------------------------------------------------------------------

    @GET
    @Path(SERVICE_STATUS_PATH)
    @Produces(MediaType.TEXT_PLAIN)
    public String serviceStatusPlain() {
        LOGGER.debug("serviceStatusPlain(): Called");

        String ret = service.serviceStatusPlain();

        LOGGER.info("serviceStatusPlain(): return '{}'", ret);
        return ret;
    }

    @GET
    @Path(SERVICE_STATUS_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public PiLightServiceStatus serviceStatus() {
        LOGGER.debug("serviceStatus(): Called");

        PiLightServiceStatus ret = service.serviceStatus();

        LOGGER.info("serviceStatus(): return '{}'", ret);
        return ret;
    }

    @GET
    @Path(SERVICE_KNOWN_FAMILY_NAMES_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public String[] serviceKnownFamilyNames() {
        LOGGER.debug("serviceKnownFamilyNames(): Called");

        String[] ret = service.serviceKnownFamilyNames();

        LOGGER.info("serviceKnownFamilyNames(): return '{}'", (Object) ret);
        return ret;
    }

    // ----------------------------------------------------------------------

    @GET
    @Path(SERVICE_FAMILY_INFO_LIGHTS_TEMPLATE)
    @Produces(MediaType.APPLICATION_JSON)
    public Family serviceFamilyInfoLights(@PathParam(PATH_PARAM_FAMILY) String family) {
        LOGGER.debug("serviceFamilyInfo('{}'): called", family);

        Family ret = service.serviceFamilyInfo(family);

        LOGGER.info("serviceFamilyInfo('{}'): return '{}'", family, ret);
        return ret;
    }

    @GET
    @Path(SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE)
    @Produces(MediaType.APPLICATION_JSON)
    public String serviceFamilyLightStatusGet(@PathParam(PATH_PARAM_FAMILY) String family,
                                              @PathParam(PATH_PARAM_LIGHT) String light) {
        LOGGER.debug("serviceFamilyLightStatusGet('{}','{}'): called", family, light);

        Boolean status = service.serviceFamilyLightStatusGet(family, light);

        LOGGER.info("serviceFamilyLightStatusGet('{}', '{}'): return '{}'", family, light, status);
        return "" + status;
    }

    @PUT
    @Path(SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String serviceFamilyLightStatusPut(@PathParam(PATH_PARAM_FAMILY) String family,
                                              @PathParam(PATH_PARAM_LIGHT) String light,
                                              String status) {
        String ret;

        LOGGER.info("serviceFamilyLightStatusPut('{}','{}','{}'): called", family, light, status);
        Boolean checkedStatus = Boolean.valueOf(status);
        Boolean serviceResponse = service.serviceFamilyLightStatusPut(family, light, checkedStatus);

        ret = "" + serviceResponse;
        LOGGER.info("serviceFamilyLightStatusPut('{}','{}','{}'): return '{}'", family, light, status, ret);
        return ret;
    }

    // ----------------------------------------------------------------------

    public String toString() {
        String ret = new ToStringBuilder(this)
                .append("service", service)
                .toString();
        return ret;
    }

}
