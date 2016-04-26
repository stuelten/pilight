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

package de.ckc.agwa.pilight.services.client;

import org.junit.Test;

public class PiLightServiceClientMainTest {

    private String[] buildArgs(String... args) {
        String ret[] = new String[args.length + 2];
        ret[0] = "-b";
        ret[1] = "http://localhost:9997/pilight";

        System.arraycopy(args, 0, ret, 2, args.length);

        return ret;
    }

    @Test
    public void testUsage() {
        String[] args = {};
        PiLightServiceClientMain.main(args);
    }

    @Test
    public void testStatus() {
        String[] args = {"status"};
        PiLightServiceClientMain.main(args);
    }

    @Test
    public void testStatusBasedir() {
        String[] args = buildArgs("status");
        PiLightServiceClientMain.main(args);
    }

    @Test
    public void testFamilies() {
        String[] args = buildArgs("families");
        PiLightServiceClientMain.main(args);
    }

    @Test
    public void testLifeCycle() {
        testStatusBasedir();

        String[] args = buildArgs("lightStateGet", "testFamily", "testLight");
        PiLightServiceClientMain.main(args);

        args = buildArgs("lightStatePut", "testFamily", "testLight", "true");
        PiLightServiceClientMain.main(args);

        args = buildArgs("lightStateGet", "testFamily", "testLight");
        PiLightServiceClientMain.main(args);
    }


}
