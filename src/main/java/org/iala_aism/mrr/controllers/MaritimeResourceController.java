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
import org.iala_aism.mrr.model.MaritimeResourceDTO;
import org.iala_aism.mrr.model.MaritimeResourceEntity;
import org.iala_aism.mrr.model.MrrEntity;
import org.iala_aism.mrr.model.NamespaceSyntax;
import org.iala_aism.mrr.services.MaritimeResourceService;
import org.iala_aism.mrr.services.MrrService;
import org.iala_aism.mrr.services.NamespaceService;
import org.iala_aism.mrr.services.NamespaceSyntaxService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/resource")
public class MaritimeResourceController {

    private MaritimeResourceService resourceService;
    private NamespaceService namespaceService;
    private NamespaceSyntaxService namespaceSyntaxService;
    private MrrService mrrService;

    @Autowired
    public void setResourceService(MaritimeResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Autowired
    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    @Autowired
    public void setNamespaceSyntaxService(NamespaceSyntaxService namespaceSyntaxService) {
        this.namespaceSyntaxService = namespaceSyntaxService;
    }

    @Autowired
    public void setMrrService(MrrService mrrService) {
        this.mrrService = mrrService;
    }

    @GetMapping(
            value = "/{mrn}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Page<MaritimeResourceDTO> getResourcesForMrn(@PathVariable String mrn, @ParameterObject Pageable pageable, HttpServletRequest request) throws MrrRestException {
        Page<MaritimeResourceEntity> resourceEntities = resourceService.getAllByMrn(mrn, pageable);

        if (resourceEntities.isEmpty()) {
            Optional<MrrEntity> maybeMrr = mrrService.searchForEarlierMrr(mrn);
            if (maybeMrr.isPresent())
                throw new MrrRestException(HttpStatus.SEE_OTHER,
                        "Please repeat your query in the MRR for the namespace " + maybeMrr.get().getMrnNamespace(),
                        request.getServletPath(), maybeMrr.get().getEndpoint() + request.getServletPath());
        }

        return resourceEntities.map(MaritimeResourceDTO::new);
    }

    @GetMapping(
            value = "/{mrn}/{version}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MaritimeResourceDTO> getResourceByMrnAndVersion(@PathVariable String mrn, @PathVariable Long version, HttpServletRequest request) throws MrrRestException {
        Optional<MaritimeResourceEntity> maybeResource = resourceService.getByMrnAndVersion(mrn, version);

        if (maybeResource.isEmpty()) {
            Optional<MrrEntity> maybeMrr = mrrService.searchForEarlierMrr(mrn);
            if (maybeMrr.isPresent())
                throw new MrrRestException(HttpStatus.SEE_OTHER,
                        "Please repeat your query in the MRR for the namespace " + maybeMrr.get().getMrnNamespace(),
                        request.getServletPath(), maybeMrr.get().getEndpoint() + request.getServletPath());
            throw new MrrRestException(HttpStatus.NOT_FOUND, "The requested resource could not be found",
                    request.getServletPath());
        }
        return new ResponseEntity<>(new MaritimeResourceDTO(maybeResource.get()), HttpStatus.OK);
    }

    @GetMapping(
            value = "/id/{resourceId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MaritimeResourceDTO> getResourceById(@PathVariable Long resourceId, HttpServletRequest request) throws MrrRestException {
        Optional<MaritimeResourceEntity> resourceEntityOptional = resourceService.getById(resourceId);
        MaritimeResourceEntity resourceEntity = resourceEntityOptional.orElseThrow(
                () -> new MrrRestException(HttpStatus.NOT_FOUND, "The requested resource could not be found",
                        request.getServletPath())
        );
        return new ResponseEntity<>(new MaritimeResourceDTO(resourceEntity), HttpStatus.OK);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MaritimeResourceDTO> createResource(@RequestBody MaritimeResourceDTO maritimeResourceDTO, HttpServletRequest request) throws MrrRestException {
        try {
            MaritimeResourceEntity newResource = handleCreation(maritimeResourceDTO, request);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI("/resource/id/" + newResource.getId().toString()));
            return new ResponseEntity<>(new MaritimeResourceDTO(newResource), headers, HttpStatus.CREATED);
        } catch (URISyntaxException e) {
            throw new MrrRestException(HttpStatus.BAD_REQUEST, e.getMessage(), request.getServletPath());
        }
    }

    private MaritimeResourceEntity handleCreation(MaritimeResourceDTO maritimeResourceDTO, HttpServletRequest request) throws URISyntaxException, MrrRestException {
        MaritimeResourceEntity entity = new MaritimeResourceEntity(maritimeResourceDTO.getMrn(), maritimeResourceDTO.getVersion(),
                maritimeResourceDTO.getLocation(), maritimeResourceDTO.getTitle(), maritimeResourceDTO.getDescription());

        Optional<MrrEntity> maybeMrr = mrrService.searchForEarlierMrr(maritimeResourceDTO.getMrn());
        if (maybeMrr.isPresent()) {
            throw new MrrRestException(HttpStatus.BAD_REQUEST,
                    String.format("An MRR for the namespace %s exists. Please register your resource there",
                            maybeMrr.get().getMrnNamespace()), request.getServletPath());
        }

        Optional<MaritimeResourceEntity> maybeResource = resourceService.getByMrnAndVersion(entity.getMrn(), entity.getVersion());
        if (maybeResource.isPresent()) {
            throw new MrrRestException(HttpStatus.CONFLICT, "A resource with the given combination of MRN and version already exists",
                    request.getServletPath());
        }

        NamespaceSyntax syntax = namespaceSyntaxService.findNamespaceSyntaxForMrn(entity.getMrn());
        if (syntax == null) {
            throw new URISyntaxException("A syntax definition could not be found for the MRN of the resource", entity.getMrn());
        }
        Pattern pattern = Pattern.compile(syntax.getRegex());
        if (pattern.matcher(entity.getMrn()).matches()) {
            entity.setNamespace(namespaceService.createNamespace(entity.getMrn()));
            return resourceService.save(entity);
        } else {
            throw new URISyntaxException(entity.getMrn(),
                    String.format("The MRN of the resource does not follow the syntax definition for %s",
                            syntax.getNamespace().getMrnNamespace()));
        }
    }
}
