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

package de.ckc.agwa.pilight.io;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PiLightIOFactoryImplTest {

    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightIOFactoryImplTest.class);


    static PiLightIOFactoryImpl factory;

    @BeforeClass
    public static void setUpClass() throws Exception {
        LOGGER.info("start setup Test...")
        factory = new PiLightIOFactoryImpl();
        LOGGER.info("end setup Test.")
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSwitchAndBlink() throws Exception {

        factory.getLightSensors().get(0).addStateChangeListener(new PiLightSensor.StateChangeListener() {
            @Override
            public void stateChanged(boolean state) {
                LOGGER.info("State change")
                factory.getSwitches().get(0).setOn(state);
            }
        });

        PiLightIOHelper.sleep(600000);
    }

}
