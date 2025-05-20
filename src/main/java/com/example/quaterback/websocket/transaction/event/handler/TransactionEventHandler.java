package com.example.quaterback.websocket.transaction.event.handler;

import com.example.quaterback.api.domain.txinfo.service.TransactionInfoService;
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
import org.springframework.web.socket.WebSocketSession;

@Handler
@RequiredArgsConstructor
@Slf4j
public class TransactionEventHandler implements OcppMessageHandler {

    private final TransactionEventService transactionEventService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RefreshTimeoutService refreshTimeoutService;
    private final TransactionInfoService transactionInfoService;

    @Override
    public String getAction() {
        return "TransactionEvent";
    }

    @Override
    public JsonNode handle(WebSocketSession session, JsonNode jsonNode) {
        String messageId = MessageUtil.getMessageId(jsonNode);
        JsonNode payload = MessageUtil.getPayload(jsonNode);
        String eventType = payload.path("eventType").asText();
        log.info("Transaction : {}", eventType);
        String sessionId = session.getId();
        refreshTimeoutService.refreshTimeout(sessionId);
        String tx_id = null;
        switch (eventType) {
            case "Started":
                tx_id = transactionEventService.saveTxInfo(jsonNode, session.getId());
                log.info("Transaction save info : {}", tx_id);
                //sendTransactionEventStarted(session, messageId);
                break;
            case "Updated":
                tx_id = transactionEventService.saveTxLog(jsonNode);
                log.info("Transaction save log : {}", tx_id);
                break;
            case "Ended":
                tx_id = transactionEventService.updateTxEndTime(jsonNode);
                log.info("Transaction save end time : {}", tx_id);
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
        if (eventType.equals("Ended")) {
            payloadNode.put("totalPrice", transactionInfoService.getOneTxInfo(tx_id).getChargeSummary().getTotalPrice());
        }
        response.add(payloadNode);

        return response;
    }
}