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

import de.ckc.agwa.pilight.io.MockPiLightLamp;
import de.ckc.agwa.pilight.io.MockPiLightSwitch;
import de.ckc.agwa.pilight.io.PiLightLamp;
import de.ckc.agwa.pilight.io.PiLightSwitch;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * A mock factory producing {@link MockPiLightSwitch}s
 * and {@link MockPiLightLamp}es.
 *
 * @author Timo Stülten
 */
@Singleton
public class PiLightMockFactory {

    private List<PiLightSwitch> sensors = new ArrayList<>();
    private List<PiLightLamp> switches = new ArrayList<>();

    public PiLightMockFactory() {
        PiLightSwitch mother = new MockPiLightSwitch("mother's lamp sensor");
        sensors.add(mother);

        PiLightLamp sister = new MockPiLightLamp("sister's lamp switch");
        PiLightLamp brother = new MockPiLightLamp("brother's lamp switch");
        switches.add(sister);
        switches.add(brother);
    }

    @Produces
    public List<PiLightSwitch> getLightSensors() {
        return sensors;
    }

    @Produces
    public List<PiLightLamp> getSwitches() {
        return switches;
    }

}
