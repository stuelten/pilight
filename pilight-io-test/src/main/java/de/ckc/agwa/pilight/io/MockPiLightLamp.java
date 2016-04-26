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
package de.ckc.agwa.pilight.io;

/**
 * Mock implementation of a {@link PiLightLamp}.
 *
 * @author Timo Stülten
 */
public class MockPiLightLamp extends AbstractPiLightLamp {

    protected boolean state;

    // ----------------------------------------------------------------------

    public MockPiLightLamp() {
        super();
    }

    public MockPiLightLamp(String name) {
        super(name);
    }

    public MockPiLightLamp(String name, boolean state) {
        this(name);
        this.state = state;
    }

    // ----------------------------------------------------------------------

    @Override
    public boolean isOn() {
        return state;
    }

    public void setOn(boolean on) {
        this.state = on;
    }

}
