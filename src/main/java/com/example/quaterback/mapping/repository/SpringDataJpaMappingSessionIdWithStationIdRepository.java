package com.example.quaterback.mapping.repository;

import com.example.quaterback.mapping.entity.MappingSessionIdWithStationIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaMappingSessionIdWithStationIdRepository extends JpaRepository<MappingSessionIdWithStationIdEntity, Long> {
    MappingSessionIdWithStationIdEntity findByStationId(String stationId);
    MappingSessionIdWithStationIdEntity findBySessionId(String sessionId);
}
