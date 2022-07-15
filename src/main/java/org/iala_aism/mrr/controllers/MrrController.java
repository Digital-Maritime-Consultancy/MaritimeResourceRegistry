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

import io.swagger.v3.oas.annotations.Operation;
import org.iala_aism.mrr.exceptions.MrrRestException;
import org.iala_aism.mrr.model.dto.MrrDTO;
import org.iala_aism.mrr.model.MrrEntity;
import org.iala_aism.mrr.model.NamespaceEntity;
import org.iala_aism.mrr.services.MrrService;
import org.iala_aism.mrr.services.NamespaceService;
import org.iala_aism.mrr.utils.AccessControlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@EnableMethodSecurity
@RestController
@RequestMapping("/mrr")
public class MrrController {
    public static final String NAMESPACE_COULD_NOT_BE_FOUND = "An MRR with the given MRN namespace could not be found";
    private MrrService mrrService;
    private NamespaceService namespaceService;
    private AccessControlUtil accessControlUtil;

    @Autowired
    public void setMrrService(MrrService mrrService) {
        this.mrrService = mrrService;
    }

    @Autowired
    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    @Autowired
    public void setAccessControlUtil(AccessControlUtil accessControlUtil) {
        this.accessControlUtil = accessControlUtil;
    }

    @GetMapping(
            value = "/{mrnNamespace}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            description = "Returns the MRR for the given MRN namespace"
    )
    public ResponseEntity<MrrDTO> getMrr(@PathVariable String mrnNamespace, HttpServletRequest request) throws MrrRestException {
        MrrEntity mrr = mrrService.getByMrnNamespace(mrnNamespace)
                .orElseThrow(() -> new MrrRestException(HttpStatus.NOT_FOUND,
                        NAMESPACE_COULD_NOT_BE_FOUND, request.getServletPath()));
        MrrDTO mrrDTO = new MrrDTO(mrr);
        return new ResponseEntity<>(mrrDTO, HttpStatus.OK);
    }

    @GetMapping(
            value = "/id/{mrrId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            description = "Returns the MRR with given ID"
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
    @Operation(
            description = "Creates a new MRR"
    )
    @PreAuthorize("@accessControlUtil.canManageNamespace(#mrr.mrnNamespace)")
    public ResponseEntity<MrrDTO> createMrr(@RequestBody MrrDTO mrr, HttpServletRequest request) throws MrrRestException {
        MrrEntity mrrEntity = handleCreation(mrr, request);
        MrrDTO mrrDTO = new MrrDTO(mrrEntity);
        return new ResponseEntity<>(mrrDTO, HttpStatus.OK);
    }

    @DeleteMapping(
            value = "/{mrnNamespace}"
    )
    @Operation(
            description = "Deletes the MRR for the given MRN namespace"
    )
    @PreAuthorize("@accessControlUtil.canManageNamespace(#mrnNamespace)")
    public void deleteByMrnNamespace(@PathVariable String mrnNamespace, HttpServletRequest request) throws MrrRestException {
        Optional<MrrEntity> maybeMrr = mrrService.getByMrnNamespace(mrnNamespace);
        if (maybeMrr.isEmpty())
            throw new MrrRestException(HttpStatus.NOT_FOUND, NAMESPACE_COULD_NOT_BE_FOUND, request.getServletPath());
        mrrService.deleteByMrnNamespace(mrnNamespace);
    }

    @DeleteMapping(
            value = "/id/{mrrId}"
    )
    @Operation(
            description = "Deletes the MRR with the given ID"
    )
    public void deleteById(@PathVariable Long mrrId, HttpServletRequest request, HttpServletResponse response) throws MrrRestException {
        Optional<MrrEntity> maybeMrr = mrrService.getById(mrrId);
        if (maybeMrr.isEmpty())
            throw new MrrRestException(HttpStatus.NOT_FOUND, NAMESPACE_COULD_NOT_BE_FOUND, request.getServletPath());
        if (!accessControlUtil.canManageNamespace(maybeMrr.get().getMrnNamespace())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        mrrService.deleteById(mrrId);
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
