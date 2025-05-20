package com.example.quaterback.api.domain.chargerUptime.repository;

import com.example.quaterback.api.domain.chargerUptime.entity.ChargerUptimeEntity;
import com.example.quaterback.api.feature.statistics.dto.query.DayOperatingDto;
import com.example.quaterback.api.feature.statistics.dto.response.StatisticsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaChargerInfoRepository implements ChargerInfoRepository{
    private final SpringDataJpaChargerInfoRepository chargerInfoRepository;
    @Override
    public List<ChargerUptimeEntity> getInfosByTimeRange(LocalDateTime start, LocalDateTime end) {
        List<ChargerUptimeEntity> chargerInfoEntities = chargerInfoRepository.findByTimeRange(
                start, end);

        return chargerInfoEntities;
    }

    @Override
    public List<StatisticsData.ChartData> findOperatingRate() {
        List<DayOperatingDto> list = chargerInfoRepository.findOperatingRate();
        return list.stream()
                .map(dto -> StatisticsData.ChartData.builder()
                        .label(dto.getTimeType())
                        .value(dto.getTotalUpTime())
                        .build())
                .toList();
    }
}
