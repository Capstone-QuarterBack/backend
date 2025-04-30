package com.example.quaterback.websocket.mapping;

import com.example.quaterback.mapping.repository.MappingSessionIdWithStationIdRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakeMappingRepository implements MappingSessionIdWithStationIdRepository {

    private final Map<String, String> st_se = new ConcurrentHashMap<>();
    private final Map<String, String> se_st = new ConcurrentHashMap<>();

    @Override
    public String update(String stationId, String sessionId) {
        st_se.put(stationId, sessionId);
        se_st.put(sessionId, stationId);
        return sessionId;
    }

    @Override
    public String findStationIdBySessionId(String sessionId) {
        return se_st.get(sessionId);
    }
}
