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
import org.iala_aism.mrr.model.MrrEntity;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Object representing a reference to another MRR")
public class MrrDTO implements JsonSerializable {
    @Schema(description = "The unique ID of the MRR", accessMode = READ_ONLY)
    private Long id;
    @Schema(description = "The MRN namespace of the MRR")
    private String mrnNamespace;
    @URL(protocol = "https")
    @Schema(description = "The endpoint of the MRR")
    private String endpoint;

    @Schema(description = "The owner of the MRR")
    private OwnerDTO owner;

    public MrrDTO(MrrEntity mrrEntity) {
        this.id = mrrEntity.getId();
        this.mrnNamespace = mrrEntity.getMrnNamespace();
        this.endpoint = mrrEntity.getEndpoint();
        this.owner = new OwnerDTO(mrrEntity.getOwner());
    }
}
