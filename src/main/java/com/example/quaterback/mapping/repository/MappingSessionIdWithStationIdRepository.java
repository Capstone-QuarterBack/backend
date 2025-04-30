package com.example.quaterback.mapping.repository;

public interface MappingSessionIdWithStationIdRepository {

    String update(String stationId, String sessionId);
    String findStationIdBySessionId(String sessionId);
}
