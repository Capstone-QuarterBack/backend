package com.example.quaterback.api.feature.monitoring.dto.response;

import lombok.Builder;

@Builder
public record HourlyCongestion(
        int hour,
        Long count,
        boolean isPeak
) {
}