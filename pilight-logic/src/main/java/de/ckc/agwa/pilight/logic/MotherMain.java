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

import de.ckc.agwa.pilight.io.PiLightLamp;
import de.ckc.agwa.pilight.io.PiLightSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;

/**
 * The logic with a switch for mother's lamp.
 *
 * @author Timo Stülten
 */
@Singleton
public class MotherMain implements Runnable, PiLightSwitch.StateChangeListener {
    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MotherMain.class);

    // ----------------------------------------------------------------------

    /**
     * The switches control the lamps
     */
    @Inject
    List<PiLightSwitch> switches;

    /**
     * The lamps are switches on and off.
     */
    @Inject
    List<PiLightLamp> lamps;

    // ----------------------------------------------------------------------

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
        PiLightSwitch mothersLight = switches.iterator().next();

        mothersLight.addStateChangeListener(this);
    }

    @Override
    public void stateChanged(boolean state) {
        LOGGER.info("Switch changed to '{}'", state);

        for (PiLightLamp lamp : lamps) {
            LOGGER.info("Switch '{}' to '{}'", lamp, state);
            lamp.setOn(state);
        }

    }

}

