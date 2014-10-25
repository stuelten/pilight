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

import java.util.ArrayList;
import java.util.List;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Konfiguriert Sensoren und Aktoren.
 *
 * @author Timo Stülten
 */
public class PiLightIOFactory {
    /** The logger for this class only. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightIOFactory.class);

    // ----------------------------------------------------------------------

    /** Die Factory ist ein Singleton. */
    private static class SingletonHolder {
        private static final PiLightIOFactory SINGLETON;

        static {
            SINGLETON = new PiLightIOFactory();
            SINGLETON.init();
        }
    }

    public static PiLightIOFactory getInstance() {
        return SingletonHolder.SINGLETON;
    }

    // ----------------------------------------------------------------------

    protected List<PiLightSensor> lightSensors = new ArrayList<>();

    protected List<PiLightSwitch> switches = new ArrayList<>();


    // ----------------------------------------------------------------------

    private void init() {
        LOGGER.info("Starte Init...");

        // create gpio controller
        GpioController gpio = GpioFactory.getInstance();

        initLightSensors(gpio);
        initSwitches(gpio);

        LOGGER.info("Ende Init.");
    }

    // ----------------------------------------------------------------------

    private void initLightSensors(GpioController gpio) {
        // Lampe initialisieren
        LOGGER.debug("Start initLightSensors...");

        // Eigene Lampe
        GpioPinDigitalInput sensorPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN);
        PiLightSensor sensor = createSensor(sensorPin);
        lightSensors.add(sensor);

        LOGGER.debug("Ende initLightSensors: {}", lightSensors);
    }

    private PiLightSensor createSensor(GpioPinDigitalInput lampenPin) {
        PiLightSensor lampe = new PiLightSensor();
        lampe.setName("Lampe");
        lampe.setPin(lampenPin);
        return lampe;
    }

    // ----------------------------------------------------------------------

    private void initSwitches(GpioController gpio) {
        LOGGER.debug("Start initSwitches...");

        // Erste Signal-LED
        GpioPinDigitalOutput led1Pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "Schwester", PinState.LOW);
        PiLightSwitch schwester = createSwitcher("Schwester", led1Pin);
        switches.add(schwester);

        // Zweite Signal-LED
        GpioPinDigitalOutput led2Pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "Bruder", PinState.LOW);
        PiLightSwitch bruder = createSwitcher("Bruder", led1Pin);
        switches.add(bruder);

        LOGGER.debug("Ende initSwitches: {}", switches);
    }

    private PiLightSwitch createSwitcher(String name, GpioPinDigitalOutput outputPin) {
        PiLightSwitch ret = new PiLightSwitch();
        ret.setName(name);
        ret.setPin(outputPin);

        // Light show!
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

}
