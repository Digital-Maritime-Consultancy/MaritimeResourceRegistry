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

package com.example.mrr.services;

import com.example.mrr.model.NamespaceEntity;
import com.example.mrr.repositories.NamespaceRepository;
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
}
