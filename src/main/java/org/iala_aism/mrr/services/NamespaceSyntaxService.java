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

import org.iala_aism.mrr.model.NamespaceSyntax;
import org.iala_aism.mrr.repositories.NamespaceSyntaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NamespaceSyntaxService {

    private NamespaceSyntaxRepository repository;

    @Autowired
    public void setRepository(NamespaceSyntaxRepository repository) {
        this.repository = repository;
    }

    public NamespaceSyntax getNamespaceSyntax(String mrnNamespace) {
        return repository.findByMrnNamespace(mrnNamespace);
    }

    public NamespaceSyntax findNamespaceSyntaxForMrn(String mrn) {
        NamespaceSyntax syntax = repository.findByMrnNamespace(mrn);
        while (syntax == null) {
            if (mrn.lastIndexOf(':') > 0) {
                mrn = mrn.substring(0, mrn.lastIndexOf(':'));
                syntax = repository.findByMrnNamespace(mrn);
            } else {
                break;
            }
        }
        return syntax;
    }

    public Page<NamespaceSyntax> findNamespaceSyntaxesUnderNamespace(String namespace, Pageable pageable) {
        return repository.findByMrnNamespaceStartingWith(namespace, pageable);
    }

    public Page<NamespaceSyntax> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
