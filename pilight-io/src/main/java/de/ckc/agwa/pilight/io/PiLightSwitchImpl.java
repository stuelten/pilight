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

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A switch with a name and a state.
 *
 * @author Timo Stülten
 */
public class PiLightSwitchImpl extends AbstractPiLightSwitch {
    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightSwitchImpl.class);

    // ----------------------------------------------------------------------

    protected GpioPinDigitalOutput pin;

    // ----------------------------------------------------------------------

    @Override
    public void setOn(boolean on) {
        pin.setState(on);
    }

    @Override
    public boolean isOn() {
        return pin.getState().isHigh();
    }

    /**
     * Get this switch's technical pin.
     *
     * @return the pin
     */
    protected GpioPinDigitalOutput getPin() {
        return pin;
    }

    /**
     * Set this switch's technical pin
     *
     * @param pin the new pin
     */
    protected void setPin(GpioPinDigitalOutput pin) {
        LOGGER.info("{}: verwendet GPIO {}", this, this.pin);
        this.pin = pin;
    }

    // ----------------------------------------------------------------------

    public String toString() {
        String ret = new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("pin", getPin())
                .toString();
        return ret;
    }

}
