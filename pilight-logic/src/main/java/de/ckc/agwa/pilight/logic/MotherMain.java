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
package de.ckc.agwa.pilight.logic;

import de.ckc.agwa.pilight.io.PiLightSensor;
import de.ckc.agwa.pilight.io.PiLightSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;

/**
 * The logic with a sensor for mother's lamp and switches on all lamps.
 *
 * @author Timo Stülten
 */
@Singleton
public class MotherMain implements Runnable, PiLightSensor.StateChangeListener {
    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MotherMain.class);
    @Inject
    List<PiLightSensor> sensors;

    // ----------------------------------------------------------------------
    @Inject
    List<PiLightSwitch> switches;

    /**
     * Start with mother's
     */
    public static void main(String[] args) throws IOException {
        LOGGER.info("Start with arguments '{}'", (Object[]) args);

        // start MotherMain
        MotherMain motherMain = CDIContext.INSTANCE.getBean(MotherMain.class);
        motherMain.run();
    }

    // ----------------------------------------------------------------------

    @Override
    public void run() {
        PiLightSensor mothersLight = sensors.iterator().next();

        mothersLight.addStateChangeListener(this);
    }

    @Override
    public void stateChanged(boolean state) {
        LOGGER.info("Sensor switched to '{}'", state);
        for (PiLightSwitch lightSwitch : switches) {
            LOGGER.info("Switch '{}' to '{}'", lightSwitch, state);
            lightSwitch.setOn(state);
        }

    }

}

