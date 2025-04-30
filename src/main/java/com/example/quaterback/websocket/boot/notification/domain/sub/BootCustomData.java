package com.example.quaterback.websocket.boot.notification.domain.sub;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BootCustomData {
    private String vendorId;
    private String stationId;
}
