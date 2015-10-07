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

import de.ckc.agwa.pilight.services.Family;
import de.ckc.agwa.pilight.services.PiLightServiceStatus;
import de.ckc.agwa.pilight.services.json.PiLightRestfulService;
import de.ckc.agwa.pilight.services.json.PiLightRestfulServiceMain;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Application;
import java.net.URI;
import java.util.Arrays;

/**
 * Tests the {@link PiLightServiceClient}.
 *
 * @author Timo Stülten
 */
public class PiLightServiceClientTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        return PiLightRestfulServiceMain.createApp();
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(PiLightRestfulServiceMain.createMoxyJsonResolver());
    }

    // ----------------------------------------------------------------------

    PiLightServiceClient serviceClient;

    @Before
    public void setup() {
        String serviceBaseUrl = URI.create(PiLightRestfulServiceMain.BASE_URI.toString()
                + PiLightRestfulService.SERVICE_PREFIX).normalize().toString();
        serviceClient = new PiLightServiceClient(serviceBaseUrl);
    }

    @Test
    public void testServiceStatusPlain() throws Exception {
        String res = serviceClient.serviceStatusPlain();
        Assert.assertNotNull(res);
        Assert.assertFalse(res.trim().isEmpty());
    }

    @Test
    public void testServiceStatus() throws Exception {
        PiLightServiceStatus res = serviceClient.serviceStatus();
        Assert.assertNotNull(res);
        Assert.assertEquals(0, res.getFamiliesCount());
        Assert.assertEquals(0, res.getLightsCount());
    }

    @Test
    public void testServiceInfoFamilies() throws Exception {
        final String FAMILY = "testFamily_" + System.nanoTime();
        final String LIGHT1 = "testLight1_" + System.nanoTime();
        final String LIGHT2 = "testLight2_" + System.nanoTime();

        Family family = serviceClient.serviceFamilyInfo(FAMILY);
        Assert.assertNull(family);

        Assert.assertFalse(serviceClient.serviceFamilyLightStatusGet(FAMILY, LIGHT1));
        Assert.assertFalse(serviceClient.serviceFamilyLightStatusGet(FAMILY, LIGHT2));

        serviceClient.serviceFamilyLightStatusPut(FAMILY, LIGHT1, true);
        Assert.assertTrue(serviceClient.serviceFamilyLightStatusGet(FAMILY, LIGHT1));
        Assert.assertFalse(serviceClient.serviceFamilyLightStatusGet(FAMILY, LIGHT2));
        serviceClient.serviceFamilyLightStatusPut(FAMILY, LIGHT2, true);
        Assert.assertTrue(serviceClient.serviceFamilyLightStatusGet(FAMILY, LIGHT1));
        Assert.assertTrue(serviceClient.serviceFamilyLightStatusGet(FAMILY, LIGHT2));

        family = serviceClient.serviceFamilyInfo(FAMILY);
        Assert.assertNotNull(family);
        Assert.assertThat(family.getLights().length, IsEqual.equalTo(2));

        String[] familyNames = serviceClient.serviceKnownFamilyNames();
        Assert.assertNotNull(familyNames);
        Assert.assertTrue(Arrays.asList(familyNames).contains(FAMILY));
    }

}