/*
 * Copyright 2014 Timo Stülten <timo.stuelten@googlemail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package de.ckc.agwa.pilight.logic;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * A CDI helper class using weld.
 *
 * @author Timo Stülten
 */
public class CDIContext {

    /**
     * The single CDI management context.
     */
    public static final CDIContext INSTANCE = new CDIContext();

    // ----------------------------------------------------------------------

    private final Weld weld;
    private final WeldContainer container;

    private CDIContext() {
        this.weld = new Weld();
        this.container = weld.initialize();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                weld.shutdown();
            }
        });
    }

    // ----------------------------------------------------------------------

    /**
     * Get some CDI managed bean of the given type.
     * @param type the class of the new bean
     * @param <T> the bean's type
     * @return a CDI managed bean
     */
    public <T> T getBean(Class<T> type) {
        return container.instance().select(type).get();
    }

}
