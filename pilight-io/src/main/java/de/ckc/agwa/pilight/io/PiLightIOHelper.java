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
package de.ckc.agwa.pilight.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper methods.
 *
 * @author Timo Stülten
 */
public class PiLightIOHelper {
    /** The logger for this class only. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightIOHelper.class);

    // ----------------------------------------------------------------------

    /**
     * Sleep without exception.
     * @param millis time to wait in millis
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.warn("Ignore Exception caught: {}", e, e);
        }
    }

}
