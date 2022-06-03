/*
 * Copyright 2022 International Association of Marine Aids to Navigation and Lighthouse Authorities
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

import com.example.mrr.model.NamespaceSyntax;
import com.example.mrr.model.NamespaceSyntaxDTO;
import com.example.mrr.services.NamespaceSyntaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/syntax")
public class NamespaceSyntaxController {

    private NamespaceSyntaxService namespaceSyntaxService;

    @Autowired
    public void setNamespaceSyntaxService(NamespaceSyntaxService namespaceSyntaxService) {
        this.namespaceSyntaxService = namespaceSyntaxService;
    }

    @GetMapping(
            path = "/{mrn}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<NamespaceSyntaxDTO> getNamespaceSyntaxForMrn(@PathVariable String mrn) {
        NamespaceSyntax syntax = namespaceSyntaxService.findNamespaceSyntaxForMrn(mrn);
        if (syntax != null) {
            NamespaceSyntaxDTO syntaxDTO = new NamespaceSyntaxDTO(syntax);
            return new ResponseEntity<>(syntaxDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
