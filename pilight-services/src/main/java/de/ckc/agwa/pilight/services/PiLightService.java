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

/**
 * The pilight services.
 *
 * @author Timo Stülten
 */
public interface PiLightService {

    /**
     * Get this service's status as plain text.
     *
     * @return a String giving the number of served families and lights.
     */
    String serviceStatusPlain();

    /**
     * Get this service's status.
     *
     * @return a {@link PiLightServiceStatus} giving the number of served families and lights.
     */
    PiLightServiceStatus serviceStatus();

    /**
     * Get the names of all known families.
     *
     * @return the collection of families.
     */
    Families serviceKnownFamilies();

    /**
     * Get all known lights of a family.
     *
     * @return a Collection of Strings giving the light's names.
     */
    Family serviceFamilyInfo(String familyName);

    /**
     * Get the status for some light.
     *
     * @param familyName the name of the family
     * @param lightName  the name of the light
     * @return {@code true} for a burning light, {@code false} otherwise
     */
    LightState serviceFamilyLightStatusGet(String familyName, String lightName);

    /**
     * Set the status for some light.
     *
     * @param familyName the name of the family
     * @param lightName  the light
     * @param state {@code true} for a burning light, {@code false} otherwise
     */
    void serviceFamilyLightStatusPut(String familyName, String lightName, Boolean state);

}
