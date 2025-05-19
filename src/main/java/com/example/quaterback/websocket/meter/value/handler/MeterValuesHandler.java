package com.example.quaterback.websocket.meter.value.handler;

import com.example.quaterback.common.annotation.Handler;
import com.example.quaterback.common.redis.service.RedisMapSessionToStationService;
import com.example.quaterback.websocket.MessageUtil;
import com.example.quaterback.websocket.OcppMessageHandler;
import com.example.quaterback.websocket.RefreshTimeoutService;
import com.example.quaterback.websocket.meter.value.service.MeterValuesService;
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
public class MeterValuesHandler implements OcppMessageHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RefreshTimeoutService refreshTimeoutService;
    private final MeterValuesService meterValueService;
    private final RedisMapSessionToStationService redisMapSessionToStationService;
    @Override
    public String getAction() {
        return "MeterValues";
    }

    @Override
    public JsonNode handle(WebSocketSession session, JsonNode jsonNode) {
        String messageId = MessageUtil.getMessageId(jsonNode);
        JsonNode payload = MessageUtil.getPayload(jsonNode);
        String messageAction = MessageUtil.getAction(jsonNode);

        log.info("MeterValues value : ");

        String sessionId = session.getId();
        refreshTimeoutService.refreshTimeout(sessionId);
        String stationId = redisMapSessionToStationService.getStationId(sessionId);
        // 응답 메시지 생성
        ObjectMapper mapper = this.objectMapper;
        ArrayNode response = mapper.createArrayNode();
        response.add(3);  // MessageTypeId for CALL_RESULT
        response.add(messageId);  // 요청에서 가져온 messageId
        // payload 생성
        ObjectNode payloadNode = mapper.createObjectNode();
        response.add(payloadNode);

        return response;
    }
}
