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

package org.iala_aism.mrr.components;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.iala_aism.mrr.model.SyntaxCreationRequest;
import org.iala_aism.mrr.model.SyntaxCreationResult;
import org.iala_aism.mrr.model.dto.SyntaxCreationDTO;
import org.iala_aism.mrr.model.enums.SyntaxCreationStatus;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

@Slf4j
public class MrrWebSocketHandler extends TextWebSocketHandler {

    private final SyntaxCreationDTO syntaxCreationDTO;
    private final Map<String, SyntaxCreationResult> syntaxCreationResultMap;
    private final ObjectMapper mapper;

    public MrrWebSocketHandler(SyntaxCreationDTO syntaxCreationDTO, Map<String, SyntaxCreationResult> syntaxCreationResultMap, ObjectMapper mapper) {
        this.syntaxCreationDTO = syntaxCreationDTO;
        this.syntaxCreationResultMap = syntaxCreationResultMap;
        this.mapper = mapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        log.debug("WebSocket connection established");
        SyntaxCreationRequest request = new SyntaxCreationRequest(syntaxCreationDTO.getAbnfSyntax(),
                syntaxCreationDTO.getNamespace(), syntaxCreationDTO.getParentNamespace(),
                syntaxCreationDTO.getNamespaceOwner());
        String requestJson = mapper.writeValueAsString(request);
        TextMessage requestMessage = new TextMessage(requestJson);
        SyntaxCreationResult tmpResult = new SyntaxCreationResult();
        tmpResult.setCode(SyntaxCreationStatus.CREATING);
        syntaxCreationResultMap.put(syntaxCreationDTO.getNamespace(), tmpResult);
        session.sendMessage(requestMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        log.debug("WebSocket connection closed");
        if (status != CloseStatus.NORMAL) {
            log.error("WebSocket connection didn't close normally");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        String messageJson = message.getPayload();
        SyntaxCreationResult result = mapper.readValue(messageJson, SyntaxCreationResult.class);
        if (SyntaxCreationStatus.ERROR.equals(result.getCode())) {
            log.error("Syntax creation for namespace {} failed: {}", syntaxCreationDTO.getNamespace(), result.getMessage());
        } else if (!syntaxCreationDTO.getNamespace().equals(result.getNamespace())) {
            result = new SyntaxCreationResult();
            result.setMessage("The MRN namespace of the returned response did not match the MRN namespace of the original request");
            result.setCode(SyntaxCreationStatus.ERROR);
        }
        syntaxCreationResultMap.put(syntaxCreationDTO.getNamespace(), result);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        log.error("Client transport error: {}", exception.getMessage());
    }
}
