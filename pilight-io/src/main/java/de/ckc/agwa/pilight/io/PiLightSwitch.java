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

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Schaltet eine Lampe ein oder aus.
 * @author Timo Stülten
 */
public class PiLightSwitch {
    /** The logger for this class only. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightSwitch.class);

    // ----------------------------------------------------------------------

    protected String name;

    protected GpioPinDigitalOutput pin;

    // ----------------------------------------------------------------------

    public void setOn(boolean on) {
        pin.setState(on);
    }

    public boolean isOn() {
        return pin.getState().isHigh();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected GpioPinDigitalOutput getPin() {
        return pin;
    }

    protected void setPin(GpioPinDigitalOutput pin) {
        LOGGER.info("{}: verwendet GPIO {}", this, this.pin);
        this.pin = pin;
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
