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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * A client sending and receiving status updates for lights.
 *
 * @author Timo Stülten
 */
public class PiLightServiceClient {
    /**
     * Base URI for Service
     */
    public static final String SERVICE_PREFIX = "pilight/";

    // ----------------------------------------------------------------------
    /**
     * Base template for path for lamp status
     */
    public static final String LAMP_INFO_URI_TEMPLARE = SERVICE_PREFIX + "families/{family}/lights/{light}/status";
    public static final String LAMP_INFO_URI_TEMPLARE_FAMILY = "{family}";
    public static final String LAMP_INFO_URI_TEMPLARE_LIGHT = "{light}";
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

    /**
     * GETs the status of some light of some family.
     *
     * @param family the family
     * @param light  the light
     * @return {@code true} if the light is shining.
     */
    public boolean getLightStatus(String family, String light) {
        boolean ret = false;
        try {
            String getLampStatusPath = LAMP_INFO_URI_TEMPLARE //
                    .replace(LAMP_INFO_URI_TEMPLARE_FAMILY, family) //
                    .replace(LAMP_INFO_URI_TEMPLARE_LIGHT, light);

            URI getLampStatusUri = createServiceUri(getLampStatusPath);

            WebResource webResource = client.resource(getLampStatusUri);
            ClientResponse response = webResource.accept("application/json")
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

    /**
     * PUTs the status of some light of some family.
     *
     * @param family the family
     * @param light  the light
     * @param status {@code true} if the light is shining.
     * @return {@code true} if the service was called successfully.
     */
    public boolean setLightStatus(String family, String light, boolean status) {
        boolean ret = false;
        try {
            String getLampStatusPath = LAMP_INFO_URI_TEMPLARE //
                    .replace(LAMP_INFO_URI_TEMPLARE_FAMILY, family) //
                    .replace(LAMP_INFO_URI_TEMPLARE_LIGHT, light);

            URI getLampStatusUri = createServiceUri(getLampStatusPath);
            String input = "" + status;

            WebResource webResource = client.resource(getLampStatusUri);
            ClientResponse response = webResource.type("application/json")
                    .put(ClientResponse.class, input);

            if (200 == response.getStatus() || 201 == response.getStatus()) {
                // Could set new status
                ret = true;
            } else {
                LOGGER.info("Response for '()', '()': '()'", family, light, response);
            }
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception for request '{}'", e);
        }
        return ret;
    }

}
