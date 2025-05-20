package com.example.quaterback.api.feature.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargerTroubleData {
    private List<ChargerFailure> chargerFailures;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChargerFailure {
        private String stationName;
        private String name;
        private int failureCount;
    }
}
