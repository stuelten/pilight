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

/**
 * Abstract implementation of a {@link PiLightLamp} with a name.
 *
 * @author Timo Stülten
 */
public abstract class AbstractPiLightLamp implements PiLightLamp {

    /**
     * The name
     */
    protected String name;

    // ----------------------------------------------------------------------

    public AbstractPiLightLamp() {
        super();
    }

    public AbstractPiLightLamp(String name) {
        this();
        this.name = name;
    }

    // ----------------------------------------------------------------------

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        String ret = new ToStringBuilder(this)
                .append("name", name)
                .append("on", isOn())
                .toString();
        return ret;
    }

}
