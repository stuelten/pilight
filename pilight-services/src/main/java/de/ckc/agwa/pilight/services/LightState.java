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

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * A bean for the state of a light.
 *
 * @author Timo Stülten
 */
@XmlRootElement
public class LightState implements Serializable {

    private static final long serialVersionUID = 2384268438359298216L;

    /** The state of the light */
    protected boolean on;

    public LightState() {
    }

    public LightState(boolean on) {
        this.on = on;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    // ----------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof LightState)) return false;

        LightState that = (LightState) o;

        return new EqualsBuilder()
                .append(on, that.on)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(on)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("on", on)
                .toString();
    }

}
