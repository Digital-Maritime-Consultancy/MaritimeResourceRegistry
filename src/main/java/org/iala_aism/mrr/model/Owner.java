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
import org.iala_aism.mrr.model.dto.OwnerDTO;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Node("Owner")
public class Owner {
    @Id
    @GeneratedValue
    private Long id;
    @Property
    private String name;
    @Property
    private String email;
    @Property
    private String phone;
    @Property
    private String url;
    @Property
    private String address;
    @Property
    private String country;
    @Relationship(value = "OWNS_NAMESPACE")
    private Set<NamespaceSyntax> ownedNamespaces;
    @Relationship(value = "OWNS_MRR")
    private Set<MrrEntity> ownedMrrs;

    public Owner(OwnerDTO ownerDTO) {
        this.name = ownerDTO.getName();
        this.email = ownerDTO.getEmail();
        this.phone = ownerDTO.getPhone();
        this.url = ownerDTO.getUrl();
        this.address = ownerDTO.getAddress();
        this.country = ownerDTO.getCountry();
    }
}
