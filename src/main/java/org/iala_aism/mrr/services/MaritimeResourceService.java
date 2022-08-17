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

import org.iala_aism.mrr.model.MaritimeResourceEntity;
import org.iala_aism.mrr.repositories.MaritimeResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MaritimeResourceService {

    private MaritimeResourceRepository repository;

    @Autowired
    public void setRepository(MaritimeResourceRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MaritimeResourceEntity save(MaritimeResourceEntity entity) {
        return repository.save(entity);
    }

    @Transactional
    public void delete(MaritimeResourceEntity entity) {
        repository.delete(entity);
    }

    @Transactional
    public void deleteByMrnAndVersion(String mrn, String version) {
        repository.deleteByMrnAndVersion(mrn, version);
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Optional<MaritimeResourceEntity> getById(Long id) {
        return repository.findById(id);
    }

    public Optional<MaritimeResourceEntity> getByMrnAndVersion(String mrn, String version) {
        return repository.getByMrnAndVersion(mrn, version);
    }

    public Optional<MaritimeResourceEntity> getLatestByMrn(String mrn) {
        List<MaritimeResourceEntity> resourceEntities = repository.getByMrn(mrn);

        // Sort based on versions with '.' seperated parts
        resourceEntities.sort((mr1, mr2) -> {
            String[] mr1VersionSplit = mr1.getVersion().split("\\.");
            String[] mr2VersionSplit = mr2.getVersion().split("\\.");

            for (int i = 0; i < mr1VersionSplit.length; i++) {
                int v1 = Integer.parseInt(mr1VersionSplit[i]);
                int v2 = Integer.parseInt(mr2VersionSplit[i]);

                if (v1 < v2)
                    return -1;
                else if(v1 > v2)
                    return 1;
            }
            return 0;
        });

        if (!resourceEntities.isEmpty())
            return Optional.of(resourceEntities.get(0));
        return Optional.empty();
    }

    public Page<MaritimeResourceEntity> getAllByMrn(String mrn, Pageable pageable) {
        return repository.getAllByMrn(mrn, pageable);
    }

    public List<MaritimeResourceEntity> getAll() {
        return (List<MaritimeResourceEntity>) repository.findAll();
    }
}
