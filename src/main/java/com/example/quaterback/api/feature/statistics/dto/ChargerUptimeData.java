package com.example.quaterback.api.feature.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargerUptimeData {
    private double overallUptime;
    private double changePercent;
    private List<StationUptime> stationUptime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StationUptime {
        private String name;
        private double uptime;
    }
}