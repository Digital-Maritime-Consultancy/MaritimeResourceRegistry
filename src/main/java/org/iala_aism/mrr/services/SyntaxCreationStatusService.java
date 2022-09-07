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

import org.iala_aism.mrr.model.SyntaxCreationResultRedis;
import org.iala_aism.mrr.repositories.SyntaxCreationStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SyntaxCreationStatusService {

    private SyntaxCreationStatusRepository repository;

    @Autowired
    public void setRepository(SyntaxCreationStatusRepository repository) {
        this.repository = repository;
    }

    public Optional<SyntaxCreationResultRedis> getByNamespace(String namespace) {
        return repository.getByNamespace(namespace);
    }

    public Optional<SyntaxCreationResultRedis> getById(String id) {
        return repository.findById(id);
    }

    public SyntaxCreationResultRedis save(SyntaxCreationResultRedis creationResultRedis) {
        return repository.save(creationResultRedis);
    }
}
