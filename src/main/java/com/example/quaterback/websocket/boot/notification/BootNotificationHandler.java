package com.example.quaterback.websocket.boot.notification;

import com.example.quaterback.annotation.Handler;
import com.example.quaterback.websocket.OcppMessageHandler;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Handler
public class BootNotificationHandler implements OcppMessageHandler {
    @Override
    public String getAction() {
        return "BootNotification";
    }

    @Override
    public void handle(WebSocketSession session, JsonNode message) throws IOException {

    }
}
