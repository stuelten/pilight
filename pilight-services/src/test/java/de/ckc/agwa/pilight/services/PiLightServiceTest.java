/*
 * Copyright (c) 2015 Timo Stülten <timo.stuelten@googlemail.com>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
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
 * Tests the {@link de.ckc.agwa.pilight.services.PiLightService}.
 *
 * @author Timo Stülten
 */
public class PiLightServiceTest extends JerseyTest {

    public static final String PATH_PREFIX = "/pilight";

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
        final WebTarget target = target(PATH_PREFIX + "/status");

        {
            String serverStatus = target.request(MediaType.TEXT_PLAIN).get(String.class);
            Assert.assertNotNull("Server status must not be null", serverStatus);
        }
        {
            PiLightService.Status serverStatus = target.request(MediaType.APPLICATION_JSON).get(PiLightService.Status.class);
            Assert.assertNotNull("Server status must not be null", serverStatus);
            Assert.assertThat(serverStatus.getFamiliesCount(), IsEqual.equalTo(0));
            Assert.assertThat(serverStatus.getLightsCount(), IsEqual.equalTo(0));
        }
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

        String path = PATH_PREFIX + "/families/" + FAMILY + "/lights/" + LIGHT + "/status/";
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

        PiLightService.Status serverStatus = target(PATH_PREFIX + "/status").request(MediaType.APPLICATION_JSON_TYPE).get(PiLightService.Status.class);
        Assert.assertNotNull("Server status must not be null", serverStatus);
        Assert.assertThat("Status must know one family", serverStatus.getFamiliesCount(), IsEqual.equalTo(1));
        Assert.assertThat("Status must know one lamp", serverStatus.getLightsCount(), IsEqual.equalTo(1));
    }

    /**
     * Search status off families.
     */
    @Test
    public void testStatusFamiliesAndLamps() {
        final String FAMILY = "testFamily";
        final String LIGHT = "testLight";
        String jsonTRUE = Boolean.TRUE.toString();
        final Entity<String> LIGHT_ON = Entity.json(jsonTRUE);
        String jsonFALSE = Boolean.FALSE.toString();
        final Entity<String> LIGHT_OFF = Entity.json(jsonFALSE);

        {
            // Create light
            String path = PATH_PREFIX + "/families/" + FAMILY + "/lights/" + LIGHT + "/status/";
            target(path).request().put(LIGHT_ON);

            String lightMustBeOn = target(path).request().get(String.class);
            Assert.assertTrue("Light must be on.", Boolean.valueOf(lightMustBeOn));
        }

        // Test existence
        {
            String path = PATH_PREFIX + "/info/families";
            String statusFamilies = target(path).request().get(String.class);
            Assert.assertTrue("Status must know " + FAMILY, statusFamilies.contains(FAMILY));
        }
        {
            String path = PATH_PREFIX + "/" + "families/{family}/info/lights".replace("{family}", FAMILY);
            String statusLights = target(path).request().get(String.class);
            Assert.assertTrue("Status must know " + LIGHT, statusLights.contains(LIGHT));
        }

    }
}
