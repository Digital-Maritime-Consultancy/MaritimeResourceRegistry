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
import org.iala_aism.mrr.model.JsonSerializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Object that holds the necessary information for registering an MRN namespace syntax")
public class SyntaxCreationDTO implements JsonSerializable {
    @Schema(description = "The MRN namespace that the syntax describes")
    private String namespace;
    @Schema(description = "The parent MRN namespace that the syntax is a subset of")
    private String parentNamespace;
    @Schema(description = "The ABNF syntax of the MRN namespace")
    private String abnfSyntax;
    @Schema(description = "Contact information about the owner of the MRN namespace")
    private OwnerDTO namespaceOwner;
}
