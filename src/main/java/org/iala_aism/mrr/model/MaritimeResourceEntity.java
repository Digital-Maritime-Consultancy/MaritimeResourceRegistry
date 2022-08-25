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
import lombok.With;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Node("MaritimeResource")
public class MaritimeResourceEntity {

    @Id
    @With
    @GeneratedValue
    private Long id;

    @Property
    private String mrn;
    
    @Property
    private String version;

    @URL
    @Property
    private String location;

    @Property
    private String name;

    @Property
    private String description;

    @Relationship(value = "FOLLOWS")
    private NamespaceEntity namespace;

    public MaritimeResourceEntity(String mrn, String version, String location, String name, String description) {
        this.mrn = mrn;
        this.version = version;
        this.location = location;
        this.name = name;
        this.description = description;
    }
}
