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

import com.google.common.base.Strings;
import de.ckc.agwa.pilight.io.PiLightLamp;
import de.ckc.agwa.pilight.io.PiLightSwitch;
import de.ckc.agwa.pilight.services.PiLightServiceImpl;
import de.ckc.agwa.pilight.services.client.PiLightServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The logic with a switch for mother's lamp.
 *
 * @author Timo Stülten
 */
@Singleton
public class MotherMain implements Runnable, PiLightSwitch.StateChangeListener {
    /**
     * Environment variable to get service URL from
     */
    public static final String PILIGHT_SERVICE_ENV = "PILIGHT_SERVICE";

    // ----------------------------------------------------------------------
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
     * The lamps are switched on and off.
     */
    @Inject
    List<PiLightLamp> lamps;

    /**
     * The family
     */
    String family = "family";

    /**
     * The light
     */
    String light = "mother";

    /**
     * The service URL
     */
    String serviceBaseUrl;

    /**
     * Call the RESTful service asynch
     */
    private ScheduledExecutorService serviceExecutor;

    /**
     * The Service client API
     */
    private PiLightServiceClient serviceClient;

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
        serviceBaseUrl = System.getenv(PILIGHT_SERVICE_ENV);
        if (Strings.isNullOrEmpty(serviceBaseUrl)) {
            serviceBaseUrl = "http://kannkeule.de:9980/" + PiLightServiceImpl.SERVICE_PREFIX;
        }

        serviceExecutor = Executors.newSingleThreadScheduledExecutor();
        serviceClient = new PiLightServiceClient(serviceBaseUrl);

        PiLightSwitch mothersLight = switches.iterator().next();
        mothersLight.addStateChangeListener(this);

        // Call service asynchronously every 30s
        serviceExecutor.scheduleWithFixedDelay( //
                () -> serviceClient.serviceFamilyLightStatusPut(family, light, mothersLight.isOn()), 3, 30, TimeUnit.SECONDS);
    }

    @Override
    public void stateChanged(PiLightSwitch changedSwitch, boolean state) {
        boolean currentState = changedSwitch.isOn();
        LOGGER.info("Switch changed to '{}'", currentState);

        for (PiLightLamp lamp : lamps) {
            LOGGER.info("Switch '{}' to '{}'", lamp, currentState);
            lamp.setOn(currentState);
        }

        // Instantly and asynchronously call service
        serviceExecutor.submit(() -> serviceClient.serviceFamilyLightStatusPut(family, light, currentState));
    }

}
