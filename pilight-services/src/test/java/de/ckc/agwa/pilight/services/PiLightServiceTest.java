/*
 * Copyright 2014 Timo Stülten <timo.stuelten@googlemail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package de.ckc.agwa.pilight.services;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assert;
import org.junit.Test;

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
//        target = client.target(PiLightServiceConstants.BASE_URI);
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
    public void testServerStatus() {
        final WebTarget target = target("/pilight");

        String serverStatus = target.request(MediaType.TEXT_PLAIN).get(String.class);
        Assert.assertNotNull("Server status must not be null", serverStatus);
    }

    /**
     * Switch a lamp on and off again.
     */
    @Test
    public void testPutGet() {
        final String TESTLAMP = "testlamp";
        final Entity<String> LAMP_ON = Entity.json(Boolean.TRUE.toString());
        final Entity<String> LAMP_OFF = Entity.json(Boolean.FALSE.toString());

        {
            Response responseLampOn = target("/pilight/" + TESTLAMP + "/status/").request().put(LAMP_ON);

            String lampMustBeOn = target("/pilight/" + TESTLAMP + "/status").request().get(String.class);
            Assert.assertTrue("Lamp must be on.", Boolean.valueOf(lampMustBeOn));
        }
        {
            Response responseLampOff = target("/pilight/" + TESTLAMP + "/status/").request().put(LAMP_OFF);

            String lampMustBeOff = target("/pilight/" + TESTLAMP + "/status").request().get(String.class);
            Assert.assertFalse("Lamp must be off.", Boolean.valueOf(lampMustBeOff));
        }

        String serverStatus = target("/pilight").request().get(String.class);
        Assert.assertNotNull("Server status must not be null", serverStatus);
        Assert.assertTrue("Status must know one lamp", serverStatus.contains(" 1 "));

    }

}
