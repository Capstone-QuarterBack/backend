package com.example.quaterback.api.domain.chargerUptime.repository;

import com.example.quaterback.api.domain.chargerUptime.entity.ChargerUptimeEntity;
import com.example.quaterback.api.feature.statistics.dto.response.StatisticsData;

import java.time.LocalDateTime;
import java.util.List;

public interface ChargerInfoRepository {
    List<ChargerUptimeEntity> getInfosByTimeRange(
            LocalDateTime start, LocalDateTime end);

    List<StatisticsData.ChartData> findOperatingRate();
}
