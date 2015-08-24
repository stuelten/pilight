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

/**
 * A lamp with a name and a state.
 *
 * @author Timo Stülten
 */
public interface PiLightLamp {

    /**
     * Is the lamp switched on?
     *
     * @return {@code true} if switched on.
     */
    boolean isOn();

    /**
     * Switch the lamp on or off.
     *
     * @param on switch on if {@code true}
     */
    void setOn(boolean on);

    /**
     * Get this lamp's name
     *
     * @return the name
     */
    String getName();

    /**
     * Set this lamp's name
     *
     * @param name the new name
     */
    void setName(String name);

}
