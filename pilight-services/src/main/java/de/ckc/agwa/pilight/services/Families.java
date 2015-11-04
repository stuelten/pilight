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
import java.util.Collection;

/**
 * A bean for information about all {@link Family families}.
 * @author Timo Stülten
 */
@XmlRootElement
public class Families implements Serializable {

    private static final long serialVersionUID = -774582554636102673L;

    /**
     * The names of the families.
     */
    protected Collection<String> names;

    public Families() {
        // nothing to do
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    protected Families(Collection<String> names) {
        this.names = names;
    }

    public Collection<String> getNames() {
        return names;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    protected void setNames(Collection<String> names) {
        this.names = names;
    }

    // ----------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Families)) return false;

        Families families = (Families) o;

        return new EqualsBuilder()
                .append(names, families.names)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(names)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("names", names)
                .toString();
    }

}
