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

package org.iala_aism.mrr.exceptions;

import org.iala_aism.mrr.model.ExceptionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.net.URISyntaxException;

@ControllerAdvice
public class MrrExceptionResolver {

    @ExceptionHandler(MrrRestException.class)
    public ResponseEntity<ExceptionModel> processRestError(MrrRestException e) throws URISyntaxException {
        // mimics the standard spring error structure on exceptions
        ExceptionModel exp = new ExceptionModel(e.timestamp, e.status.value(), e.error, e.errorMessage, e.path);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (e.location != null) {
            httpHeaders.setLocation(new URI(e.location));
        }
        return new ResponseEntity<>(exp, httpHeaders, e.status);
    }
}
