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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Abstract implementation of a {@link PiLightSwitch} with a name.
 *
 * @author Timo Stülten
 */
public abstract class AbstractPiLightSwitch implements PiLightSwitch {
    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPiLightSwitch.class);

    // ----------------------------------------------------------------------

    /**
     * This sensor's name.
     */
    protected String name;

    /**
     * The listeners on this sensor.
     */
    protected Set<StateChangeListener> listeners = new CopyOnWriteArraySet<>();

    // ----------------------------------------------------------------------

    public AbstractPiLightSwitch() {
        super();
    }

    public AbstractPiLightSwitch(String name) {
        this();
        this.name = name;
    }

    // ----------------------------------------------------------------------

    @Override
    public void addStateChangeListener(StateChangeListener stateChangeListener) {
        LOGGER.debug("addStateChangeListener('{}')", stateChangeListener);
        listeners.add(stateChangeListener);
    }

    @Override
    public void removeStateChangeListener(StateChangeListener stateChangeListener) {
        LOGGER.debug("removeStateChangeListener('{}')", stateChangeListener);
        listeners.remove(stateChangeListener);
    }

    public void stateChanged(boolean state) {
        LOGGER.info("{}: New state: '{}'", this, state);

        for (StateChangeListener stateChangeListener : listeners) {
            LOGGER.debug("{}: Fire new state: '{}' to '{}'", this, stateChangeListener);
            stateChangeListener.stateChanged(state);
        }

    }

    // ----------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ----------------------------------------------------------------------


    public String toString() {
        String ret = new ToStringBuilder(this)
                .append("name", name)
                .append("on", isOn())
                .toString();
        return ret;
    }

}
