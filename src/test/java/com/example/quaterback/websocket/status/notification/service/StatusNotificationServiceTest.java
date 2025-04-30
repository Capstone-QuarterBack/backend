package com.example.quaterback.websocket.status.notification.service;

import com.example.quaterback.charger.constant.ChargerStatus;
import com.example.quaterback.charger.domain.ChargerDomain;
import com.example.quaterback.websocket.charger.repository.FakeChargerRepository;
import com.example.quaterback.websocket.mapping.FakeMappingRepository;
import com.example.quaterback.websocket.status.notification.converter.StatusNotificationConverter;
import com.example.quaterback.websocket.status.notification.fixture.StatusNotificationFixture;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class StatusNotificationServiceTest {

    private StatusNotificationService statusNotificationService;
    private StatusNotificationConverter converter;
    private FakeChargerRepository repository;
    private FakeMappingRepository mappingRepository;

    @BeforeEach
    void setUp() {
        repository = new FakeChargerRepository();
        converter = new StatusNotificationConverter();
        mappingRepository = new FakeMappingRepository();
        statusNotificationService = new StatusNotificationService(repository, converter, mappingRepository);
    }

    @Test
    void chargerStatusUpdated_jsonNode를_받으면_충전기_상태를_갱신하고_evseId반환() {

        //given
        String stationId = "station-001";
        Integer evseId = 1;
        ChargerStatus status = ChargerStatus.OCCUPIED;
        JsonNode jsonNode = StatusNotificationFixture.createStatusNotificationJsonNode(
                2,
                "status-notif-001",
                "StatusNotification",
                "2025-04-20T16:30:00",
                status.toString(),
                evseId,
                evseId,
                "quarterback",
                stationId
        );
        LocalDateTime before = LocalDateTime.of(2025,4,10,12,12,12);
        repository.initializeStorage(
                ChargerStatus.AVAILABLE,
                before
        );
        String sessionId = "123";

        //when
        Integer result = statusNotificationService.chargerStatusUpdated(jsonNode, sessionId);

        //then
        assertThat(result).isEqualTo(evseId);

        ChargerDomain resultDomain = repository.findByStationIdAndEvseId(stationId, evseId);
        assertThat(resultDomain.getChargerStatus()).isEqualTo(ChargerStatus.OCCUPIED);
        assertThat(resultDomain.getUpdateStatusTimeStamp()).isAfter(before);
    }
}