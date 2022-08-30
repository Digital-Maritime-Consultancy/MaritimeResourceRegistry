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
import lombok.Getter;
import org.iala_aism.mrr.model.JsonSerializable;
import org.iala_aism.mrr.model.NamespaceOwner;
import org.iala_aism.mrr.model.NamespaceSyntax;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Schema(description = "Object representing the syntax definition for a MRN namespace")
public class NamespaceSyntaxDTO implements JsonSerializable {
    @Schema(description = "The MRN namespace that this syntax describes", accessMode = READ_ONLY)
    private final String namespace;
    @Schema(description = "The ABNF syntax", accessMode = READ_ONLY)
    private final String abnfSyntax;
    @Schema(description = "A regular expression derived from the ABNF syntax", accessMode = READ_ONLY)
    private final String regex;
    @Schema(description = "The contact information of the namespace owner", accessMode = READ_ONLY)
    private final NamespaceOwnerDTO namespaceOwner;
    @Schema(description = "The unique ID of the namespace syntax", accessMode = READ_ONLY)
    private final long id;

    public NamespaceSyntaxDTO(NamespaceSyntax namespaceSyntax) {
        this.namespace = namespaceSyntax.getNamespace().getMrnNamespace();
        this.abnfSyntax = namespaceSyntax.getAbnfSyntax();
        this.regex = namespaceSyntax.getRegex();
        this.namespaceOwner = new NamespaceOwnerDTO(namespaceSyntax.getNamespaceOwner());
        this.id = namespaceSyntax.getId();
    }
}
