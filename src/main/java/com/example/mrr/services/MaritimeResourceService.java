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

import com.example.mrr.model.MaritimeResourceEntity;
import com.example.mrr.repositories.MaritimeResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaritimeResourceService {

    private MaritimeResourceRepository repository;

    @Autowired
    public void setRepository(MaritimeResourceRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void save(MaritimeResourceEntity entity) {
        repository.save(entity);
    }

    @Transactional
    public void delete(MaritimeResourceEntity entity) {
        repository.delete(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Page<MaritimeResourceEntity> getByMrn(String mrn, Pageable pageable) {
        return repository.getAllByMrn(mrn, pageable);
    }
}
