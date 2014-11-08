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
package de.ckc.agwa.pilight.logic;

import de.ckc.agwa.pilight.io.MockPiLightSensor;
import de.ckc.agwa.pilight.io.MockPiLightSwitch;
import de.ckc.agwa.pilight.io.PiLightIOFactory;
import de.ckc.agwa.pilight.io.PiLightSensor;
import de.ckc.agwa.pilight.io.PiLightSwitch;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Timo Stülten
 */
@Singleton
public class TestPiLightIOFactory implements PiLightIOFactory {

    private List<PiLightSensor> sensors = new ArrayList<>();
    private List<PiLightSwitch> switches = new ArrayList<>();

    public TestPiLightIOFactory() {
        PiLightSensor mother = new MockPiLightSensor("mother's lamp sensor");
        sensors.add(mother);

        PiLightSwitch sister = new MockPiLightSwitch("sister's lamp switch");
        PiLightSwitch brother = new MockPiLightSwitch("brother's lamp switch");
        switches.add(sister);
        switches.add(brother);
    }

    @Override
    @Produces
    public List<PiLightSensor> getLightSensors() {
        return sensors;
    }

    @Override
    @Produces
    public List<PiLightSwitch> getSwitches() {
        return switches;
    }

}
