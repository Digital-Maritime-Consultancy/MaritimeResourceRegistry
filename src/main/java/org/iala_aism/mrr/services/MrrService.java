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

import org.iala_aism.mrr.model.MrrEntity;
import org.iala_aism.mrr.model.NamespaceEntity;
import org.iala_aism.mrr.repositories.MrrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MrrService {

    private MrrRepository repository;

    @Autowired
    public void setRepository(MrrRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MrrEntity save(MrrEntity mrrEntity) {
        return repository.save(mrrEntity);
    }

    @Transactional
    public void delete(MrrEntity mrrEntity) {
        repository.delete(mrrEntity);
    }

    @Transactional
    public void deleteByMrnNamespace(String mrnNamespace) {
        repository.deleteByMrnNamespace(mrnNamespace);
    }

    @Transactional
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public Optional<MrrEntity> getByMrnNamespace(String mrnNamespace) {
        return repository.getByMrnNamespace(mrnNamespace);
    }

    public Optional<MrrEntity> getById(String id) {
        return repository.findById(id);
    }

    public Optional<MrrEntity> searchForEarlierMrr(String mrnNamespace) {
        Optional<MrrEntity> maybeMrr = getByMrnNamespace(mrnNamespace);
        while (maybeMrr.isEmpty() && mrnNamespace.contains(":")) {
            mrnNamespace = mrnNamespace.substring(0, mrnNamespace.lastIndexOf(':'));
            maybeMrr = getByMrnNamespace(mrnNamespace);
        }
        return maybeMrr;
    }

    public MrrEntity searchForLaterMrr(NamespaceEntity namespace) {
        if (namespace == null) {
            return null;
        }
        if (namespace.getMrr() != null) {
            return namespace.getMrr();
        }
        for (NamespaceEntity ns : namespace.getChildNamespaces()) {
            MrrEntity mrr = searchForLaterMrr(ns);
            if (mrr != null)
                return mrr;
        }
        return null;
    }
}
