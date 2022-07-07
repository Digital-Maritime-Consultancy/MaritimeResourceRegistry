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
import org.iala_aism.mrr.model.MrrDTO;
import org.iala_aism.mrr.model.MrrEntity;
import org.iala_aism.mrr.model.NamespaceEntity;
import org.iala_aism.mrr.services.MrrService;
import org.iala_aism.mrr.services.NamespaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/mrr")
public class MrrController {
    private MrrService mrrService;
    private NamespaceService namespaceService;

    @Autowired
    public void setMrrService(MrrService mrrService) {
        this.mrrService = mrrService;
    }

    @Autowired
    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    @GetMapping(
            value = "/{mrnNamespace}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MrrDTO> getMrr(@PathVariable String mrnNamespace, HttpServletRequest request) throws MrrRestException {
        MrrEntity mrr = mrrService.getByMrnNamespace(mrnNamespace)
                .orElseThrow(() -> new MrrRestException(HttpStatus.NOT_FOUND,
                        "An MRR with the given MRN namespace could not be found", request.getServletPath()));
        MrrDTO mrrDTO = new MrrDTO(mrr);
        return new ResponseEntity<>(mrrDTO, HttpStatus.OK);
    }

    @GetMapping(
            value = "/id/{mrrId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MrrDTO> getMrrById(@PathVariable Long mrrId, HttpServletRequest request) throws MrrRestException {
        MrrEntity mrr = mrrService.getById(mrrId)
                .orElseThrow(() -> new MrrRestException(HttpStatus.NOT_FOUND,
                        "An MRR with the given ID could not be found", request.getServletPath()));
        MrrDTO mrrDTO = new MrrDTO(mrr);
        return new ResponseEntity<>(mrrDTO, HttpStatus.OK);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MrrDTO> createMrr(@RequestBody MrrDTO mrr, HttpServletRequest request) throws MrrRestException {
        MrrEntity mrrEntity = handleCreation(mrr, request);
        MrrDTO mrrDTO = new MrrDTO(mrrEntity);
        return new ResponseEntity<>(mrrDTO, HttpStatus.OK);
    }

    private MrrEntity handleCreation(MrrDTO mrrDTO, HttpServletRequest request) throws MrrRestException {
        NamespaceEntity namespace = namespaceService.getNamespaceByMrn(mrrDTO.getMrnNamespace());
        MrrEntity mrrEntity = mrrService.searchForLaterMrr(namespace);
        if (mrrEntity != null) {
            throw new MrrRestException(HttpStatus.BAD_REQUEST,
                    "An MRR entry already exists for this or a later MRN namespace: " + mrrEntity.getMrnNamespace(),
                    request.getServletPath());
        }
        if (namespace == null) {
            namespace = namespaceService.createNamespace(mrrDTO.getMrnNamespace());
        }
        MrrEntity newMrr = new MrrEntity(mrrDTO.getMrnNamespace(), mrrDTO.getEndpoint());
        newMrr.setNamespace(namespace);
        return mrrService.save(newMrr);
    }
}
