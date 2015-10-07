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

package de.ckc.agwa.pilight.services.json.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.ckc.agwa.pilight.services.Family;
import de.ckc.agwa.pilight.services.PiLightService;
import de.ckc.agwa.pilight.services.PiLightServiceStatus;
import de.ckc.agwa.pilight.services.json.PiLightRestfulService;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.net.URI;

/**
 * A client sending and receiving status updates for lights.
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

    // ----------------------------------------------------------------------

    /**
     * Used by all methods
     */
    private Client client = Client.create();

    /**
     * Base URI prefix for all services.
     */
    private String baseUri;

    // ----------------------------------------------------------------------

    /**
     * Creates a simple client.
     *
     * @param baseUri Base URI prefix for all services.
     */
    public PiLightServiceClient(String baseUri) {
        this.baseUri = baseUri;
    }

    // ----------------------------------------------------------------------

    /**
     * Creates the absolute URI to call for the given service URI.
     *
     * @param serviceUriRelative the relative URI for some service
     * @return the absolute URI to call
     */
    private URI createServiceUri(String serviceUriRelative) {
        URI ret = URI.create(baseUri + "/" + serviceUriRelative).normalize();
        return ret;
    }

    @Override
    public String serviceStatusPlain() {
        String ret = null;
        try {
            String servicePath = PiLightRestfulService.SERVICE_STATUS_PATH;
            URI serviceUri = createServiceUri(servicePath);

            WebResource webResource = client.resource(serviceUri);
            ClientResponse response = webResource.type(MediaType.TEXT_PLAIN)
                    .get(ClientResponse.class);

            if (HTTP_STATUS_200_OK == response.getStatus()) {
                // get result
                ret = response.getEntity(String.class);
            } else {
                LOGGER.info("Response: '()'", response);
            }
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e);
        }
        return ret;
    }

    @Override
    public PiLightServiceStatus serviceStatus() {
        PiLightServiceStatus ret = null;
        try {
            String servicePath = PiLightRestfulService.SERVICE_STATUS_PATH;
            URI serviceUri = createServiceUri(servicePath);

            WebResource webResource = client.resource(serviceUri);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
                    .get(ClientResponse.class);

            if (HTTP_STATUS_200_OK == response.getStatus()) {
                // get result
                String jsonResult = response.getEntity(String.class);
                ObjectMapper objectMapper = new ObjectMapper();
                ret = objectMapper.readValue(jsonResult, PiLightServiceStatus.class);
            } else {
                LOGGER.info("Response: '()'", response);
            }
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e);
        }
        return ret;
    }

    @Override
    public String[] serviceKnownFamilyNames() {
        String[] ret = null;
        try {
            String servicePath = PiLightRestfulService.SERVICE_KNOWN_FAMILY_NAMES_PATH;
            URI serviceUri = createServiceUri(servicePath);

            WebResource webResource = client.resource(serviceUri);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
                    .get(ClientResponse.class);

            if (HTTP_STATUS_200_OK == response.getStatus()) {
                // get result
                String jsonResult = response.getEntity(String.class);
                ObjectMapper objectMapper = new ObjectMapper();
                ret = objectMapper.readValue(jsonResult, String[].class);
            } else {
                LOGGER.info("Response: '()'", response);
            }
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e);
        }
        return ret;
    }

    @Override
    public Family serviceFamilyInfo(String family) {
        Family ret = null;
        try {
            String servicePath = PiLightRestfulService.SERVICE_FAMILY_INFO_LIGHTS_TEMPLATE //
                    .replace(PiLightRestfulService.TEMPLATE_PARAM_FAMILY, family);

            URI serviceUri = createServiceUri(servicePath);

            WebResource webResource = client.resource(serviceUri);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
                    .get(ClientResponse.class);

            if (HTTP_STATUS_200_OK == response.getStatus()) {
                // get result
                String jsonResult = response.getEntity(String.class);
                ObjectMapper objectMapper = new ObjectMapper();
                ret = objectMapper.readValue(jsonResult, Family.class);
                // ret = response.getEntity(Family.class);
            } else {
                LOGGER.info("Response for '()': '()'", family, response);
            }
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e);
        }
        return ret;
    }

    @Override
    public Boolean serviceFamilyLightStatusGet(String family, String light) {
        Boolean ret = Boolean.FALSE;
        try {
            String servicePath = PiLightRestfulService.SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE //
                    .replace(PiLightRestfulService.TEMPLATE_PARAM_FAMILY, family) //
                    .replace(PiLightRestfulService.TEMPLATE_PARAM_LIGHT, light);
            URI serviceUri = createServiceUri(servicePath);

            WebResource webResource = client.resource(serviceUri);
            ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON)
                    .get(ClientResponse.class);

            if (HTTP_STATUS_200_OK == response.getStatus()) {
                String output = response.getEntity(String.class);

                LOGGER.debug("Response for '()', '()': '()'", family, light, output);

                ret = Boolean.valueOf(output);
            } else {
                LOGGER.info("Response for '()', '()': '()'", family, light, response);
            }
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e);
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
            URI serviceUri = createServiceUri(servicePath);

            String input = "" + status;

            WebResource webResource = client.resource(serviceUri);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
                    .put(ClientResponse.class, input);

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
