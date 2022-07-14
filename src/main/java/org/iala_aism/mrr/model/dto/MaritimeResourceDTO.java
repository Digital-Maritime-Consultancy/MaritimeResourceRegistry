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

package org.iala_aism.mrr.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.iala_aism.mrr.model.JsonSerializable;
import org.iala_aism.mrr.model.MaritimeResourceEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaritimeResourceDTO implements JsonSerializable {
    private String mrn;
    private long version;
    private String location;
    private String title;
    private String description;
    private long id;

    public MaritimeResourceDTO(MaritimeResourceEntity maritimeResourceEntity) {
        this.mrn = maritimeResourceEntity.getMrn();
        this.version = maritimeResourceEntity.getVersion();
        this.location = maritimeResourceEntity.getLocation();
        this.title = maritimeResourceEntity.getTitle();
        this.description = maritimeResourceEntity.getDescription();
        this.id = maritimeResourceEntity.getId();
    }
}
