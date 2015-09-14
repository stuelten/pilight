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

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

/**
 * Tests the {@link PiLightServiceImpl}.
 *
 * @author Timo Stülten
 */
public class PiLightServiceTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        return PiLightMain.createApp();
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(PiLightMain.createMoxyJsonResolver());
    }

    // ----------------------------------------------------------------------

    /**
     * Request the server status.
     */
    @Test
    public void testServerStatusPlain() {
        final WebTarget target = target(PiLightServiceImpl.SERVICE_PREFIX + PiLightServiceImpl.SERVICE_STATUS_PATH);

        {
            String serverStatus = target.request(MediaType.TEXT_PLAIN).get(String.class);
            Assert.assertNotNull("Server status must not be null", serverStatus);
        }
        {
            PiLightServiceStatus serverStatus = target.request(MediaType.APPLICATION_JSON).get(PiLightServiceStatus.class);
            Assert.assertNotNull("Server status must not be null", serverStatus);
            Assert.assertThat(serverStatus.getFamiliesCount(), IsEqual.equalTo(0));
            Assert.assertThat(serverStatus.getLightsCount(), IsEqual.equalTo(0));
        }
    }


    /**
     * Test if an unknown light is returned as off
     */
    @Test
    public void testUnknownLightIsOff() {
        final String FAMILY = "testFamily";
        final String LIGHT = "unknownLight" + (long) ((double) Long.MAX_VALUE * Math.random());

        String path = PiLightServiceImpl.SERVICE_PREFIX
                + PiLightServiceImpl.SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE
                .replace(PiLightServiceImpl.TEMPLATE_PARAM_FAMILY, FAMILY)
                .replace(PiLightServiceImpl.TEMPLATE_PARAM_LIGHT, LIGHT);
        {
            String lightMustBeOff = target(path).request().get(String.class);
            Assert.assertFalse("Light must be off.", Boolean.valueOf(lightMustBeOff));
        }

        PiLightServiceStatus serverStatus = target(PiLightServiceImpl.SERVICE_PREFIX + PiLightServiceImpl.SERVICE_STATUS_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE).get(PiLightServiceStatus.class);
        Assert.assertNotNull("Server status must not be null", serverStatus);
    }

    /**
     * Switch a lamp on and off again.
     */
    @Test
    public void testPutGet() {
        final String FAMILY = "testFamily";
        final String LIGHT = "testLight";
        String jsonTRUE = Boolean.TRUE.toString();
        final Entity<String> LIGHT_ON = Entity.json(jsonTRUE);
        String jsonFALSE = Boolean.FALSE.toString();
        final Entity<String> LIGHT_OFF = Entity.json(jsonFALSE);

        String path = PiLightServiceImpl.SERVICE_PREFIX
                + PiLightServiceImpl.SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE
                .replace(PiLightServiceImpl.TEMPLATE_PARAM_FAMILY, FAMILY)
                .replace(PiLightServiceImpl.TEMPLATE_PARAM_LIGHT, LIGHT);
        // + "/families/" + FAMILY + "/lights/" + LIGHT + "/status/";
        {
            /* Response responseLightOn = */
            target(path).request().put(LIGHT_ON);

            String lightMustBeOn = target(path).request().get(String.class);
            Assert.assertTrue("Light must be on.", Boolean.valueOf(lightMustBeOn));
        }
        {
            /* Response responseLightOff = */
            target(path).request().put(LIGHT_OFF);

            String lightMustBeOff = target(path).request().get(String.class);
            Assert.assertFalse("Light must be off.", Boolean.valueOf(lightMustBeOff));
        }

        PiLightServiceStatus serverStatus = target(PiLightServiceImpl.SERVICE_PREFIX + PiLightServiceImpl.SERVICE_STATUS_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE).get(PiLightServiceStatus.class);
        Assert.assertNotNull("Server status must not be null", serverStatus);
        Assert.assertThat("Status must know one family", serverStatus.getFamiliesCount(), IsEqual.equalTo(1));
        Assert.assertThat("Status must know one light", serverStatus.getLightsCount(), IsEqual.equalTo(1));
    }

    /**
     * Search status off families.
     */
    @Test
    public void testStatusFamiliesAndLamps() {
        final String FAMILY = "testFamily";
        final String LIGHT1 = "testLight1";
        final String LIGHT2 = "testLight2";
        String jsonTRUE = Boolean.TRUE.toString();
        final Entity<String> LIGHT_ON = Entity.json(jsonTRUE);
        String jsonFALSE = Boolean.FALSE.toString();
        final Entity<String> LIGHT_OFF = Entity.json(jsonFALSE);

        {
            // Create light 1
            // String path = PiLightServiceImpl.SERVICE_PREFIX + "/families/" + FAMILY + "/lights/" + LIGHT1 +
            // "/status/";
            String path = PiLightServiceImpl.SERVICE_PREFIX
                    + PiLightServiceImpl.SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE
                    .replace(PiLightServiceImpl.TEMPLATE_PARAM_FAMILY, FAMILY)
                    .replace(PiLightServiceImpl.TEMPLATE_PARAM_LIGHT, LIGHT1);
            target(path).request().put(LIGHT_ON);

            String lightMustBeOn = target(path).request().get(String.class);
            Assert.assertTrue("Light must be on.", Boolean.valueOf(lightMustBeOn));
        }

        {
            // Create light 2
            // String path = PiLightServiceImpl.SERVICE_PREFIX + "/families/" + FAMILY + "/lights/" + LIGHT2 +
            // "/status/";
            String path = PiLightServiceImpl.SERVICE_PREFIX
                    + PiLightServiceImpl.SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE
                    .replace(PiLightServiceImpl.TEMPLATE_PARAM_FAMILY, FAMILY)
                    .replace(PiLightServiceImpl.TEMPLATE_PARAM_LIGHT, LIGHT2);
            target(path).request().put(LIGHT_ON);

            String lightMustBeOn = target(path).request().get(String.class);
            Assert.assertTrue("Light must be on.", Boolean.valueOf(lightMustBeOn));
        }

        // Test existence
        {
            String path = PiLightServiceImpl.SERVICE_PREFIX + "/info/families";
            String statusFamilies = target(path).request().get(String.class);
            Assert.assertTrue("Status must know " + FAMILY, statusFamilies.contains(FAMILY));
        }
        {
            // String path = PiLightServiceImpl.SERVICE_PREFIX + "/" + "families/{family}/info/lights".replace
            //    ("{family}", FAMILY);
            String path = PiLightServiceImpl.SERVICE_PREFIX
                    + PiLightServiceImpl.SERVICE_FAMILY_INFO_LIGHTS_TEMPLATE
                    .replace(PiLightServiceImpl.TEMPLATE_PARAM_FAMILY, FAMILY);

            String statusLights = target(path).request().get(String.class);
            Assert.assertTrue("Status must know " + LIGHT1, statusLights.contains(LIGHT1));
        }
        {
            // String path = PiLightServiceImpl.SERVICE_PREFIX + "/" + "families/{family}/info/lights".replace
            //    ("{family}", FAMILY);
            String path = PiLightServiceImpl.SERVICE_PREFIX
                    + PiLightServiceImpl.SERVICE_FAMILY_INFO_LIGHTS_TEMPLATE
                    .replace(PiLightServiceImpl.TEMPLATE_PARAM_FAMILY, FAMILY);

            String statusLights = target(path).request().get(String.class);
            Assert.assertTrue("Status must know " + LIGHT2, statusLights.contains(LIGHT2));
        }

    }
}
