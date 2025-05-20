package com.example.quaterback.api.domain.activeStationRecord;

import com.example.quaterback.api.domain.activeStationRecord.service.ActiveStationService;
import com.example.quaterback.api.domain.chargerUptime.entity.ChargerUptimeEntity;
import com.example.quaterback.api.domain.chargerUptime.repository.ChargerInfoRepository;
import com.example.quaterback.api.domain.chargerUptime.repository.SpringDataJpaChargerInfoRepository;
import com.example.quaterback.api.domain.station.entity.ChargingStationEntity;
import com.example.quaterback.api.domain.station.repository.ChargingStationRepository;
import com.example.quaterback.api.domain.station.repository.SpringDataJpaChargingStationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class UptimeRolloverScheduler {
    private final ActiveStationService activeService;
    private final SpringDataJpaChargerInfoRepository uptimeRepo;
    private final SpringDataJpaChargingStationRepository stationRepo;

    @Scheduled(cron = "0 0 0 * * *")
    public void rollover() {
        // 오늘 00:00:00
        LocalDateTime todayMidnight = LocalDate.now().atStartOfDay();
        // 어제 자정부터 어제 23:59:59.999...
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime yStart = yesterday.atStartOfDay();
        LocalDateTime yEnd   = yesterday.atTime(LocalTime.MAX);

        for (String stationId : activeService.getAllActiveStationIds()) {
            LocalDateTime start = activeService.getStartTime(stationId);
            // 어제 구간 이전에 켜진 상태만 처리
            if (start == null || !start.isBefore(todayMidnight)) continue;

            // 어제 자정 전 구간(어제 on→자정) upTime 계산
            LocalDateTime cutoff = todayMidnight;
            long secondsUp = Duration.between(start, cutoff).toSeconds();
            double percentUp = secondsUp / 86_400.0 * 100;

            ChargingStationEntity station = stationRepo.findByStationId(stationId)
                    .orElseThrow(() -> new EntityNotFoundException(stationId));

            // ① 어제 날짜의 existing 엔티티가 있으면 누적, 없으면 신규 생성
            ChargerUptimeEntity entity = uptimeRepo
                    .findByStationAndCreatedAtBetween(station, yStart, yEnd)
                    .map(existing -> {
                        existing.accumulate(percentUp, yEnd);
                        existing.updateReason("EndOfDay");
                        return existing;
                    })
                    .orElseGet(() -> ChargerUptimeEntity.of(station, percentUp, yEnd, "EndOfDay"));

            uptimeRepo.save(entity);

            // ② 자정을 기점으로 다시 측정 시작
            activeService.activate(stationId);
        }
    }
}
