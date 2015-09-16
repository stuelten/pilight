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
import de.ckc.agwa.pilight.services.PiLightService;
import de.ckc.agwa.pilight.services.PiLightServiceStatus;
import de.ckc.agwa.pilight.services.json.PiLightJsonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;

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
        return "" + serviceStatus();
    }

    @Override
    public PiLightServiceStatus serviceStatus() {
        PiLightServiceStatus ret = null;
        try {
            String servicePath = PiLightJsonService.SERVICE_STATUS_PATH;
            URI serviceUri = createServiceUri(servicePath);

            WebResource webResource = client.resource(serviceUri);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
                    .get(ClientResponse.class);

            if (200 == response.getStatus()) {
                // get result
                ret = response.getEntity(PiLightServiceStatus.class);
            } else {
                LOGGER.info("Response: '()'", response);
            }
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e);
        }
        return ret;
    }

    @Override
    public Collection<String> serviceInfoFamilies() {
        Collection<String> ret = Collections.emptyList();
        try {
            String servicePath = PiLightJsonService.SERVICE_INFO_FAMILIES_PATH;
            URI serviceUri = createServiceUri(servicePath);

            WebResource webResource = client.resource(serviceUri);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
                    .get(ClientResponse.class);

            if (200 == response.getStatus()) {
                // get result
                //noinspection unchecked
                ret = (Collection<String>) response.getEntity(Collection.class);
            } else {
                LOGGER.info("Response: '()'", response);
            }
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e);
        }
        return ret;
    }

    @Override
    public Collection<String> serviceFamilyInfoLights(String family) {
        Collection<String> ret = Collections.emptyList();
        try {
            String servicePath = PiLightJsonService.SERVICE_FAMILY_INFO_LIGHTS_TEMPLATE //
                    .replace(PiLightJsonService.TEMPLATE_PARAM_FAMILY, family);

            URI serviceUri = createServiceUri(servicePath);

            WebResource webResource = client.resource(serviceUri);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
                    .get(ClientResponse.class);

            if (200 == response.getStatus()) {
                // get result
                //noinspection unchecked
                ret = (Collection<String>) response.getEntity(Collection.class);
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
            String servicePath = PiLightJsonService.SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE //
                    .replace(PiLightJsonService.TEMPLATE_PARAM_FAMILY, family) //
                    .replace(PiLightJsonService.TEMPLATE_PARAM_LIGHT, light);
            URI serviceUri = createServiceUri(servicePath);

            WebResource webResource = client.resource(serviceUri);
            ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON)
                    .get(ClientResponse.class);

            if (200 == response.getStatus()) {
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
            String servicePath = PiLightJsonService.SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE //
                    .replace(PiLightJsonService.TEMPLATE_PARAM_FAMILY, family) //
                    .replace(PiLightJsonService.TEMPLATE_PARAM_LIGHT, light);
            URI serviceUri = createServiceUri(servicePath);

            String input = "" + status;

            WebResource webResource = client.resource(serviceUri);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
                    .put(ClientResponse.class, input);

            if (200 == response.getStatus() || 201 == response.getStatus()) {
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
