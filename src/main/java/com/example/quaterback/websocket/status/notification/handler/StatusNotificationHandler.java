package com.example.quaterback.websocket.status.notification.handler;

import com.example.quaterback.annotation.Handler;
import com.example.quaterback.websocket.MessageUtil;
import com.example.quaterback.websocket.OcppMessageHandler;
import com.example.quaterback.websocket.status.notification.service.StatusNotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Handler
@RequiredArgsConstructor
@Slf4j
public class StatusNotificationHandler implements OcppMessageHandler {

    private final StatusNotificationService statusNotificationService;
    private final ObjectMapper objectMapper;

    @Override
    public String getAction() {
        return "StatusNotification";
    }

    @Override
    public void handle(WebSocketSession session, JsonNode jsonNode) throws IOException {
        String messageId = MessageUtil.getMessageId(jsonNode);
        JsonNode payload = MessageUtil.getPayload(jsonNode);
        String stationId = payload.path("customData").path("stationId").asText();
        Integer evseId = payload.path("evseId").asInt();
        String status = payload.path("connectorStatus").asText();
        log.info("StatusNotification - {} {} {}", stationId, evseId, status);

        Integer resultEvseId = statusNotificationService.chargerStatusUpdated(jsonNode, session.getId());
        log.info("updated {}", resultEvseId);
        //반환 메시지 전송
    }
}
