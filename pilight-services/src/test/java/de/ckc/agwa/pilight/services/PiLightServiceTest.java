/*
 * Copyright (c) 2014 Timo Stülten <timo.stuelten@googlemail.com>
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

//    @Before
//    public void setUp() throws Exception {
//        // start the server
//        server = PiLightMain.startServer();
//
//        // create the client
//        // Client client = ClientBuilder.newClient();
//
//        final Client client = ClientBuilder.newBuilder()
////                // The line below that registers MOXy feature can be
////                // omitted if FEATURE_AUTO_DISCOVERY_DISABLE is
////                // not disabled.
//                .register(MoxyJsonFeature.class)
//                .register(JsonConfigResolver.class)
//                .build();
//
//        // uncomment the following line if you want to enable
//        // support for JSON in the client (you also have to uncomment
//        // dependency on jersey-media-json module in pom.xml and Main.startServer())
//        // --
//        // client.configuration().enable(new JsonJaxbFeature());
//
//        target = client.target(PiLightMain.BASE_URI);
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        server.shutdownNow();
//    }

    // ----------------------------------------------------------------------

    /**
     * Request the server status.
     */
    @Test
    public void testServerStatusPlain() {
        final WebTarget target = target("/pilight");

        {
            String serverStatus = target.request(MediaType.TEXT_PLAIN).get(String.class);
            Assert.assertNotNull("Server status must not be null", serverStatus);
        }
        {
            PiLightService.Status serverStatus = target.request(MediaType.APPLICATION_JSON).get(PiLightService.Status.class);
            Assert.assertNotNull("Server status must not be null", serverStatus);
            Assert.assertThat(serverStatus.familiesCount, IsEqual.equalTo(0));
            Assert.assertThat(serverStatus.lightsCount, IsEqual.equalTo(0));
        }
    }

    /**
     * Switch a lamp on and off again.
     */
    @Test
    public void testPutGet() {
        final String FAMILY = "testfamily";
        final String LIGHT = "testlight";
        final Entity<String> LIGHT_ON = Entity.json(Boolean.TRUE.toString());
        final Entity<String> LIGHT_OFF = Entity.json(Boolean.FALSE.toString());

        {
            /* Response responseLightOn = */
            target("/pilight/" + FAMILY + "/" + LIGHT + "/status/").request().put(LIGHT_ON);

            String lightMustBeOn = target("/pilight/" + FAMILY + "/" + LIGHT + "/status").request().get(String.class);
            Assert.assertTrue("Light must be on.", Boolean.valueOf(lightMustBeOn));
        }
        {
            /* Response responseLightOff = */
            target("/pilight/" + FAMILY + "/" + LIGHT + "/status/").request().put(LIGHT_OFF);

            String lightMustBeOff = target("/pilight/" + FAMILY + "/" + LIGHT + "/status").request().get(String.class);
            Assert.assertFalse("Light must be off.", Boolean.valueOf(lightMustBeOff));
        }

        PiLightService.Status serverStatus = target("/pilight").request(MediaType.APPLICATION_JSON_TYPE).get(PiLightService.Status.class);
        Assert.assertNotNull("Server status must not be null", serverStatus);
        Assert.assertThat("Status must know one family", serverStatus.familiesCount, IsEqual.equalTo(1));
        Assert.assertThat("Status must know one lamp", serverStatus.lightsCount, IsEqual.equalTo(1));
    }

}
