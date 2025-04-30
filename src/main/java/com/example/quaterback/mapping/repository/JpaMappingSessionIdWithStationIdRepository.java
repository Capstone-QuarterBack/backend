package com.example.quaterback.mapping.repository;

import com.example.quaterback.mapping.entity.MappingSessionIdWithStationIdEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaMappingSessionIdWithStationIdRepository implements MappingSessionIdWithStationIdRepository {

    private final SpringDataJpaMappingSessionIdWithStationIdRepository repository;

    @Override
    public String update(String stationId, String sessionId) {
        MappingSessionIdWithStationIdEntity entity = repository.findByStationId(stationId);
        entity.setSessionId(sessionId);
        return entity.getSessionId();
    }

    @Override
    public String findStationIdBySessionId(String sessionId) {
        MappingSessionIdWithStationIdEntity entity = repository.findBySessionId(sessionId);
        return entity.getStationId();
    }

}
