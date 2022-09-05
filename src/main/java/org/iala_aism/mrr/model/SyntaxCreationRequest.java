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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.iala_aism.mrr.model.dto.OwnerDTO;

@Getter
@Setter
public class SyntaxCreationRequest implements JsonSerializable {
    private String function;
    private String abnf;
    private String namespace;
    @JsonProperty("parent_namespace")
    private String parentNamespace;
    @JsonProperty("namespace_owner")
    private OwnerDTO ownerDTO;

    public SyntaxCreationRequest(String abnf, String namespace, String parentNamespace, OwnerDTO ownerDTO) {
        this.function = "create";
        this.abnf = abnf;
        this.namespace = namespace;
        this.parentNamespace = parentNamespace;
        this.ownerDTO = ownerDTO;
    }
}
