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

package de.ckc.agwa.pilight.services;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * A bean for a simple Light.
 *
 * @author Timo Stülten
 */
public class Light implements Serializable {

    private static final long serialVersionUID = 6299429549357599430L;

    /**
     * The light's name
     */
    protected String name;

    /**
     * Is the light on or off?
     */
    protected boolean state;

    // ----------------------------------------------------------------------

    public Light() {
        // nothing to do
    }

    public Light(String name, boolean state) {
        this.name = name;
        this.state = state;
    }

    // ----------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    // ----------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Light)) return false;

        Light light = (Light) o;

        return new EqualsBuilder()
                .append(name, light.name)
                .append(state, light.state)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(47, 97)
                .append(name)
                .append(state)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("state", state)
                .toString();
    }

}
