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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

/**
 * A converter for {@link PiLightServiceStatus}.
 *
 * @author Timo Stülten
 */
@Provider
@Produces({"text/plain", "application/json"})
public class PiLightServiceStatusConverter extends AbstractMessageBodyWriter<PiLightServiceStatus> {
    /**
     * The {@link Logger}
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightServiceStatusConverter.class);

    public PiLightServiceStatusConverter() {
        super(PiLightServiceStatus.class);
        LOGGER.info("Instance created.");
    }
}
