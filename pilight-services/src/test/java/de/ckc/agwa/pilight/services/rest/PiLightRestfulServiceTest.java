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
import de.ckc.agwa.pilight.services.PiLightServiceStatus;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;

/**
 * Tests the {@link PiLightRestfulService}.
 *
 * @author Timo Stülten
 */
public class PiLightRestfulServiceTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        return PiLightRestfulServiceMain.createConfig();
    }

    // ----------------------------------------------------------------------

    /**
     * Request the server status.
     */
    @Test
    public void testServerStatusPlain() {
        final WebTarget target = target(PiLightRestfulService.SERVICE_PREFIX + PiLightRestfulService.SERVICE_STATUS_PATH);

        assertServerStatusPlainExists();
        assertServerStatusExists();
    }

    /**
     * Test if an unknown light is returned as off
     */
    @Test
    public void testUnknownLightIsOff() {
        final String FAMILY = "testFamily";
        final String LIGHT = "unknownLight" + (long) ((double) Long.MAX_VALUE * Math.random());

        Boolean state = getLightState(FAMILY, LIGHT);
        Assert.assertFalse("Light must be off.", state);

        PiLightServiceStatus serverStatus = getServiceStatus();
        Assert.assertNotNull("Server status must not be null", serverStatus);
    }

    /**
     * Switch a lamp on and off again.
     */
    @Test
    public void testPutGet() {
        final String FAMILY = "testFamily";
        final String LIGHT = "testLight";

        switchLight(FAMILY, LIGHT, Boolean.TRUE);
        assertFamilyExists(FAMILY);
        assertLightsExist(FAMILY, LIGHT);
        switchLight(FAMILY, LIGHT, Boolean.FALSE);
        assertFamilyExists(FAMILY);
        assertLightsExist(FAMILY, LIGHT);

        PiLightServiceStatus serverStatus = getServiceStatus();
        Assert.assertNotNull("Server status must not be null", serverStatus);
        Assert.assertThat("Status must know one family", serverStatus.getFamiliesCount(), IsEqual.equalTo(1));
        Assert.assertThat("Status must know one light", serverStatus.getLightsCount(), IsEqual.equalTo(1));

    }

    /**
     * Test life cycle of family with lights.
     */
    @Test
    public void testStatusFamiliesAndLights() {
        final String FAMILY = "testFamily";
        final String LIGHT1 = "testLight1";
        final String LIGHT2 = "testLight2";

        switchLight(FAMILY, LIGHT1, Boolean.TRUE);
        assertFamilyExists(FAMILY);
        assertLightsExist(FAMILY, LIGHT1);

        switchLight(FAMILY, LIGHT2, Boolean.FALSE);
        assertFamilyExists(FAMILY);
        assertLightsExist(FAMILY, LIGHT1, LIGHT2);
    }

    // ----------------------------------------------------------------------


    private String getServiceStatusPlain() {
        String path = PiLightRestfulService.SERVICE_PREFIX + PiLightRestfulService.SERVICE_STATUS_PATH;
        return target(path).request(MediaType.TEXT_PLAIN).get(String.class);
    }


    private PiLightServiceStatus getServiceStatus() {
        return target(PiLightRestfulService.SERVICE_PREFIX + PiLightRestfulService.SERVICE_STATUS_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE).get(PiLightServiceStatus.class);
    }

    private Family getFamily(String familyName) {
        String path = PiLightRestfulService.SERVICE_PREFIX
                + PiLightRestfulService.SERVICE_FAMILY_INFO_LIGHTS_TEMPLATE
                .replace(PiLightRestfulService.TEMPLATE_PARAM_FAMILY, familyName);

        return target(path).request(MediaType.APPLICATION_JSON_TYPE).get(Family.class);
    }

    private String[] getFamilies() {
        String path = PiLightRestfulService.SERVICE_PREFIX
                + PiLightRestfulService.SERVICE_KNOWN_FAMILY_NAMES_PATH;


        return target(path).request(MediaType.APPLICATION_JSON_TYPE).get(String[].class);
    }

    private Boolean getLightState(String familyName, String lightName) {
        Boolean ret;

        // Switch light
        String path = PiLightRestfulService.SERVICE_PREFIX
                + PiLightRestfulService.SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE
                .replace(PiLightRestfulService.TEMPLATE_PARAM_FAMILY, familyName)
                .replace(PiLightRestfulService.TEMPLATE_PARAM_LIGHT, lightName);

        // FIXME use Boolean instead of String!
        String output = target(path).request(MediaType.APPLICATION_JSON_TYPE).get(String.class);
        ret = Boolean.valueOf(output);

        return ret;
    }

    private void switchLight(String familyName, String lightName, Boolean state) {
        // Switch light
        String path = PiLightRestfulService.SERVICE_PREFIX
                + PiLightRestfulService.SERVICE_FAMILY_LIGHT_STATUS_TEMPLATE
                .replace(PiLightRestfulService.TEMPLATE_PARAM_FAMILY, familyName)
                .replace(PiLightRestfulService.TEMPLATE_PARAM_LIGHT, lightName);

        // FIXME use Boolean instead of String!
        Entity<String> entity = Entity.entity(state.toString(), MediaType.APPLICATION_JSON_TYPE);
        target(path).request(MediaType.APPLICATION_JSON_TYPE).put(entity);

        Boolean lightMustBeSet = getLightState(familyName, lightName);

        Assert.assertEquals("Light must be set.", state, lightMustBeSet);
    }

    // ----------------------------------------------------------------------

    private void assertServerStatusExists() {
        PiLightServiceStatus serverStatus = getServiceStatus();
        Assert.assertNotNull("Server status must not be null", serverStatus);
        Assert.assertThat(serverStatus.getFamiliesCount(), IsEqual.equalTo(0));
        Assert.assertThat(serverStatus.getLightsCount(), IsEqual.equalTo(0));
        Assert.assertNotNull(serverStatus.getFamilies());
        Assert.assertTrue(serverStatus.getFamilies().isEmpty());
    }

    private void assertServerStatusPlainExists() {
        String serverStatus = getServiceStatusPlain();
        Assert.assertNotNull("Server status must not be null", serverStatus);
    }

    private void assertFamilyExists(String familyName) {
        {
            String[] familyNames = getFamilies();
            Assert.assertNotNull(familyNames);
            Assert.assertTrue("Family must be known " + familyName, Arrays.asList(familyNames).contains(familyName));
        }
        {
            Family family = getFamily(familyName);
            Assert.assertNotNull("Family must exist " + familyName, family);
        }
    }

    private void assertLightsExist(String familyName, String... lights) {
        {
            String[] familyNames = getFamilies();
            Assert.assertNotNull(familyNames);
            Assert.assertTrue("Family must be known " + familyName, Arrays.asList(familyNames).contains(familyName));
        }
        {
            Family family = getFamily(familyName);
            for (String light : lights) {
                Assert.assertNotNull("Family must know " + light, family.getLight(light));
            }
        }
    }

}
