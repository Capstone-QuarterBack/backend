package com.example.quaterback.api.feature.ocpplog.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OcppLogResponse {
    private LocalDateTime timestamp;
    private String stationId;
    private Integer messageType;
    private String action;
    private JsonNode rawMessage;
}
