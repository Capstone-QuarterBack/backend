package com.example.quaterback.websocket.status.notification.handler;

import com.example.quaterback.common.annotation.Handler;
import com.example.quaterback.websocket.MessageUtil;
import com.example.quaterback.websocket.OcppMessageHandler;
import com.example.quaterback.websocket.RefreshTimeoutService;
import com.example.quaterback.websocket.status.notification.service.StatusNotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Handler
@RequiredArgsConstructor
@Slf4j
public class StatusNotificationHandler implements OcppMessageHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RefreshTimeoutService refreshTimeoutService;
    private final StatusNotificationService statusNotificationService;
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

        String sessionId = session.getId();
        refreshTimeoutService.refreshTimeout(sessionId);

        Integer resultEvseId = statusNotificationService.chargerStatusUpdated(jsonNode, session.getId());
        log.info("updated {}", resultEvseId);
        // 응답 메시지 생성
        ObjectMapper mapper = this.objectMapper;
        ArrayNode response = mapper.createArrayNode();
        response.add(3);  // MessageTypeId for CALL_RESULT
        response.add(messageId);  // 요청에서 가져온 messageId
        // payload 생성
        ObjectNode payloadNode = mapper.createObjectNode();
        ObjectNode customDataNode = mapper.createObjectNode();
        customDataNode.put("pricePermW", 1);
        payloadNode.set("customData", customDataNode);
        response.add(payloadNode);

        // 메시지 전송
        try {
            session.sendMessage(new TextMessage(response.toString()));
            log.info("Sent BootNotificationResponse: {}", response);
        } catch (IOException e) {
            log.error("Error sending BootNotificationResponse", e);
        }
    }
}
