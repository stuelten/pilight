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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This service receives and serves the status of lights for some families.
 *
 * @author Timo Stülten
 */
@Singleton
public class PiLightServiceImpl implements PiLightService {
    /**
     * The logger for this class only.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PiLightServiceImpl.class);

    // ----------------------------------------------------------------------

    /**
     * Contains all lights.
     * The families' names are the key, the {@link Map} of { lamp's names -&gt; lamp } are the values.
     */
    // protected static Map<String, Map<String, Boolean>> lights = new ConcurrentHashMap<>();

    protected Map<String, Family> familyMap = new ConcurrentHashMap<>();

    // ----------------------------------------------------------------------

    @Override
    public String serviceStatusPlain() {
        LOGGER.debug("serviceStatusPlain(): Called");

        PiLightServiceStatus status = serviceStatus();

        String ret = status.toString();

        LOGGER.debug("serviceStatusPlain(): return '{}'", ret);
        return ret;
    }

    @Override
    public PiLightServiceStatus serviceStatus() {
        LOGGER.debug("serviceStatus(): Called");

        PiLightServiceStatus ret = new PiLightServiceStatus();
        ret.setFamilies(familyMap.keySet());
        int familiesCount = familyMap.size();
        int lightsCount = familyMap.values().stream()
                .map(Family::getLightsMap)
                .mapToInt(Map::size)
                .sum();

        ret.setFamiliesCount(familiesCount);
        ret.setLightsCount(lightsCount);

        LOGGER.debug("serviceStatus(): return '{}'", ret);
        return ret;
    }

    @Override
    public Families serviceKnownFamilies() {
        LOGGER.debug("serviceKnownFamilies(): Called");
        Collection<String> families = familyMap.keySet();

        Families ret = new Families(families);

        LOGGER.debug("serviceKnownFamilyNames(): return '{}'", ret);
        return ret;
    }

    // ----------------------------------------------------------------------

    @Override
    public Family serviceFamilyInfo(String familyName) {
        LOGGER.debug("serviceFamilyInfo('{}'): called", familyName);

        Family ret = familyMap.get(familyName);

        LOGGER.debug("serviceFamilyInfo('{}'): return '{}'", familyName, ret);
        return ret;
    }

    @Override
    public LightState serviceFamilyLightStatusGet(String familyName,
                                                  String lightName) {
        LOGGER.debug("serviceFamilyLightStatusGet('{}','{}'): called", familyName, lightName);
        LightState ret;

        Family family = familyMap.get(familyName);
        if (null == family) {
            ret = new LightState(false);
        } else {
            Light light = family.getLight(lightName);
            boolean maybeNull = (light != null) && light.getState();
            ret = new LightState(maybeNull);
        }

        LOGGER.debug("serviceFamilyLightStatusGet('{}', '{}'): return '{}'", familyName, lightName, ret);
        return ret;
    }

    @Override
    public void serviceFamilyLightStatusPut(String familyName,
                                            String lightName,
                                            Boolean state) {
        LOGGER.debug("serviceFamilyLightStatusPut('{}','{}','{}'): called", familyName, lightName, state);
        Objects.requireNonNull(familyName);
        Objects.requireNonNull(lightName);
        Objects.requireNonNull(state);

        Light light = new Light(lightName, state);
        Family family = familyMap.get(familyName);
        if (null == family) {
            family = new Family(familyName);
        }
        family.putLight(light);
        familyMap.put(familyName, family);
    }

    // ----------------------------------------------------------------------

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("familyMap", familyMap)
                .toString();
    }

}
