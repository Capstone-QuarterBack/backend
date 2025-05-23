package com.example.quaterback.websocket.status.notification.service;

import com.example.quaterback.api.domain.charger.constant.ChargerStatus;
import com.example.quaterback.api.domain.charger.domain.ChargerDomain;
import com.example.quaterback.common.redis.service.RedisMapSessionToStationService;
import com.example.quaterback.api.domain.charger.repository.FakeChargerRepository;
import com.example.quaterback.websocket.status.notification.converter.StatusNotificationConverter;
import com.example.quaterback.websocket.status.notification.fixture.StatusNotificationFixture;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StatusNotificationServiceTest {

    @Mock
    private RedisMapSessionToStationService redisMappingService;

    private StatusNotificationService statusNotificationService;
    private StatusNotificationConverter converter;
    private FakeChargerRepository repository;

    @BeforeEach
    void setUp() {
        repository = new FakeChargerRepository();
        converter = new StatusNotificationConverter();
        statusNotificationService = new StatusNotificationService(repository, converter, redisMappingService);
    }

    @Test
    void chargerStatusUpdated_jsonNode를_받으면_충전기_상태를_갱신하고_evseId반환() {

        //given
        given(redisMappingService.getStationId(anyString())).willReturn("station-001");

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
                evseId
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