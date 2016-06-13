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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A family contains some {@link Lamp lamps}.
 *
 * @author stuelten
 */
@Entity
public class Family {

    @Id
    private String name;

    @OneToMany // (mappedBy = "family")
    private Set<Lamp> lamps = new LinkedHashSet<>();

    // ----------------------------------------------------------------------

    public Family() {
        // JPA needs empty constructor
    }

    public Family(String name) {
        this.name = name;
    }

    // ----------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public Set<Lamp> getLamps() {
        return lamps;
    }

    public void setLamps(Set<Lamp> lamps) {
        this.lamps = lamps;
    }

    // ----------------------------------------------------------------------

    @Override
    public String toString() {
        return "Family{" +
                "name='" + name + '\'' +
                ", lamps=" + lamps +
                '}';
    }

}

