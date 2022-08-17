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
import org.iala_aism.mrr.model.dto.MaritimeResourceDTO;
import org.iala_aism.mrr.model.MaritimeResourceEntity;
import org.iala_aism.mrr.model.MrrEntity;
import org.iala_aism.mrr.model.NamespaceSyntax;
import org.iala_aism.mrr.services.MaritimeResourceService;
import org.iala_aism.mrr.services.MrrService;
import org.iala_aism.mrr.services.NamespaceService;
import org.iala_aism.mrr.services.NamespaceSyntaxService;
import org.iala_aism.mrr.utils.AccessControlUtil;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@EnableMethodSecurity
@RestController
@RequestMapping("/resource")
public class MaritimeResourceController {

    public static final String COULD_NOT_BE_FOUND = "The requested resource could not be found";
    private MaritimeResourceService resourceService;
    private NamespaceService namespaceService;
    private NamespaceSyntaxService namespaceSyntaxService;
    private MrrService mrrService;
    private AccessControlUtil accessControlUtil;

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

    @Autowired
    public void setAccessControlUtil(AccessControlUtil accessControlUtil) {
        this.accessControlUtil = accessControlUtil;
    }

    @GetMapping(
            value = "/{mrn}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            description = "Returns a page of all versions of the resource with the given MRN"
    )
    public Page<MaritimeResourceDTO> getAllResourcesForMrn(@PathVariable String mrn, @ParameterObject Pageable pageable, HttpServletRequest request) throws MrrRestException {
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
            value = "/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            description = "Returns the list containing all registered resources. Is only here for testing purposes and will be removed in the future."
    )
    public ResponseEntity<List<MaritimeResourceDTO>> getAllMaritimeResources() {
        List<MaritimeResourceEntity> allResources = resourceService.getAll();
        return new ResponseEntity<>(allResources.stream().map(MaritimeResourceDTO::new).toList(), HttpStatus.OK);
    }

    @GetMapping(
            value = "/{mrn}/{version}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            description = "Returns the resource with the given MRN and version"
    )
    public ResponseEntity<MaritimeResourceDTO> getResourceByMrnAndVersion(@PathVariable String mrn, @PathVariable String version, HttpServletRequest request) throws MrrRestException {
        Optional<MaritimeResourceEntity> maybeResource = resourceService.getByMrnAndVersion(mrn, version);

        if (maybeResource.isEmpty())
            throw handleOptionalResource(mrn, request);
        return new ResponseEntity<>(new MaritimeResourceDTO(maybeResource.get()), HttpStatus.OK);
    }

    @GetMapping(
            value = "/{mrn}/latest",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            description = "Returns the latest version of the resource with the given MRN"
    )
    public ResponseEntity<MaritimeResourceDTO> getLatestVersionOfResourceByMrn(@PathVariable String mrn, HttpServletRequest request) throws MrrRestException {
        Optional<MaritimeResourceEntity> maybeResource = resourceService.getLatestByMrn(mrn);

        if (maybeResource.isEmpty())
            throw handleOptionalResource(mrn, request);
        return new ResponseEntity<>(new MaritimeResourceDTO(maybeResource.get()), HttpStatus.OK);
    }

    @GetMapping(
            value = "/id/{resourceId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            description = "Returns the resource with the given ID"
    )
    public ResponseEntity<MaritimeResourceDTO> getResourceById(@PathVariable Long resourceId, HttpServletRequest request) throws MrrRestException {
        Optional<MaritimeResourceEntity> resourceEntityOptional = resourceService.getById(resourceId);
        MaritimeResourceEntity resourceEntity = resourceEntityOptional.orElseThrow(
                () -> new MrrRestException(HttpStatus.NOT_FOUND, COULD_NOT_BE_FOUND,
                        request.getServletPath())
        );
        return new ResponseEntity<>(new MaritimeResourceDTO(resourceEntity), HttpStatus.OK);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            description = "Creates a new resource"
    )
    @PreAuthorize("@accessControlUtil.canManageNamespace(#maritimeResourceDTO.mrn)")
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

    @DeleteMapping(
            value = "/{mrn}/{version}"
    )
    @Operation(
            description = "Deletes the resource with the given MRN and version"
    )
    @PreAuthorize("@accessControlUtil.canManageNamespace(#mrn)")
    public void deleteResourceByMrnAndVersion(@PathVariable String mrn, @PathVariable String version, HttpServletRequest request) throws MrrRestException {
        Optional<MaritimeResourceEntity> maybeResource = resourceService.getByMrnAndVersion(mrn, version);
        if (maybeResource.isEmpty())
            throw new MrrRestException(HttpStatus.NOT_FOUND, COULD_NOT_BE_FOUND, request.getServletPath());
        resourceService.deleteByMrnAndVersion(mrn, version);
    }

    @DeleteMapping(
            value = "/id/{resourceId}"
    )
    @Operation(
            description = "Deletes the resource with the given ID"
    )
    public void deleteResourceById(@PathVariable Long resourceId, HttpServletRequest request, HttpServletResponse response) throws MrrRestException {
        Optional<MaritimeResourceEntity> maybeResource = resourceService.getById(resourceId);
        if (maybeResource.isEmpty())
            throw new MrrRestException(HttpStatus.NOT_FOUND, COULD_NOT_BE_FOUND, request.getServletPath());
        if (!accessControlUtil.canManageNamespace(maybeResource.get().getMrn())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        resourceService.deleteById(resourceId);
    }

    private MrrRestException handleOptionalResource(@PathVariable String mrn, HttpServletRequest request) {
        Optional<MrrEntity> maybeMrr = mrrService.searchForEarlierMrr(mrn);
        return maybeMrr.map(mrrEntity -> new MrrRestException(HttpStatus.SEE_OTHER,
                "Please repeat your query in the MRR for the namespace " + mrrEntity.getMrnNamespace(),
                request.getServletPath(), mrrEntity.getEndpoint() + request.getServletPath()))
                .orElseGet(() -> new MrrRestException(HttpStatus.NOT_FOUND, COULD_NOT_BE_FOUND,
                request.getServletPath()));
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
