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

package de.ckc.agwa.pilight.services.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ckc.agwa.pilight.services.Family;
import de.ckc.agwa.pilight.services.PiLightService;
import de.ckc.agwa.pilight.services.PiLightServiceStatus;
import de.ckc.agwa.pilight.services.rest.PiLightRestfulService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A de.ckc.agwa.pilight.services.client sending and receiving status updates for lights.
 *
 * @author Timo Stülten
 */
public class PiLightServiceClient implements PiLightService {
    public static final int HTTP_STATUS_200_OK = 200;

    // ----------------------------------------------------------------------
    public static final int HTTP_STATUS_201_CREATED = 201;
    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightServiceClient.class);

    // ----------------------------------------------------------------------
    /**
     * Used by all methods
     */
    private WebTarget webTarget;

    /**
     * Base URI prefix for all services.
     */
    private String baseUri;

    // ----------------------------------------------------------------------

    /**
     * Creates a simple service client.
     * The {@link #baseUri} has to be set, to be usable.
     */
    public PiLightServiceClient() {
    }

    /**
     * Creates a simple de.ckc.agwa.pilight.services.client.
     *
     * @param baseUri Base URI prefix for all services.
     */
    public PiLightServiceClient(String baseUri) {
        setBaseUri(baseUri);
    }

    public String getBaseUri() {
        return baseUri;
    }

    /**
     * Sets the base URI for all service calls.
     *
     * @param baseUri Base URI prefix for all services.
     */
    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
        if (null == baseUri) {
            reset();
        } else {
            init();
        }
    }

    private void reset() {
        webTarget = null;
    }

    private void init() {
        Client client = ClientBuilder.newClient();
        webTarget = client.target(baseUri);
    }

    // ----------------------------------------------------------------------

    @Override
    public String serviceStatusPlain() {
        String ret;
        try {
            ret = webTarget
                    .path(PiLightRestfulService.SERVICE_STATUS_PATH)
                    .request(MediaType.TEXT_PLAIN_TYPE)
                    .get(String.class);
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e);
            ret = "Error: " + e.getLocalizedMessage();
        }
        return ret;
    }

    @Override
    public PiLightServiceStatus serviceStatus() {
        PiLightServiceStatus ret;
        try {
            ret = webTarget
                    .path(PiLightRestfulService.SERVICE_STATUS_PATH)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(PiLightServiceStatus.class);
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e);
            ret = null;
        }
        return ret;
    }

    @Override
    public String[] serviceKnownFamilyNames() {
        String[] ret;
        try {
            String familyNames = webTarget
                    .path(PiLightRestfulService.SERVICE_KNOWN_FAMILY_NAMES_PATH)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(String.class);

            ObjectMapper mapper = new ObjectMapper();
            ret = mapper.readValue(familyNames, new TypeReference() {
            });
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e);
            ret = null;
        }
        return ret;
    }

    @Override
    public Family serviceFamilyInfo(String family) {
        Family ret;
        try {
            String servicePath = PiLightRestfulService.SERVICE_FAMILY_INFO_LIGHTS_TEMPLATE //
                    .replace(PiLightRestfulService.TEMPLATE_PARAM_FAMILY, family);

            ret = webTarget
                    .path(servicePath)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(Family.class);
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e);
            ret = null;
        }
        return ret;
    }

    @Override
    public Boolean serviceFamilyLightStatusGet(String family, String light) {
        Boolean ret;
        try {
            String servicePath = PiLightRestfulService.SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE //
                    .replace(PiLightRestfulService.TEMPLATE_PARAM_FAMILY, family) //
                    .replace(PiLightRestfulService.TEMPLATE_PARAM_LIGHT, light);
            // FIXME use Boolean instead of String!
            String output = webTarget
                    .path(servicePath)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(String.class);
            ret = Boolean.valueOf(output);
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e);
            ret = Boolean.FALSE;
        }
        return ret;
    }

    @Override
    public Boolean serviceFamilyLightStatusPut(String family, String light, Boolean status) {
        Boolean ret = Boolean.FALSE;
        try {
            String servicePath = PiLightRestfulService.SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE //
                    .replace(PiLightRestfulService.TEMPLATE_PARAM_FAMILY, family) //
                    .replace(PiLightRestfulService.TEMPLATE_PARAM_LIGHT, light);
            // FIXME use Boolean instead of String!
            Response response = webTarget
                    .path(servicePath)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .put(Entity.entity(status.toString(), MediaType.APPLICATION_JSON_TYPE));

            if (HTTP_STATUS_200_OK == response.getStatus()
                    || HTTP_STATUS_201_CREATED == response.getStatus()) {
                // Could set new status
                ret = Boolean.TRUE;
            } else {
                LOGGER.info("Response for '()', '()': '()'", family, light, response);
            }
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e);
        }
        return ret;
    }

}
