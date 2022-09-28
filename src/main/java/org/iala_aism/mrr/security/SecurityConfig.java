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

package org.iala_aism.mrr.security;

import org.iala_aism.mrr.config.SimpleCorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

@EnableWebSecurity
public class SecurityConfig {

    private SimpleCorsFilter simpleCorsFilter;

    @Autowired
    public void setSimpleCorsFilter(SimpleCorsFilter simpleCorsFilter) {
        this.simpleCorsFilter = simpleCorsFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(simpleCorsFilter, ChannelProcessingFilter.class)
                .authorizeRequests(authz -> authz
                        .mvcMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        return http.build();
    }
}
