package com.example.quaterback.websocket.boot.notification.converter;


import com.example.quaterback.annotation.Converter;
import com.example.quaterback.websocket.MessageUtil;
import com.example.quaterback.websocket.boot.notification.domain.BootNotificationDomain;
import com.example.quaterback.websocket.boot.notification.domain.sub.BootCustomData;
import com.fasterxml.jackson.databind.JsonNode;

@Converter
public class BootNotificationConverter {
    public BootNotificationDomain convertToBootNotificationDomain(JsonNode jsonNode) {
        String messageId = MessageUtil.getMessageId(jsonNode);
        String messageTypeId = MessageUtil.getMessageTypeId(jsonNode);
        String action = MessageUtil.getAction(jsonNode);
        JsonNode payload = MessageUtil.getPayload(jsonNode);

        return BootNotificationDomain.builder()
                .messageId(messageId)
                .messageTypeId(messageTypeId)
                .action(action)
                .reason(payload.path("reason").asText())
                .model(payload.path("chargingStation").path("model").asText())
                .customData(new BootCustomData(
                        payload.path("customData").path("vendorId").asText(),
                        payload.path("customData").path("stationId").asText())
                )
                .build();
    }
}
