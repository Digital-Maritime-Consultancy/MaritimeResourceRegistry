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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.iala_aism.mrr.components.MrrWebSocketHandler;
import org.iala_aism.mrr.exceptions.MrrRestException;
import org.iala_aism.mrr.model.MrrEntity;
import org.iala_aism.mrr.model.NamespaceSyntax;
import org.iala_aism.mrr.model.SyntaxCreationResult;
import org.iala_aism.mrr.model.SyntaxCreationResultRedis;
import org.iala_aism.mrr.model.dto.NamespaceSyntaxDTO;
import org.iala_aism.mrr.model.dto.SyntaxCreationDTO;
import org.iala_aism.mrr.model.enums.SyntaxCreationStatus;
import org.iala_aism.mrr.services.MrrService;
import org.iala_aism.mrr.services.NamespaceSyntaxService;
import org.iala_aism.mrr.services.SyntaxCreationStatusService;
import org.iala_aism.mrr.utils.AccessControlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/syntax")
public class NamespaceSyntaxController {

    private NamespaceSyntaxService namespaceSyntaxService;
    private MrrService mrrService;
    private SyntaxCreationStatusService creationStatusService;
    private ObjectMapper mapper;

    private AccessControlUtil accessControlUtil;

    @Value("${org.iala_aism.mrr.websocket-url}")
    private String webSocketUrl;

    @Autowired
    public void setNamespaceSyntaxService(NamespaceSyntaxService namespaceSyntaxService) {
        this.namespaceSyntaxService = namespaceSyntaxService;
    }

    @Autowired
    public void setMrrService(MrrService mrrService) {
        this.mrrService = mrrService;
    }

    @Autowired
    public void setCreationStatusService(SyntaxCreationStatusService creationStatusService) {
        this.creationStatusService = creationStatusService;
    }

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Autowired
    public void setAccessControlUtil(AccessControlUtil accessControlUtil) {
        this.accessControlUtil = accessControlUtil;
    }

    @GetMapping(
            path = "/{mrn}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            description = "Returns the syntax definition that applies to the given MRN"
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

    @PostMapping(
            path = "/"
    )
    @PreAuthorize("@accessControlUtil.canManageNamespace(#syntaxCreationDTO.namespace)")
    public ResponseEntity<String> createNamespaceSyntax(@RequestBody SyntaxCreationDTO syntaxCreationDTO) {
        SyntaxCreationResultRedis tmpResult = new SyntaxCreationResultRedis();
        tmpResult.setCode(SyntaxCreationStatus.CREATING);
        tmpResult.setNamespace(syntaxCreationDTO.getNamespace());
        SyntaxCreationResultRedis result = creationStatusService.save(tmpResult);
        WebSocketConnectionManager connectionManager = new WebSocketConnectionManager(
                new StandardWebSocketClient(),
                new MrrWebSocketHandler(syntaxCreationDTO, creationStatusService, result, mapper),
                webSocketUrl
        );
        connectionManager.start();
        return ResponseEntity.accepted().body(tmpResult.getId());
    }

    @GetMapping(
            value = "/status/{creationId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SyntaxCreationResult> getSyntaxCreationStatus(@PathVariable String creationId, HttpServletRequest request) throws MrrRestException {
        Optional<SyntaxCreationResultRedis> resultRedis = creationStatusService.getById(creationId);
        if (resultRedis.isEmpty()) {
            throw new MrrRestException(HttpStatus.NOT_FOUND,
                    "The syntax creation status for the given MRN namespace could not be found", request.getServletPath());
        }
        SyntaxCreationResult result = resultRedis.get();
        if (!accessControlUtil.canManageNamespace(result.getNamespace())) {
            throw new MrrRestException(HttpStatus.FORBIDDEN, "You are not allowed to see this resource", request.getServletPath());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
