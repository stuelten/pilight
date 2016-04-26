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

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sets up lamps and switches on raspi.
 *
 * @author Timo Stülten
 */
@Singleton
public class PiLightIOFactoryImpl {
    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightIOFactoryImpl.class);

    // ----------------------------------------------------------------------

    /**
     * The list of sensors.
     */
    protected List<PiLightSwitch> switches = new ArrayList<>();

    /**
     * The list of lamps
     */
    protected List<PiLightLamp> lamps = new ArrayList<>();

    // ----------------------------------------------------------------------

    public PiLightIOFactoryImpl() {
        super();
        init();
    }

    private void init() {
        LOGGER.info("Start init...");

        // create gpio controller
        GpioController gpio = GpioFactory.getInstance();

        initSwitches(gpio);
        initLamps(gpio);

        LOGGER.info("End init.");
    }

    // ----------------------------------------------------------------------

    private void initSwitches(GpioController gpio) {
        LOGGER.debug("Start initSwitches...");

        // only mother's switch exists
        GpioPinDigitalInput sensorPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN);
        PiLightSwitch aSwitch = createSwitch("mother's light switch", sensorPin);
        switches.add(aSwitch);

        LOGGER.debug("End initSwitches: {}", switches);
    }

    private PiLightSwitch createSwitch(String name, GpioPinDigitalInput sensorPin) {
        PiLightSwitchImpl ret = new PiLightSwitchImpl();
        ret.setName(name);
        ret.setPin(sensorPin);

        LOGGER.info("Created switch '{}'", ret);
        return ret;
    }

    // ----------------------------------------------------------------------

    private void initLamps(GpioController gpio) {
        LOGGER.debug("Start initLamps...");

        {
            // 1st LED is mother's light
            GpioPinDigitalOutput ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "mother", PinState.LOW);
            PiLightLamp sister = createLamp("switched mother's lamp", ledPin);
            lamps.add(sister);
        }
        {
            // 2nd LED is sister's lamp
            GpioPinDigitalOutput ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "sister", PinState.LOW);
            PiLightLamp sister = createLamp("switched sister's lamp", ledPin);
            lamps.add(sister);
        }
        {
            // 3rd LED is brother's lamp
            GpioPinDigitalOutput ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "brother", PinState.LOW);
            PiLightLamp brother = createLamp("switched brother's lamp", ledPin);
            lamps.add(brother);
        }

        LOGGER.debug("End initLamps: {}", lamps);
    }

    private PiLightLamp createLamp(String name, GpioPinDigitalOutput outputPin) {
        PiLightLampImpl ret = new PiLightLampImpl();
        ret.setName(name);
        ret.setPin(outputPin);

        outputPin.setShutdownOptions(true, PinState.LOW);

        // Light show!
        LOGGER.info("Created lamp '{}'", ret);
        int blinks = outputPin.getPin().getAddress();

        // one long blink for start
        ret.setOn(true);
        PiLightIOHelper.sleep(500);
        ret.setOn(false);
        PiLightIOHelper.sleep(500);

        // count pin number with fast short blinks
        while (blinks-- > 0) {
            ret.setOn(true);
            PiLightIOHelper.sleep(100);
            ret.setOn(false);
            PiLightIOHelper.sleep(100);
        }

        // one long blink for end
        PiLightIOHelper.sleep(400);
        ret.setOn(true);
        PiLightIOHelper.sleep(500);
        ret.setOn(false);

        return ret;
    }

    // ----------------------------------------------------------------------

    /**
     * @return the only switch this factory supports, wrapped in a {@link Collections#unmodifiableList(List)}.
     */
    @Produces
    public List<PiLightSwitch> getSwitches() {
        return Collections.unmodifiableList(switches);
    }

    /**
     * @return the lamps this factory supports, wrapped in a {@link Collections#unmodifiableList(List)}.
     */
    @Produces
    public List<PiLightLamp> getLamps() {
        return Collections.unmodifiableList(lamps);
    }

}
