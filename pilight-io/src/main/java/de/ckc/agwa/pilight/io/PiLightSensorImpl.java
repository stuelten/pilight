/*
 * Copyright (c) 2015 Timo Stülten <timo.stuelten@googlemail.com>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package de.ckc.agwa.pilight.io;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A sensor listening on event on some {@link #pin}.
 *
 * @author Timo Stülten
 */
public class PiLightSensorImpl extends AbstractPiLightSensor implements GpioPinListenerDigital {

    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightSensorImpl.class);

    // ----------------------------------------------------------------------

    /**
     * The pin to listen on.
     */
    protected GpioPinDigitalInput pin;

    // ----------------------------------------------------------------------

    /**
     * Called on every state change on the {@link #pin}.
     */
    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        LOGGER.debug("state change event: '{}'", event);
        stateChanged(event.getState().isHigh());
    }

    // ----------------------------------------------------------------------

    public boolean isOn() {
        return pin == null ? false : pin.getState().isHigh();
    }

    // ----------------------------------------------------------------------

    protected GpioPinDigitalInput getPin() {
        return pin;
    }

    protected void setPin(GpioPinDigitalInput pin) {
        if (this.pin != null) {
            LOGGER.info("{}: remove listener on old GPIO '{}'", this, this.pin);
            this.pin.removeListener(this);
        }
        this.pin = pin;
        if (this.pin != null) {
            LOGGER.info("{}: add listener on new GPIO '{}'", this, this.pin);
            this.pin.addListener(this);
        }
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
