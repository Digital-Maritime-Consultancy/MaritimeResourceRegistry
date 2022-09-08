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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.iala_aism.mrr.model.enums.SyntaxCreationStatus;

@Getter
@Setter
@Schema(description = "Object representing the result of a namespace syntax creation")
public class SyntaxCreationResult implements JsonSerializable {
    @Schema(description = "The status of the creation")
    private SyntaxCreationStatus code;
    @Schema(description = "The MRN namespace of the created syntax")
    private String namespace;
    @Schema(description = "A regular expression generated from the created syntax")
    private String regex;
    @Schema(description = "An error message that is set if the creation results in an error")
    private String message;
}
