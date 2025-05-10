package com.example.quaterback.websocket.transaction.event.handler;

import com.example.quaterback.common.annotation.Handler;
import com.example.quaterback.websocket.MessageUtil;
import com.example.quaterback.websocket.OcppMessageHandler;
import com.example.quaterback.websocket.RefreshTimeoutService;
import com.example.quaterback.websocket.transaction.event.service.TransactionEventService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;

@Handler
@RequiredArgsConstructor
@Slf4j
public class TransactionEventHandler implements OcppMessageHandler {

    private final TransactionEventService transactionEventService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RefreshTimeoutService refreshTimeoutService;

    @Override
    public String getAction() {
        return "TransactionEvent";
    }

    @Override
    public void handle(WebSocketSession session, JsonNode jsonNode) throws IOException {
        String messageId = MessageUtil.getMessageId(jsonNode);
        JsonNode payload = MessageUtil.getPayload(jsonNode);
        String eventType = payload.path("eventType").asText();
        log.info(eventType);
        String sessionId = session.getId();
        refreshTimeoutService.refreshTimeout(sessionId);
        String tx_id;
        switch (eventType) {
            case "Started":
                tx_id = transactionEventService.saveTxInfo(jsonNode, session.getId());
                log.info("save info : {}", tx_id);
                //sendTransactionEventStarted(session, messageId);
                break;
            case "Updated":
                tx_id = transactionEventService.saveTxLog(jsonNode);
                log.info("save log : {}", tx_id);
                break;
            case "Ended":
                tx_id = transactionEventService.updateTxEndTime(jsonNode);
                log.info("save end time : {}", tx_id);
                break;
            default:
                // 무시
                break;
        }
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
            log.info("Sent BootNotificationResponse: {}", response);
        } catch (IOException e) {
            log.error("Error sending BootNotificationResponse", e);
        }
    }
}