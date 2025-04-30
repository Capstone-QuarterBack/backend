package com.example.quaterback.websocket.boot.notification.domain;

import com.example.quaterback.websocket.boot.notification.domain.sub.BootCustomData;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BootNotificationDomain {
    private String messageTypeId;
    private String messageId;
    private String action;

    private String reason;
    private String model;

    private BootCustomData customData;

    public String extractStationId() {
        return customData.getStationId();
    }

    public String extractVendorId() {
        return customData.getVendorId();
    }
}
