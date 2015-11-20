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

import de.ckc.agwa.pilight.services.Families;
import de.ckc.agwa.pilight.services.Family;
import de.ckc.agwa.pilight.services.Light;
import de.ckc.agwa.pilight.services.LightState;
import de.ckc.agwa.pilight.services.PiLightServiceStatus;
import de.ckc.agwa.pilight.services.rest.PiLightRestfulServiceMain;

import java.net.URI;

/**
 * Uses the client API and prints all output via sysout.
 *
 * @author Timo Stülten
 */
public class PiLightServiceClientMain {

    public static void main(String[] args) {
        int offset = 0;
        URI baseURI;
        if (0 == args.length) {
            System.err.println(usage());
        } else {
            if ("--baseurl".equals(args[0])
                    || "-b".equals(args[0])) {
                baseURI = URI.create(args[1]);
                offset = 2;
            } else {
                baseURI = PiLightRestfulServiceMain.BASE_URI;
            }
            String command = args[offset];
            offset += 1;
            String[] options = new String[args.length - offset];
            System.arraycopy(args, offset, options, 0, args.length - offset);

            PiLightServiceClientMain clientMain = new PiLightServiceClientMain(baseURI);
            clientMain.execute(command, options);
        }
    }

    private static String usage() {
        String ret = "Usage:  ([--baseurl|-b] <URL>) <command> <params>* \n"
                + "    [--baseurl|-b    <URL>   set base url\n";
        return ret;
    }

    // ----------------------------------------------------------------------

    private URI baseURI;
    private PiLightServiceClient serviceClient;

    private PiLightServiceClientMain(URI baseURI) {
        this.baseURI = baseURI;
    }

    private void out(String line) {
        System.out.println(line);
    }

    private void execute(String command, String... options) {
        serviceClient = new PiLightServiceClient(baseURI.toString());
        switch (command) {
            case "status":
                executeStatus();
                break;
            case "families":
                executeFamilies();
                break;
            case "familyInfo":
                parseFamilyInfo(options);
                break;
            case "lightInfo":
                parseLightInfo(options);
                break;
            case "lightStateGet":
                parseLightStatusGet(options);
                break;
            case "lightStatePut":
                parseLightStatePut(options);
                break;
            default:
                throw new IllegalArgumentException("Unknown command: " + command);
        }
    }

    private void parseFamilyInfo(String[] options) {
        for (String familyName : options) {
            executeFamilyInfo(familyName);
        }
    }

    private void parseLightInfo(String[] options) {
        String familyName = options[0];
        boolean skipFirstOption = true;
        for (String lightName : options) {
            if (skipFirstOption) {
                skipFirstOption = false;
            } else {
                executeLightInfo(familyName, lightName);
            }
        }
    }

    private void parseLightStatusGet(String[] options) {
        String familyName = options[0];
        String lightName = options[1];
        executeLightStatusGet(familyName, lightName);
    }

    private void parseLightStatePut(String[] options) {
        String familyName = options[0];
        String lightName = options[1];
        Boolean state = Boolean.valueOf(options[2]);
        executeLightStatusPut(familyName, lightName, state);
    }

    private void executeStatus() {
        out("# querying status");
        PiLightServiceStatus piLightServiceStatus = serviceClient.serviceStatus();
        out(piLightServiceStatus.toString());
    }

    private void executeFamilies() {
        out("# querying families");
        Families families = serviceClient.serviceKnownFamilies();
        out("" + families);
        for (String familyName : families.getNames()) {
            out("familyName: " + familyName);
        }
    }

    private void executeFamilyInfo(String familyName) {
        out("# querying family info for: " + familyName);
        Family family = serviceClient.serviceFamilyInfo(familyName);
        out("" + family);
        for (Light light : family.getLights()) {
            out("light: " + light);
        }
    }

    private void executeLightInfo(String familyName, String lightName) {
        out("# querying light info for: " + familyName + "[" + lightName + "]");
        Family family = serviceClient.serviceFamilyInfo(familyName);
        out("family: " + family.getName());
        out("family: " + family.getName() + " light: " + family.getLight(lightName).getName());
    }

    private void executeLightStatusGet(String familyName, String lightName) {
        out("# querying light state for: " + familyName + "[" + lightName + "]");
        LightState lightState = serviceClient.serviceFamilyLightStatusGet(familyName, lightName);
        out("family: " + familyName + " light: " + lightName + " lightState: " + lightState.isOn());
    }

    private void executeLightStatusPut(String familyName, String lightName, boolean state) {
        out("# setting light state for: " + familyName + "[" + lightName + "][" + state + "]");
        LightState lightState = new LightState(state);
        serviceClient.serviceFamilyLightStatusPut(familyName, lightName, lightState);
    }

}
