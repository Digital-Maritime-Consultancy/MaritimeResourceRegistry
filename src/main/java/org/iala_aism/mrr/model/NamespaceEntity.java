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
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Node("Namespace")
public class NamespaceEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String mrnNamespace;

    @Relationship(value = "EXTENDS")
    @Setter
    private NamespaceEntity parentNamespace;

    @Relationship(value = "EXTENDS", direction = Direction.INCOMING)
    private Set<NamespaceEntity> childNamespaces;

    @Relationship(value = "DESCRIBES", direction = Direction.INCOMING)
    private NamespaceSyntax namespaceSyntax;

    public NamespaceEntity(String mrnNamespace) {
        this.mrnNamespace = mrnNamespace;
    }
}
