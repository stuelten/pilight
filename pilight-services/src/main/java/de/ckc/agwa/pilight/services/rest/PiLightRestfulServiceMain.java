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

import de.ckc.agwa.pilight.services.PiLightServiceStatus;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
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
public class PiLightRestfulServiceMain {
    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightRestfulServiceMain.class);

    // ----------------------------------------------------------------------

    /**
     * Base URI for this app.
     */
    public static final URI BASE_URI = URI.create("http://localhost:9998/");

    /**
     * Search for service classes in the service package and below.
     */
    private static final String DOMAIN_CLASS_PACKAGE = PiLightServiceStatus.class.getPackage().getName();

    // ----------------------------------------------------------------------

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     */
    @SuppressWarnings({"UseOfSystemOutOrSystemErr", "MethodCanBeVariableArityMethod"})
    public static void main(String[] args) {
        try {
            System.out.println("Start PiLightRestfulServiceMain...");

            URI baseUri;
            if (args.length == 1) {
                baseUri = URI.create(args[0]);
            } else {
                baseUri = BASE_URI;
            }

            System.out.println("Start application for URL " + baseUri);
            ResourceConfig resourceConfig = createConfig();
            HttpServer server = createServer(baseUri, resourceConfig);

            // Additionally listen on all interfaces
            System.out.println("Additionally listen on all interfaces on port 9997.");
            NetworkListener listenToAll = new NetworkListener("all", "0.0.0.0", 9997);
            server.addListener(listenToAll);
            server.start();

            System.out.println("Application is running...");

            System.out.println("Hit enter to stop.");
            //noinspection ResultOfMethodCallIgnored
            System.in.read();

            server.shutdownNow();
        } catch (IOException ex) {
            LOGGER.error("" + ex, ex);
        }
    }

    // ----------------------------------------------------------------------

    /**
     * Creates a grizzly http server listening not only on the
     * given uri but on all interfaces.
     *
     * @param baseUri        the URI to listen on.
     * @param resourceConfig the grizzly configuration
     * @return the configured server
     */
    public static HttpServer createServer(URI baseUri, ResourceConfig resourceConfig) {
        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(baseUri, resourceConfig);
        return httpServer;
    }

    /**
     * Configures Jackson/Jersey to use classes in package {@link #DOMAIN_CLASS_PACKAGE}
     * and the {@link MoxyJsonConfig} from {@link #createMoxyJsonResolver()}
     * for resource lookup.
     *
     * @return the config
     */
    public static ResourceConfig createConfig() {
        ResourceConfig ret = new ResourceConfig()
                .packages(true, DOMAIN_CLASS_PACKAGE)
                .register(createMoxyJsonResolver());
        LOGGER.debug("createConfig(): '{}'", ret);
        return ret;
    }

    public static ContextResolver<MoxyJsonConfig> createMoxyJsonResolver() {
        final MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
        Map<String, String> namespacePrefixMapper = new HashMap<>(1);
        namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        moxyJsonConfig.setNamespacePrefixMapper(namespacePrefixMapper).setNamespaceSeparator(':');
        return moxyJsonConfig.resolver();
    }

}
