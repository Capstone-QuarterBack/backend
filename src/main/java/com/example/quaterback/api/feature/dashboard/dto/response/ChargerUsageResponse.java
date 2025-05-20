package com.example.quaterback.api.feature.dashboard.dto.response;

import com.example.quaterback.api.feature.dashboard.dto.query.ChargerUsageQuery;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record
ChargerUsageResponse(
        LocalDateTime timestamp,
        String chargerLocation,
        String chargerNumber,
        String usage,
        String price,
        String transactionId
) {

    public static ChargerUsageResponse from(ChargerUsageQuery query) {
        return ChargerUsageResponse.builder()
                .timestamp(query.getTime())
                .chargerLocation(query.getStationAddress())
                .chargerNumber(query.getStationModel())
                .usage(query.getUsageKwh())
                .price(query.getPriceWon())
                .transactionId(query.getConfirmCode())
                .build();
    }
}
