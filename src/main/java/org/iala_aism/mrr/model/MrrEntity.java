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

package org.iala_aism.mrr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Node("MRR")
public class MrrEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String mrnNamespace;

    @URL(protocol = "https")
    @Property
    private String endpoint;

    @Relationship(value = "CONTAINS")
    private NamespaceEntity namespace;

    @Relationship(value = "OWNS_MRR", direction = INCOMING)
    private Owner owner;

    public MrrEntity(String mrnNamespace, String endpoint, Owner owner) {
        this.mrnNamespace = mrnNamespace;
        this.endpoint = endpoint;
        this.owner = owner;
    }
}
