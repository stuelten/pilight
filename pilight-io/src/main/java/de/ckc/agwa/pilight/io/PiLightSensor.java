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

import org.apache.commons.lang.builder.ToStringBuilder;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ein Sensor, der den Status der Lampe misst.
 *
 * @author Timo Stülten
 */
public class PiLightSensor implements GpioPinListenerDigital {

    /** The logger for this class only. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightSensor.class);

    // ----------------------------------------------------------------------

    protected String name;

    protected GpioPinDigitalInput pin;

    // ----------------------------------------------------------------------

    /** Als GPIO-Listener auf Änderungen am Sensor lauschen. */
    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        LOGGER.debug("state change event: {}", event);
        stateChanged(event.getState().isHigh());

    }

    protected void stateChanged(boolean state) {
        LOGGER.debug("{}: New state: {}", this, state);
    }

    // ----------------------------------------------------------------------

    public boolean isOn() {
        return pin.getState().isHigh();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected GpioPinDigitalInput getPin() {
        return pin;
    }

    protected void setPin(GpioPinDigitalInput pin) {
        if(this.pin != null) {
            LOGGER.info("{}: Listener abmelden von GPIO {}", this, this.pin);
            this.pin.removeListener(this);
        }
        this.pin = pin;
        if(this.pin != null) {
            LOGGER.info("{}: Listener anmelden an GPIO {}", this, this.pin);
            this.pin.addListener(this);
        }
    }

    // ----------------------------------------------------------------------

    public String toString() {
        String ret = new ToStringBuilder(this)
                .append("name", name)
                .append("on", isOn())
                .append("pin", pin)
                .toString();
        return ret;
    }

}
