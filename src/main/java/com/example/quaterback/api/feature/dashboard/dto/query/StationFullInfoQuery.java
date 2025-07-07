package com.example.quaterback.api.feature.dashboard.dto.query;

import com.example.quaterback.api.domain.station.constant.StationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StationFullInfoQuery {
    private String stationId;
    private String model;
    private String address;
    private StationStatus stationStatus;
    private LocalDateTime updateStatusTimeStamp;
    private Long chargerCount;
    private Long activeCount;
    private Long errorCount;
    private Long disconnectedCount;

}