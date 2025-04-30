package com.example.quaterback.websocket.boot.notification.handler;

import com.example.quaterback.annotation.Handler;
import com.example.quaterback.websocket.MessageUtil;
import com.example.quaterback.websocket.OcppMessageHandler;
import com.example.quaterback.websocket.boot.notification.service.BootNotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Handler
@RequiredArgsConstructor
@Slf4j
public class BootNotificationHandler implements OcppMessageHandler {

    private final BootNotificationService bootNotificationService;
    private final ObjectMapper objectMapper;

    @Override
    public String getAction() {
        return "BootNotification";
    }

    @Override
    public void handle(WebSocketSession session, JsonNode jsonNode) {
        String messageId = MessageUtil.getMessageId(jsonNode);
        JsonNode payload = MessageUtil.getPayload(jsonNode);
        String reason = payload.path("reason").asText();
        log.info("BootNotification reason - {}", reason);

        if (reason.equals("PowerUp")) {
            String stationId = bootNotificationService.updateStationStatus(jsonNode, session.getId());
            //반환 메시지 전송
        }
    }
}
