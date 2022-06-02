/*
 * Copyright 2022 Digital Maritime Consultancy
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

package com.example.mrr.controllers;

import com.example.mrr.model.MaritimeResourceDTO;
import com.example.mrr.model.MaritimeResourceEntity;
import com.example.mrr.model.NamespaceEntity;
import com.example.mrr.model.NamespaceSyntax;
import com.example.mrr.services.MaritimeResourceService;
import com.example.mrr.services.NamespaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    private MaritimeResourceService resourceService;

    private NamespaceService namespaceService;

    @Autowired
    public void setResourceService(MaritimeResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Autowired
    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> createResource(@RequestBody MaritimeResourceDTO maritimeResourceDTO) {
        try {
            handleCreation(maritimeResourceDTO);
            return new ResponseEntity<>("The resource was successfully created", HttpStatus.CREATED);
        } catch (URISyntaxException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private void handleCreation(MaritimeResourceDTO maritimeResourceDTO) throws URISyntaxException {
        MaritimeResourceEntity entity = new MaritimeResourceEntity(maritimeResourceDTO.getMrn(), maritimeResourceDTO.getLocation(), maritimeResourceDTO.getTitle(), maritimeResourceDTO.getDescription());

        NamespaceSyntax syntax = namespaceService.getNamespaceSyntaxByMrn(entity.getMrn());
        if (syntax == null) {
            throw new URISyntaxException("A syntax definition could not be found for the MRN of the resource", entity.getMrn());
        }
        Pattern pattern = Pattern.compile(syntax.getRegex());
        if (pattern.matcher(entity.getMrn()).matches()) {
            entity.setNamespace(createNamespace(entity.getMrn()));
            resourceService.save(entity);
        } else {
            throw new URISyntaxException(entity.getMrn(), String.format("The MRN of the resource does not follow " +
                    "the syntax definition for %s", syntax.getNamespace().getMrnNamespace()));
        }
    }

    private NamespaceEntity createNamespace(String mrn) {
        NamespaceEntity namespaceEntity = namespaceService.getNamespaceByMrn(mrn);
        if (namespaceEntity != null) {
            return namespaceEntity;
        }
        NamespaceEntity entity = new NamespaceEntity(mrn);
        if (mrn.contains(":")) {
            String namespace = mrn.substring(0, mrn.lastIndexOf(':'));
            entity.setParentNamespace(createNamespace(namespace));
        }
        return entity;
    }
}
