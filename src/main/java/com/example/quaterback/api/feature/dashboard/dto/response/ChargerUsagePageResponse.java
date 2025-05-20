package com.example.quaterback.api.feature.dashboard.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ChargerUsagePageResponse(
        List<ChargerUsageResponse> usage,
        Integer currentPage,
        Long totalElements,
        Integer totalPages
) {
}
