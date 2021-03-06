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
import org.jglue.cdiunit.AdditionalPackages;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * Tests {@link de.ckc.agwa.pilight.logic.MotherMain} with CDI test setup.
 *
 * @author Timo Stülten
 */
@AdditionalPackages(PiLightMockFactory.class)
@RunWith(CdiRunner.class)
public class MotherMainTest {
    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MotherMainTest.class);

    @Inject
    List<PiLightSwitch> switches;

    @Inject
    List<PiLightLamp> lamps;

    @Inject
    MotherMain motherMain;

    @Test
    public void testMother() {
        LOGGER.info("Test switches '{}'", switches);
        LOGGER.info("Test lamps '{}'", lamps);

        MockPiLightSwitch mothersSwitch = (MockPiLightSwitch) switches.get(0);

        MockPiLightLamp mothersLamp = (MockPiLightLamp) lamps.get(0);
        MockPiLightLamp sistersLamp = (MockPiLightLamp) lamps.get(1);
        MockPiLightLamp brothersLamp = (MockPiLightLamp) lamps.get(2);

        motherMain.run();

        Assert.assertTrue("mother is dark.", !mothersLamp.isOn());
        Assert.assertTrue("sister is dark.", !sistersLamp.isOn());
        Assert.assertTrue("brother is dark.", !brothersLamp.isOn());

        mothersSwitch.setOn(true);

        Assert.assertTrue("mother must shine.", mothersLamp.isOn());
        Assert.assertTrue("sister must shine.", sistersLamp.isOn());
        Assert.assertTrue("brother must shine.", brothersLamp.isOn());
    }

}
