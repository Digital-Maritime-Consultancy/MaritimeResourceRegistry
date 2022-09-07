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

package org.iala_aism.mrr.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("accessControlUtil")
@Slf4j
public class AccessControlUtil {

    public boolean canManageNamespace(String mrn) {
        log.debug("Checking if user is allowed to manage namespace \"{}\"", mrn);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.isAuthenticated() && auth instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            List<String> managesNamespaces = jwtAuthenticationToken.getToken().getClaimAsStringList("manages_namespaces");
            if (managesNamespaces != null && !managesNamespaces.isEmpty()) {
                for (String namespace : managesNamespaces) {
                    if (!namespace.isBlank() && mrn.startsWith(namespace)) {
                        log.debug("User is allowed to manage namespace \"{}\"", mrn);
                        return true;
                    }
                }
            }
        }
        log.debug("User is not allowed to manage namespace \"{}\"", mrn);
        return false;
    }
}
