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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ckc.agwa.pilight.services.Families;
import de.ckc.agwa.pilight.services.Family;
import de.ckc.agwa.pilight.services.LightState;
import de.ckc.agwa.pilight.services.PiLightService;
import de.ckc.agwa.pilight.services.PiLightServiceStatus;
import de.ckc.agwa.pilight.services.rest.PiLightRestfulService;
import de.ckc.agwa.pilight.services.rest.PiLightRestfulServiceMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * A de.ckc.agwa.pilight.services.client sending and receiving status updates for lights.
 *
 * @author Timo Stülten
 */
public class PiLightServiceClient implements PiLightService {
    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightServiceClient.class);
    // ----------------------------------------------------------------------

    public static final int HTTP_STATUS_200_OK = 200;
    public static final int HTTP_STATUS_201_CREATED = 201;
    public static final int HTTP_STATUS_204_NO_CONTENT = 204;

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
        LOGGER.info("setBaseUri('{}'): called", baseUri);
        this.baseUri = baseUri;
        if (null != baseUri) {
            Client client = ClientBuilder.newClient(PiLightRestfulServiceMain.createConfig());
            webTarget = client.target(baseUri);
        }
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
            LOGGER.warn("Ignoring exception for request '{}'", e.getMessage());
            ret = "Error: " + e.getLocalizedMessage();
        }
        return ret;
    }

    @Override
    public PiLightServiceStatus serviceStatus() {
        PiLightServiceStatus ret;
        try {
            String payload = webTarget
                    .path(PiLightRestfulService.SERVICE_STATUS_PATH)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(String.class);

            ObjectMapper mapper = new ObjectMapper();
            ret = mapper.readValue(payload, PiLightServiceStatus.class);
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e.getMessage());
            ret = null;
        }
        return ret;
    }

    @Override
    public Families serviceKnownFamilies() {
        Families ret;
        try {
            String payload = webTarget
                    .path(PiLightRestfulService.SERVICE_KNOWN_FAMILY_NAMES_PATH)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(String.class);

            ObjectMapper mapper = new ObjectMapper();
            ret = mapper.readValue(payload, Families.class);
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e.getMessage());
            ret = null;
        }
        return ret;
    }

    @Override
    public Family serviceFamilyInfo(String familyName) {
        Family ret;
        try {
            String servicePath = PiLightRestfulService.SERVICE_FAMILY_INFO_LIGHTS_TEMPLATE //
                    .replace(PiLightRestfulService.TEMPLATE_PARAM_FAMILY, familyName);

            String payload = webTarget
                    .path(servicePath)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(String.class);

            ObjectMapper mapper = new ObjectMapper();
            ret = mapper.readValue(payload, Family.class);
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e.getMessage());
            ret = null;
        }
        return ret;
    }

    @Override
    public LightState serviceFamilyLightStatusGet(String familyName, String lightName) {
        LightState ret;
        try {
            String servicePath = PiLightRestfulService.SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE //
                    .replace(PiLightRestfulService.TEMPLATE_PARAM_FAMILY, familyName) //
                    .replace(PiLightRestfulService.TEMPLATE_PARAM_LIGHT, lightName);

            String payload = webTarget
                    .path(servicePath)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(String.class);

            ObjectMapper mapper = new ObjectMapper();
            ret = mapper.readValue(payload, LightState.class);
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e.getMessage());
            ret = new LightState(false);
        }
        return ret;
    }

    @Override
    public LightState serviceFamilyLightStatusPut(String familyName, String lightName, Boolean state) {
        LightState lightState = new LightState(state);
        return serviceFamilyLightStatusPut(familyName, lightName, lightState);
    }

    @Override
    public LightState serviceFamilyLightStatusPut(String familyName, String lightName, LightState state) {
        LightState ret = null;
        try {
            String servicePath = PiLightRestfulService.SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE //
                    .replace(PiLightRestfulService.TEMPLATE_PARAM_FAMILY, familyName) //
                    .replace(PiLightRestfulService.TEMPLATE_PARAM_LIGHT, lightName);

            ObjectMapper mapper = new ObjectMapper();
            String payloadRequest = mapper.writeValueAsString(state);
            Entity<String> entity = Entity.entity(payloadRequest, MediaType.APPLICATION_JSON_TYPE);

            String payloadResponse = webTarget
                    .path(servicePath)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .put(entity, String.class);

            ret = mapper.readValue(payloadResponse, LightState.class);
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e.getMessage());
        }
        if (null == ret) {
            LOGGER.warn("Response for '{}', '{}', '{}' did not return light's state but null",
                    familyName, lightName, state);
        }
        return ret;
    }

}
