package com.example.quaterback.api.domain.activeStationRecord.service;

import com.example.quaterback.api.domain.activeStationRecord.ActiveStationRecordEntity;
import com.example.quaterback.api.domain.activeStationRecord.repository.ActiveStationRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActiveStationService {
    private final ActiveStationRecordRepository repo;

    /** 부팅 시 호출: 기존 기록 삭제 후 새로 저장 */
    public void activate(String stationId) {
        // 중복 방지: 이미 있으면 지우고
        repo.deleteByStationId(stationId);

        // 새로운 부팅 시각 저장
        ActiveStationRecordEntity record = ActiveStationRecordEntity.builder()
                .stationId(stationId)
                .startTime(LocalDateTime.now())
                .build();
        repo.save(record);
    }
    public LocalDateTime getStartTime(String stationId) {
        return repo.findByStationId(stationId)
                .map(ActiveStationRecordEntity::getStartTime)
                .orElse(null);
    }

    public void deactivate(String stationId) {
        repo.deleteByStationId(stationId);
    }

    public List<String> getAllActiveStationIds() {
        return repo.findAll().stream()
                .map(ActiveStationRecordEntity::getStationId)
                .toList();
    }
}
