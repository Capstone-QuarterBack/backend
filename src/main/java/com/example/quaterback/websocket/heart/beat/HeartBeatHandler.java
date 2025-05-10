package com.example.quaterback.websocket.heart.beat;

import com.example.quaterback.common.annotation.Handler;
import com.example.quaterback.websocket.MessageUtil;
import com.example.quaterback.websocket.OcppMessageHandler;
import com.example.quaterback.websocket.RefreshTimeoutService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
@Slf4j
@Handler
@RequiredArgsConstructor
public class HeartBeatHandler implements OcppMessageHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RefreshTimeoutService refreshTimeoutService;
    @Override
    public String getAction() {
        return "HeartBeat";
    }

    @Override
    public void handle(WebSocketSession session, JsonNode message) throws IOException {
        String messageId = MessageUtil.getMessageId(message);
        String messageAction = MessageUtil.getAction(message);
        String sessionId = session.getId();

        refreshTimeoutService.refreshTimeout(sessionId);
        // 응답 메시지 생성
        ObjectMapper mapper = this.objectMapper;
        ArrayNode response = mapper.createArrayNode();
        response.add(3);  // MessageTypeId for CALL_RESULT
        response.add(messageId);  // 요청에서 가져온 messageId
        // payload 생성
        ObjectNode payloadNode = mapper.createObjectNode();
        response.add(payloadNode);

        // 메시지 전송
        try {
            session.sendMessage(new TextMessage(response.toString()));
        } catch (IOException e) {
            log.error("Error sending BootNotificationResponse", e);
        }
    }

    //Response 보내기

}
