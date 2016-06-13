/*
 * Copyright (c) 2016 Timo Stülten <timo@stuelten.de>
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
package org.subluna.pilight.lightcentral;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * A lamp has a name and a state: it is on ({@code state == true})
 * or off ({@code state == false}).
 *
 * @author timo@stuelten.de
 */
@Entity
public class Lamp {

    @JsonIgnore
    @ManyToOne
    private Family family;

    @Id
    private String name;

    private Boolean state = false;

    // ----------------------------------------------------------------------

    public Lamp() {
        // JPA needs an empty constructor
    }

    public Lamp(Family family, String name, Boolean state) {
        this.family = family;
        this.name = name;
        this.state = state;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    // ----------------------------------------------------------------------

    @Override
    public String toString() {
        return "Lamp{" +
                // no family: avoid endless recursion
                // "family=" + family +
                "name='" + name + '\'' +
                ", state=" + state +
                '}';
    }

}

