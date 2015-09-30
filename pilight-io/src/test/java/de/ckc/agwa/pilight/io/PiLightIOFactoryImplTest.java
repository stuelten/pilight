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

package de.ckc.agwa.pilight.io;

import org.junit.After;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PiLightIOFactoryImplTest {

    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightIOFactoryImplTest.class);


    private static PiLightIOFactoryImpl factory = null;

    public static boolean isRaspi() {
        boolean ret = "arm".equalsIgnoreCase(System.getProperty("os.arch"));
        return ret;
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Assume.assumeTrue("Running on Raspi", isRaspi());
        LOGGER.info("start setup Test...");
        factory = new PiLightIOFactoryImpl();
        LOGGER.info("end setup Test.");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test(timeout = 60000)
    public void testSwitchAndBlink() throws Exception {

        factory.getSwitches().get(0).addStateChangeListener((changedSwitch, state) -> {
            LOGGER.info("State change '{}'", state);

            factory.getLamps().get(0).setOn(factory.getSwitches().get(0).isOn());
        });

        // let it run
        PiLightIOHelper.sleep(600000);
    }

}
