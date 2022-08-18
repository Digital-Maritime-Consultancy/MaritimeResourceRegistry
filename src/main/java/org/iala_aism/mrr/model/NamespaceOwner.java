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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Representation of the contact information of a namespace owner")
public class NamespaceOwner implements JsonSerializable {
    @Schema(description = "The name of the namespace owner", accessMode = READ_ONLY)
    private String name;
    @Schema(description = "The mail for the point of contact of the namespace owner", accessMode = READ_ONLY)
    private String email;
    @Schema(description = "The phone number for the point of contact of the namespace owner", accessMode = READ_ONLY)
    private String phone;
    @Schema(description = "The URL for the website of the namespace owner", accessMode = READ_ONLY)
    private String url;
    @Schema(description = "The address of the namespace owner", accessMode = READ_ONLY)
    private String address;
    @Schema(description = "The country of the namespace owner", accessMode = READ_ONLY)
    private String country;
}
