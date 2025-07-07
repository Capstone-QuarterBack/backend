package com.example.quaterback.api.feature.ocpplog.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class OcppLogFilterRequest {
    private String stationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer messageType;
    private String action;
    private int page = 0;
    private int size = 10;
}
