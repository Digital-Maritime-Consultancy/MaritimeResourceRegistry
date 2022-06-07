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

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
@ToString
public class MrrRestException extends Exception {

    // mimics the standard spring error structure on exceptions
    protected final HttpStatus status;
    protected final String error;
    protected final String errorMessage;
    protected final String path;
    protected final long timestamp;

    public MrrRestException(HttpStatus status, String errorMessage, String path) {
        super(errorMessage);
        this.status = status;
        this.errorMessage = errorMessage;
        this.path = path;
        this.timestamp = Instant.now().toEpochMilli();
        this.error = status.getReasonPhrase();
    }
}
