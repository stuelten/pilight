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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.Optional;

/**
 * A rest controller, serving {@link Lamp lamps}.
 *
 * @author timo@stuelten.de
 */
@RestController
@RequestMapping("/families/{familyName}/lamps/")
public class LampRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LampRestController.class);

    private final FamilyRepository familyRepository;
    private final LampRepository lampRepository;

    @Autowired
    public LampRestController(
            FamilyRepository familyRepository,
            LampRepository lampRepository) {
        this.familyRepository = familyRepository;
        this.lampRepository = lampRepository;
        LOGGER.info("new LampRestController: '{}'", this);
    }

    // ----------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    Collection<Lamp> readLamps(
            @PathVariable(value = "familyName") String familyName) {
        LOGGER.info("readLamps: '{}'", familyName);
        Collection<Lamp> ret;
        ret = this.lampRepository.findByFamilyName(familyName);
        LOGGER.info("readLamps:  return '{}':'{}'", familyName, ret);
        return ret;
    }

    @RequestMapping(value = "/{lampName}", method = RequestMethod.GET)
    Lamp get(
            @PathVariable(value = "familyName") String familyName,
            @PathVariable(value = "lampName") String lampName) {
        LOGGER.info("get:  '{}':'{}'", familyName, lampName);

        Lamp ret = null;
        Optional<Lamp> lamp = lampRepository
                .findByFamilyNameAndName(familyName, lampName);
        if (lamp.isPresent()) {
            ret = lamp.get();
        }
        LOGGER.info("get:  return '{}':'{}':'{}'", familyName, lampName, ret);
        return ret;
    }

    @RequestMapping(value = "/{lampName}", method = RequestMethod.POST)
    ResponseEntity<?> add(
            @PathVariable(value = "familyName") String familyName,
            @PathVariable(value = "lampName") String lampName,
            @RequestBody Lamp lamp) {
        LOGGER.info("add:  '{}':'{}':'{}'", familyName, lampName, lamp);

        Family family = familyRepository.findOne(familyName);
        if (family == null) {
            family = new Family(familyName);
            family = familyRepository.save(family);
        }
        family.getLamps().add(lamp);
        lamp.setFamily(family);
        Lamp savedLamp = lampRepository.save(lamp);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{lampName}")
                .buildAndExpand(savedLamp.getName()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{lampName}", method = RequestMethod.DELETE)
    public void delete(
            @PathVariable(value = "familyName") String familyName,
            @PathVariable(value = "lampName") String lampName) {
        LOGGER.info("delete:  '{}':'{}'", familyName, lampName);

        this.lampRepository.findByFamilyNameAndName(familyName, lampName)
                .ifPresent(lampRepository::delete);
    }

    // ----------------------------------------------------------------------

    @Override
    public String toString() {
        return "LampRestController{" +
                "familyRepository=" + familyRepository +
                ", lampRepository=" + lampRepository +
                '}';
    }

}
