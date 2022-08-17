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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.iala_aism.mrr.model.JsonSerializable;
import org.iala_aism.mrr.model.MaritimeResourceEntity;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Object representing a reference to a maritime resource")
public class MaritimeResourceDTO implements JsonSerializable {
    @Schema(description = "The MRN of the resource")
    private String mrn;
    @Schema(description = "The version of the resource in the format X.Y.Z and more formally given by the following regex: ^(0|[1-9]\\d\\*).(0|[1-9]\\d\\*).(0|[1-9]\\d\\*)$")
    private String version;
    @URL
    @Schema(description = "The location of the resource in the form of a URL")
    private String location;
    @Schema(description = "The title of the resource")
    private String title;
    @Schema(description = "A description of the resource")
    private String description;
    @Schema(description = "The unique ID of the resource in the MRR", accessMode = READ_ONLY)
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
