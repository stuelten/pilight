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
import java.util.Collections;
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
    protected static Map<String, Map<String, Boolean>> lights = new ConcurrentHashMap<>();

    // ----------------------------------------------------------------------

    @Override
    public String serviceStatusPlain() {
        LOGGER.debug("serviceStatusPlain(): Called");

        PiLightServiceStatus status = serviceStatus();

        String ret = status.toString();

        LOGGER.info("serviceStatusPlain(): return '{}'", ret);
        return ret;
    }

    @Override
    public PiLightServiceStatus serviceStatus() {
        LOGGER.debug("serviceStatus(): Called");

        int familiesCount = lights.size();
        int lightsCount = lights.values().stream()
                .mapToInt(Map::size)
                .sum();

        PiLightServiceStatus ret = new PiLightServiceStatus();
        ret.setFamiliesCount(familiesCount);
        ret.setLightsCount(lightsCount);

        LOGGER.debug("serviceStatus(): return '{}'", ret);
        return ret;
    }

    @Override
    public Collection<String> serviceInfoFamilies() {
        LOGGER.debug("serviceInfoFamilies(): Called");
        Collection<String> ret = lights.keySet();

        LOGGER.info("serviceInfoFamilies(): return '{}'", ret);
        return ret;
    }

    // ----------------------------------------------------------------------

    @Override
    public Collection<String> serviceFamilyInfoLights(String family) {
        LOGGER.debug("serviceFamilyInfoLights('{}'): called", family);

        Map<String, Boolean> familyLights = lights.get(family);
        Collection<String> ret = Collections.emptyList();
        if (familyLights != null) {
            ret = familyLights.keySet();
        }

        LOGGER.info("serviceFamilyInfoLights('{}'): return '{}'", family, ret);
        return ret;
    }

    @Override
    public Boolean serviceFamilyLightStatusGet(String family,
                                               String light) {
        LOGGER.debug("serviceFamilyLightStatusGet('{}','{}'): called", family, light);
        Boolean status;

        Map<String, Boolean> familyLights = lights.get(family);
        if (null == familyLights) {
            status = Boolean.FALSE;
        } else {
            Boolean maybeNull = familyLights.get(light);
            status = (maybeNull == null ? Boolean.FALSE : maybeNull);
        }

        LOGGER.info("serviceFamilyLightStatusGet('{}', '{}'): return '{}'", family, light, status);
        return status;
    }

    @Override
    public Boolean serviceFamilyLightStatusPut(String family,
                                               String light,
                                               Boolean status) {
        LOGGER.info("serviceFamilyLightStatusPut('{}','{}','{}'): called", family, light, status);
        Objects.requireNonNull(family);
        Objects.requireNonNull(light);
        Objects.requireNonNull(status);

        Map<String, Boolean> familyLights = lights.get(family);
        if (null == familyLights) {
            familyLights = new ConcurrentHashMap<>();
            lights.put(family, familyLights);
        }
        familyLights.put(light, status);

        return status;
    }

    // ----------------------------------------------------------------------

    public String toString() {
        String ret = new ToStringBuilder(this)
                .append("lights", lights)
                .toString();
        return ret;
    }

}
