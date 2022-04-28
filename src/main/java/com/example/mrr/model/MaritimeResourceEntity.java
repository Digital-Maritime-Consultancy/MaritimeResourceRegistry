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
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

@Getter
@AllArgsConstructor
@Node("MaritimeResource")
public class MaritimeResourceEntity {

    @Id
    @With
    @GeneratedValue
    private Long id;

    @Property
    private final String mrn;

    @URL
    @Property
    private final String location;

    @Property
    private final String title;

    @Property
    private final String description;

    @Setter
    @Relationship(value = "FOLLOWS")
    private NamespaceEntity namespace;

    public MaritimeResourceEntity(String mrn, String location, String title, String description) {
        this.mrn = mrn;
        this.location = location;
        this.title = title;
        this.description = description;
    }
}
