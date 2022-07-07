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

package org.iala_aism.mrr.controllers;

import org.iala_aism.mrr.exceptions.MrrRestException;
import org.iala_aism.mrr.model.MrrEntity;
import org.iala_aism.mrr.model.NamespaceSyntax;
import org.iala_aism.mrr.model.NamespaceSyntaxDTO;
import org.iala_aism.mrr.services.MrrService;
import org.iala_aism.mrr.services.NamespaceSyntaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/syntax")
public class NamespaceSyntaxController {

    private NamespaceSyntaxService namespaceSyntaxService;
    private MrrService mrrService;

    @Autowired
    public void setNamespaceSyntaxService(NamespaceSyntaxService namespaceSyntaxService) {
        this.namespaceSyntaxService = namespaceSyntaxService;
    }

    @Autowired
    public void setMrrService(MrrService mrrService) {
        this.mrrService = mrrService;
    }

    @GetMapping(
            path = "/{mrn}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<NamespaceSyntaxDTO> getNamespaceSyntaxForMrn(@PathVariable String mrn, HttpServletRequest request) throws MrrRestException {
        // Start by checking if there is a syntax for the specific MRN
        NamespaceSyntax syntax = namespaceSyntaxService.getNamespaceSyntax(mrn);
        if (syntax != null) {
            NamespaceSyntaxDTO syntaxDTO = new NamespaceSyntaxDTO(syntax);
            return new ResponseEntity<>(syntaxDTO, HttpStatus.OK);
        }
        // If there isn't check if there is another MRR for the MRN
        Optional<MrrEntity> maybeMrr = mrrService.searchForEarlierMrr(mrn);
        if (maybeMrr.isPresent()) {
            MrrEntity mrr = maybeMrr.get();
            throw new MrrRestException(HttpStatus.SEE_OTHER,
                    "Please repeat your query in the MRR for the namespace " + mrr.getMrnNamespace(),
                    request.getServletPath(), mrr.getEndpoint() + request.getServletPath());
        }
        // If none of the above succeed we traverse up the tree and return the result if we find a syntax
        syntax = namespaceSyntaxService.findNamespaceSyntaxForMrn(mrn);
        if (syntax != null) {
            NamespaceSyntaxDTO syntaxDTO = new NamespaceSyntaxDTO(syntax);
            return new ResponseEntity<>(syntaxDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
