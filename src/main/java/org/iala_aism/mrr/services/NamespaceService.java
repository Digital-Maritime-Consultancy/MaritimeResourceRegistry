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

package org.iala_aism.mrr.services;

import org.iala_aism.mrr.model.NamespaceEntity;
import org.iala_aism.mrr.model.NamespaceSyntax;
import org.iala_aism.mrr.repositories.NamespaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NamespaceService {

    private NamespaceRepository repository;

    @Autowired
    public void setRepository(NamespaceRepository repository) {
        this.repository = repository;
    }

    public NamespaceEntity getNamespaceByMrn(String mrn) {
        return repository.findByMrnNamespace(mrn);
    }

    // Finds the syntax definition, if one exists, for the given MRN
    public NamespaceSyntax getNamespaceSyntaxByMrn(String mrn) {
        NamespaceEntity namespace = this.getNamespaceByMrn(mrn);
        // If a namespace entity doesn't exist for the given MRN we need to find one higher up in the tree
        while (namespace == null) {
            mrn = mrn.substring(0, mrn.lastIndexOf(':'));
            namespace = this.getNamespaceByMrn(mrn);
        }
        NamespaceSyntax syntax = null;
        while (syntax == null) {
            syntax = namespace.getNamespaceSyntax();
            namespace = namespace.getParentNamespace();
        }
        return syntax;
    }

    public NamespaceEntity createNamespace(String mrn) {
        NamespaceEntity namespaceEntity = getNamespaceByMrn(mrn);
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
