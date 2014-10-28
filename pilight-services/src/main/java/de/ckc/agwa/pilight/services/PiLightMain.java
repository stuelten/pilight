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

import javax.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class.
 */
public class PiLightMain {
    /** The logger for this class only. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightMain.class);

    /** Search for service classes below this package root. */
    public static final Package SERVICE_ROOT = PiLightMain.class.getPackage();

    /** Base URI for this app */
    private static final URI BASE_URI = URI.create("http://localhost:9998/jsonmoxy/");

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     */
    public static void main(String[] args) {
        try {
            System.out.println("JSON with MOXy Jersey Example App");

            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, createApp());

            System.out.println(String.format("Application started.%nHit enter to stop it..."));
            //noinspection ResultOfMethodCallIgnored
            System.in.read();
            server.shutdownNow();
        } catch (IOException ex) {
            LOGGER.error("" + ex, ex);
        }
    }

    // ----------------------------------------------------------------------

    public static ResourceConfig createApp() {
        return new ResourceConfig().
                packages(SERVICE_ROOT.getName()).
                register(createMoxyJsonResolver());
    }

    public static ContextResolver<MoxyJsonConfig> createMoxyJsonResolver() {
        final MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
        Map<String, String> namespacePrefixMapper = new HashMap<String, String>(1);
        namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        moxyJsonConfig.setNamespacePrefixMapper(namespacePrefixMapper).setNamespaceSeparator(':');
        return moxyJsonConfig.resolver();
    }

}
