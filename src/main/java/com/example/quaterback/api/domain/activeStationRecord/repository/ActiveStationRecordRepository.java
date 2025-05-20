package com.example.quaterback.api.domain.activeStationRecord.repository;

import com.example.quaterback.api.domain.activeStationRecord.ActiveStationRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActiveStationRecordRepository extends JpaRepository<ActiveStationRecordEntity, Long> {
    // stationId로 조회
    Optional<ActiveStationRecordEntity> findByStationId(String stationId);

    // stationId로 삭제
    void deleteByStationId(String stationId);
}