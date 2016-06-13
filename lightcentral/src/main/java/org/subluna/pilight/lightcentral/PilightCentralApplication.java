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

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class PilightCentralApplication {

    public static void main(String[] args) {
        SpringApplication.run(PilightCentralApplication.class, args);
    }

    @Bean
    CommandLineRunner init(FamilyRepository familyRepository, LampRepository lampRepository) {
        return (evt) -> {
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            Family family = new Family("testfamily " + now);
            Lamp lamp1 = new Lamp(family, "testlamp 1 " + now, true);
            Lamp lamp2 = new Lamp(family, "testlamp 2 " + now, false);
            familyRepository.save(family);
            lampRepository.save(lamp1);
            lampRepository.save(lamp2);
            family.getLamps().add(lamp1);
            family.getLamps().add(lamp2);
            familyRepository.save(family);
        };
    }

}
