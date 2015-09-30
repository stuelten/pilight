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

package de.ckc.agwa.pilight.services.json;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Startup logic for RESTful JSON service.
 */
public class PiLightJsonServiceMain {
    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightJsonServiceMain.class);

    // ----------------------------------------------------------------------

    /**
     * Search for service classes below this package root.
     */
    private static final String SERVICE_ROOT = PiLightJsonServiceMain.class.getPackage().getName();

    /**
     * Base URI for this app
     */
    public static final URI BASE_URI = URI.create("http://localhost:9998/");

    // ----------------------------------------------------------------------

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     */
    @SuppressWarnings({"UseOfSystemOutOrSystemErr", "MethodCanBeVariableArityMethod"})
    public static void main(String[] args) {
        try {
            System.out.println("Start PiLightJsonServiceMain...");

            URI baseUri;
            if (args.length == 1) {
                baseUri = URI.create(args[0]);
            } else {
                baseUri = BASE_URI;
            }

            System.out.println("Start application for URL " + baseUri);
            ResourceConfig resourceConfig = createApp();
            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, resourceConfig);
            System.out.println("Application running...");

            System.out.println("Hit enter to stop it...");

            //noinspection ResultOfMethodCallIgnored
            System.in.read();
            server.shutdownNow();
        } catch (IOException ex) {
            LOGGER.error("" + ex, ex);
        }
    }

    // ----------------------------------------------------------------------

    /**
     * Configures Jackson/Jersey to use classes in package {@link #SERVICE_ROOT}
     * and the {@link MoxyJsonConfig} from {@link #createMoxyJsonResolver()}
     * for resource lookup.
     *
     * @return the config
     */
    public static ResourceConfig createApp() {
        return new ResourceConfig().
                packages(SERVICE_ROOT).
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
