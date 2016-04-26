/* Copyright 2016 stuelten.
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
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Collection;
import java.util.Optional;

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

    public String getName() {
        return name;
    }

    public Boolean getState() {
        return state;
    }

}

