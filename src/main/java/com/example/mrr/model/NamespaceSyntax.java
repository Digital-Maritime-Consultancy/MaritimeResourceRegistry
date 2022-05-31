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

package com.example.mrr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

@Data
@AllArgsConstructor
@Node("NamespaceSyntax")
public class NamespaceSyntax {

    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String abnfSyntax;

    @Property
    private String regex;

    @Relationship(value = "DESCRIBES")
    private final NamespaceEntity namespace;

    public NamespaceSyntax(String abnfSyntax, String regex, NamespaceEntity namespace) {
        this.abnfSyntax = abnfSyntax;
        this.regex = regex;
        this.namespace = namespace;
    }
}
