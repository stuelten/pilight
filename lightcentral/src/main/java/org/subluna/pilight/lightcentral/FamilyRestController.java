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

/**
 * A rest controller, serving {@link Family families}.
 *
 * @author timo@stuelten.de
 */
@RestController
@RequestMapping("/families/")
public class FamilyRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FamilyRestController.class);

    private final FamilyRepository familyRepository;
    private final LampRepository lampRepository;

    @Autowired
    public FamilyRestController(
            FamilyRepository familyRepository,
            LampRepository lampRepository) {
        this.familyRepository = familyRepository;
        this.lampRepository = lampRepository;
        LOGGER.info("new FamilyRestController: '{}'", this);
    }

    // ----------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    Collection<Family> readFamilies() {
        LOGGER.info("readFamilies");

        Collection<Family> ret;
        ret = this.familyRepository.findAll();
        return ret;
    }

    @RequestMapping(value = "/{familyName}", method = RequestMethod.GET)
    Family get(@PathVariable String familyName) {
        LOGGER.info("get:  '{}'", familyName);
        Family ret = familyRepository.findOne(familyName);
        return ret;
    }

    @RequestMapping(value = "/{familyName}", method = RequestMethod.POST)
    ResponseEntity<?> add(@PathVariable String familyName, @RequestBody Family family) {
        LOGGER.info("add:  '{}':'{}'", familyName, family);

        Family savedFamily = familyRepository.save(family);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{familyName}")
                .buildAndExpand(savedFamily.getName()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{familyName}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String familyName) {
        LOGGER.info("delete:  '{}'", familyName);
        this.familyRepository.findByName(familyName).ifPresent(family -> {
            lampRepository.delete(family.getLamps());
            familyRepository.delete(family);
        });
    }

    // ----------------------------------------------------------------------

    @Override
    public String toString() {
        return "FamilyRestController{" +
                "familyRepository=" + familyRepository +
                ", lampRepository=" + lampRepository +
                '}';
    }

}
