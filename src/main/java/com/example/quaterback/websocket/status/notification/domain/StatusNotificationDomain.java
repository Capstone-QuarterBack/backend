package com.example.quaterback.websocket.status.notification.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StatusNotificationDomain {
    private String messageTypeId;
    private String messageId;
    private String action;

    private LocalDateTime timeStamp;
    private String connectorStatus;
    private Integer evseId;
    private Integer connectorId;
}
