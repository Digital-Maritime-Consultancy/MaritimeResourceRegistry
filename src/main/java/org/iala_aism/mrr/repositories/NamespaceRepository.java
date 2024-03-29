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

package org.iala_aism.mrr.repositories;

import org.iala_aism.mrr.model.NamespaceEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NamespaceRepository extends Neo4jRepository<NamespaceEntity, String> {

    NamespaceEntity findByMrnNamespace(String mrnNamespace);
}
