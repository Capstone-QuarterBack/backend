package com.example.quaterback.api.feature.dashboard.dto.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ChargerUsageQuery {
    private LocalDateTime time;
    private String stationAddress;
    private String stationModel;
    private Double usageKwh;
    private Double priceWon;
    private String confirmCode;

    public String getUsageKwh() {
        return usageKwh+" (Wh)";
    }

    public String getPriceWon() {
        return String.format("%.2f (KRW)", priceWon);
    }
}
