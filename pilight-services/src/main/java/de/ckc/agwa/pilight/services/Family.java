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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A bean for a family of {@link Light}s.
 *
 * @author Timo Stülten
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Family implements Serializable {

    /**
     * The family's name. It is used as key in service calls.
     */
    protected String name;

    /**
     * The lights of this family, mapped by the light's name.
     */
    private Map<String, Light> lightsMap = new ConcurrentHashMap<>();

    // ----------------------------------------------------------------------

    public Family() {
        // nothing to do
    }

    public Family(String name) {
        this.name = name;
    }

    public Family(String name, Map<String, Light> lightsMap) {
        this.name = name;
        Objects.requireNonNull(lightsMap);
        this.lightsMap = lightsMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected Map<String, Light> getLightsMap() {
        return lightsMap;
    }

    private void setLightsMap(Map<String, Light> lightsMap) {
        this.lightsMap = lightsMap;
    }

    // ----------------------------------------------------------------------

    public Light[] getLights() {
        return lightsMap.values().toArray(new Light[lightsMap.size()]);
    }

    public void setLights(Light[] lights) {
        lightsMap.clear();
        for (Light light : lights) {
            putLight(light);
        }
    }

    public void putLight(Light light) {
        Objects.requireNonNull(light);
        Objects.requireNonNull(light.getName());
        lightsMap.put(light.getName(), light);
    }

    public Light getLight(String name) {
        Objects.requireNonNull(name);
        Light ret = lightsMap.get(name);
        return ret;
    }

    public boolean removeLight(Light light) {
        Objects.requireNonNull(light);
        Objects.requireNonNull(light.getName());
        boolean ret = lightsMap.remove(light.getName()) != null;
        return ret;
    }

    // ----------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Family)) return false;

        Family family = (Family) o;

        return new EqualsBuilder()
                .append(name, family.name)
                .append(lightsMap, family.lightsMap)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(lightsMap)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("lightsMap", lightsMap)
                .toString();
    }

}
