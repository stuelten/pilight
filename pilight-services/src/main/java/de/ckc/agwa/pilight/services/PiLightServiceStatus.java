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
 * A bean for this service's status.
 *
 * @author Timo Stülten
 */
public class PiLightServiceStatus {

    int familiesCount = 0;

    int lightsCount = 0;

    /**
     * @return Number of families
     */
    public int getFamiliesCount() {
        return familiesCount;
    }

    public void setFamiliesCount(int familiesCount) {
        this.familiesCount = familiesCount;
    }

    /**
     * @return Number of lights.
     */
    public int getLightsCount() {
        return lightsCount;
    }

    public void setLightsCount(int lightsCount) {
        this.lightsCount = lightsCount;
    }

    @Override
    public String toString() {
        String ret = "Serving " + getFamiliesCount() + " families with " + getLightsCount() + " lights.";
        return ret;
    }

}
