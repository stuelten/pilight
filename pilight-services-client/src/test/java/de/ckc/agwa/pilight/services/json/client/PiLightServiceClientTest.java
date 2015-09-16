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

import de.ckc.agwa.pilight.services.PiLightServiceStatus;
import de.ckc.agwa.pilight.services.json.PiLightJsonService;
import de.ckc.agwa.pilight.services.json.PiLightJsonServiceMain;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Application;
import java.net.URI;
import java.util.Collection;

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

        return PiLightJsonServiceMain.createApp();
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(PiLightJsonServiceMain.createMoxyJsonResolver());
    }

    // ----------------------------------------------------------------------

    PiLightServiceClient serviceClient;

    @Before
    public void setup() {
        String serviceBaseUrl = URI.create(PiLightJsonServiceMain.BASE_URI.toString()
                + PiLightJsonService.SERVICE_PREFIX).normalize().toString();
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

        Collection<String> familyLights = serviceClient.serviceFamilyInfoLights(FAMILY);
        Assert.assertTrue(familyLights.isEmpty());
        serviceClient.serviceFamilyLightStatusPut(FAMILY, LIGHT1, true);
        serviceClient.serviceFamilyLightStatusPut(FAMILY, LIGHT2, true);

        familyLights = serviceClient.serviceFamilyInfoLights(FAMILY);
        Assert.assertFalse(familyLights.isEmpty());
        Assert.assertThat(familyLights.size(), IsEqual.equalTo(2));
    }

}