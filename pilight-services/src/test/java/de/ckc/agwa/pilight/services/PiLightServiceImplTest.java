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

package de.ckc.agwa.pilight.services;

import org.hamcrest.core.IsEqual;
import org.jglue.cdiunit.AdditionalPackages;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * Tests the {@link PiLightServiceImpl}.
 *
 * @author Timo Stülten
 */
@AdditionalPackages(PiLightServiceImpl.class)
@RunWith(CdiRunner.class)
public class PiLightServiceImplTest {

    @Inject
    PiLightService service;

    /**
     * Request the server status.
     */
    @Test
    public void testServerStatusPlain() {
        String statusPlain = service.serviceStatusPlain();
        Assert.assertNotNull(statusPlain);
        Assert.assertFalse(statusPlain.isEmpty());
    }

    /**
     * Test if an unknown light is returned as off
     */
    @Test
    public void testUnknownLightIsOff() {
        LightState lightState = service.serviceFamilyLightStatusGet("UnknownFamily", "UnknownLamp_" + System.nanoTime());
        Assert.assertFalse(lightState.isOn());
    }

    /**
     * Switch a lamp on and off again.
     */
    @Test
    public void testPutGet() {
        final String FAMILY = "testFamily_" + System.nanoTime();
        final String LIGHT = "testLight_" + System.nanoTime();

        {
            LightState lightState = service.serviceFamilyLightStatusGet(FAMILY, LIGHT);
            Assert.assertFalse(lightState.isOn());
        }
        {
            service.serviceFamilyLightStatusPut(FAMILY, LIGHT, true);
            LightState lightState = service.serviceFamilyLightStatusGet(FAMILY, LIGHT);
            Assert.assertTrue(lightState.isOn());
        }
        {
            service.serviceFamilyLightStatusPut(FAMILY, LIGHT, false);
            LightState lightState = service.serviceFamilyLightStatusGet(FAMILY, LIGHT);
            Assert.assertFalse(lightState.isOn());
        }
    }

    /**
     * Search status of families.
     */
    @Test
    public void testStatusFamiliesAndLamps() {
        final String FAMILY = "testFamily_" + System.nanoTime();
        final String LIGHT1 = "testLight1_" + System.nanoTime();
        final String LIGHT2 = "testLight2_" + System.nanoTime();

        {
            Family family = service.serviceFamilyInfo(FAMILY);
            Assert.assertNull(family);
        }
        {
            service.serviceFamilyLightStatusPut(FAMILY, LIGHT1, true);
            service.serviceFamilyLightStatusPut(FAMILY, LIGHT2, true);

            Family family = service.serviceFamilyInfo(FAMILY);
            Assert.assertThat(family.getLights().length, IsEqual.equalTo(2));
            Assert.assertThat(family.getLightsMap().values().size(), IsEqual.equalTo(2));
        }
    }

}
