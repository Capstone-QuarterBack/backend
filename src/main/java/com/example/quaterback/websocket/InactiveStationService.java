package com.example.quaterback.websocket;

import com.example.quaterback.api.domain.activeStationRecord.service.ActiveStationService;
import com.example.quaterback.api.domain.charger.constant.ChargerStatus;
import com.example.quaterback.api.domain.charger.domain.ChargerDomain;
import com.example.quaterback.api.domain.charger.repository.ChargerRepository;
import com.example.quaterback.api.domain.chargerUptime.entity.ChargerUptimeEntity;
import com.example.quaterback.api.domain.chargerUptime.repository.SpringDataJpaChargerInfoRepository;
import com.example.quaterback.api.domain.station.constant.StationStatus;
import com.example.quaterback.api.domain.station.domain.ChargingStationDomain;
import com.example.quaterback.api.domain.station.entity.ChargingStationEntity;
import com.example.quaterback.api.domain.station.repository.ChargingStationRepository;
import com.example.quaterback.api.domain.station.repository.SpringDataJpaChargingStationRepository;
import com.example.quaterback.common.redis.service.RedisHeartbeatMonitorService;
import com.example.quaterback.common.redis.service.RedisMapSessionToStationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.*;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class InactiveStationService {
    private final ActiveStationService activeStationService;
    private final RedisMapSessionToStationService mapService;
    private final RedisHeartbeatMonitorService heartbeatMonitorService;
    private final ChargingStationRepository chargingStationRepository;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final ChargerRepository chargerRepository;
    private final SpringDataJpaChargerInfoRepository chargerInfoRepository;
    private final SpringDataJpaChargingStationRepository springDataJpaChargingStationRepository;
    private static final long HEARTBEAT_INTERVAL_MS = 600_000;

    @Transactional
    public void cleanInactiveStationsWithTx() {
        Set<String> keys = stringRedisTemplate.keys("sessionId:*");
        if (keys == null || keys.isEmpty()) return;

        for (String sessionKey : keys) {
            String sessionId = sessionKey.replace("sessionId:", "");
            String stationId = mapService.getStationId(sessionId);
            if (stationId == null) continue;

            Long lastHeartbeat = heartbeatMonitorService.getLastHeartbeat(stationId);
            if (lastHeartbeat == null || isExpired(lastHeartbeat)) {
                valueChange(stationId, sessionId, "InactiveHandler");
            }
        }
    }

    public void valueChange(String stationId, String sessionId, String reason) {
        // 1) 꺼짐 판정 시점에 upTime 누적
        activeStationService.getStartTime(stationId); // (internal startTime 조회)
        // 호출만으로 handleInactive 내부에서 DB에 퍼센트 누적 저장까지 끝남
        handleInactiveDbRecording(stationId, reason);

        ChargingStationDomain domain = chargingStationRepository.findByStationId(stationId);
        List<ChargerDomain> chargerDomains = chargerRepository.findByStationID(stationId);
        if (domain != null) {
            domain.updateStationStatus(StationStatus.INACTIVE);
            chargingStationRepository.update(domain);

            for(ChargerDomain charger : chargerDomains){
                charger.updateChargerStatus(ChargerStatus.UNAVAILABLE);
                chargerRepository.update(charger);
            }
            log.info("Station [{}] marked INACTIVE due to expired heartbeat", stationId);
        } else {
            log.warn("Station [{}] not found in DB", stationId);
        }

        mapService.removeMapping(sessionId);
        heartbeatMonitorService.removeHeartbeat(stationId);
    }

    private void handleInactiveDbRecording(String stationId, String reason) {
        // 1) 부팅 시각 조회
        LocalDateTime start = activeStationService.getStartTime(stationId);
        if (start == null) {
            return;
        }

        // 2) 꺼짐 시각 및 가동 비율 계산
        LocalDateTime end = LocalDateTime.now();
        long secondsUp = Duration.between(start, end).toSeconds();
        double percentUp = secondsUp / 86_400.0 * 100;

        // 3) 스테이션 엔티티 조회
       ChargingStationEntity station = springDataJpaChargingStationRepository.findByStationId(stationId)
               .orElseThrow(() -> new EntityNotFoundException(stationId));
        // 4) 오늘 날짜 범위
        LocalDate today = end.toLocalDate();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd   = today.atTime(LocalTime.MAX);

        // 5) 기존 엔티티 조회 후 accumulate() 또는 신규 of() 호출
        ChargerUptimeEntity entity = chargerInfoRepository
                .findByStationAndCreatedAtBetween(station, dayStart, dayEnd)
                .map(existing -> {
                    existing.accumulate(percentUp, end);
                    existing.updateReason(reason);
                    return existing;
                })
                .orElseGet(() ->
                        ChargerUptimeEntity.of(station, percentUp, end, reason)
                );

        // 6) 저장 및 Active 레코드 삭제
        chargerInfoRepository.save(entity);
        activeStationService.deactivate(stationId);
    }

    private boolean isExpired(Long lastTimestamp) {
        return Instant.now().toEpochMilli() - lastTimestamp > HEARTBEAT_INTERVAL_MS;
    }
}
