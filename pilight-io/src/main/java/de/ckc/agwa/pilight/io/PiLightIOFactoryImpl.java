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
 * Sets up sensors and switches on raspi.
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
    protected List<PiLightSensor> lightSensors = new ArrayList<>();

    /**
     * The list of switches
     */
    protected List<PiLightSwitch> switches = new ArrayList<>();

    // ----------------------------------------------------------------------

    public PiLightIOFactoryImpl() {
        super();
        init();
    }

    private void init() {
        LOGGER.info("Start init...");

        // create gpio controller
        GpioController gpio = GpioFactory.getInstance();

        initLightSensors(gpio);
        initSwitches(gpio);

        LOGGER.info("End init.");
    }

    // ----------------------------------------------------------------------

    private void initLightSensors(GpioController gpio) {
        // Lampe initialisieren
        LOGGER.debug("Start initLightSensors...");

        // Eigene Lampe
        GpioPinDigitalInput sensorPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN);
        PiLightSensor sensor = createSensor(sensorPin);
        lightSensors.add(sensor);

        LOGGER.debug("End initLightSensors: {}", lightSensors);
    }

    private PiLightSensor createSensor(GpioPinDigitalInput sensorPin) {
        PiLightSensorImpl sensor = new PiLightSensorImpl();
        sensor.setName("mother's switch");
        sensor.setPin(sensorPin);
        return sensor;
    }

    // ----------------------------------------------------------------------

    private void initSwitches(GpioController gpio) {
        LOGGER.debug("Start initSwitches...");

        // 1st LED
        GpioPinDigitalOutput led1Pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "sister", PinState.LOW);
        PiLightSwitch sister = createSwitch("sister", led1Pin);
        switches.add(sister);

        // 2nd LED
        GpioPinDigitalOutput led2Pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "brother", PinState.LOW);
        PiLightSwitch brother = createSwitch("brother", led1Pin);
        switches.add(brother);

        LOGGER.debug("End initSwitches: {}", switches);
    }

    private PiLightSwitch createSwitch(String name, GpioPinDigitalOutput outputPin) {
        PiLightSwitchImpl ret = new PiLightSwitchImpl();
        ret.setName(name);
        ret.setPin(outputPin);

        // Light show!
        LOGGER.info("Created switch '{}'", ret);
        ret.setOn(true);
        PiLightIOHelper.sleep(500);
        ret.setOn(false);
        PiLightIOHelper.sleep(500);
        ret.setOn(true);
        PiLightIOHelper.sleep(500);
        ret.setOn(false);
        PiLightIOHelper.sleep(500);

        return ret;
    }

    // ----------------------------------------------------------------------

    @Produces
    public List<PiLightSensor> getLightSensors() {
        return Collections.unmodifiableList(lightSensors);
    }

    @Produces
    public List<PiLightSwitch> getSwitches() {
        return Collections.unmodifiableList(switches);
    }

}
