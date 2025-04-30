package com.example.quaterback.websocket.status.notification.converter;

import com.example.quaterback.annotation.Converter;
import com.example.quaterback.websocket.MessageUtil;
import com.example.quaterback.websocket.status.notification.domain.StatusNotificationDomain;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

@Converter
public class StatusNotificationConverter {
    public StatusNotificationDomain convertToStatusNotificationDomain(JsonNode jsonNode) {
        String messageId = MessageUtil.getMessageId(jsonNode);
        String messageTypeId = MessageUtil.getMessageTypeId(jsonNode);
        String action = MessageUtil.getAction(jsonNode);
        JsonNode payload = MessageUtil.getPayload(jsonNode);

        return StatusNotificationDomain.builder()
                .messageId(messageId)
                .messageTypeId(messageTypeId)
                .action(action)
                .connectorStatus(payload.path("connectorStatus").asText())
                .timeStamp(LocalDateTime.parse(payload.path("timestamp").asText()))
                .evseId(payload.path("evseId").asInt())
                .connectorId(payload.path("connectorId").asInt())
                .build();
    }
}
