package com.example.quaterback.websocket.boot.notification.service;

import com.example.quaterback.mapping.repository.MappingSessionIdWithStationIdRepository;
import com.example.quaterback.station.constant.StationStatus;
import com.example.quaterback.station.domain.ChargingStationDomain;
import com.example.quaterback.station.entity.ChargingStationEntity;
import com.example.quaterback.websocket.boot.notification.converter.BootNotificationConverter;
import com.example.quaterback.websocket.boot.notification.fixture.BootNotificationFixture;
import com.example.quaterback.websocket.mapping.FakeMappingRepository;
import com.example.quaterback.websocket.station.repository.FakeChargingStationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BootNotificationServiceTest {

    private BootNotificationService bootNotificationService;
    private BootNotificationConverter converter;
    private FakeChargingStationRepository repository;
    private MappingSessionIdWithStationIdRepository mappingRepository;

    @BeforeEach
    void setUp() {
        converter = new BootNotificationConverter();
        repository = new FakeChargingStationRepository();
        mappingRepository = new FakeMappingRepository();
        bootNotificationService = new BootNotificationService(repository, converter, mappingRepository);
    }

    @Test
    void stationPowerUpTest_BootNotification_jsonNode를_받으면_충전소_상태를_갱신하고_충전소_id를_반환한다() {
        //given
        JsonNode jsonNode = BootNotificationFixture.createBootNotificationJsonNode(
                2,
                "12345678-boot-location",
                "BootNotification",
                "PowerUp",
                "R1",
                "quarterback",
                "quarterback",
                "station-001",
                37.5665,
                126.9780,
                "서울특별시 중구 세종대로 110"
        );

        // 충전소 초기 데이터 세팅 및 초기 값 저장
        ChargingStationEntity entity = BootNotificationFixture.createInitialChargingStationEntityFromJsonNode(
                jsonNode,
                StationStatus.INACTIVE,
                LocalDateTime.of(2025, 2, 10, 8, 30, 20)
        );
        LocalDateTime before = entity.getUpdateStatusTimeStamp();
        repository.initializeStorage(entity);
        String sessionId = "123";

        //when
        String result = bootNotificationService.updateStationStatus(jsonNode, sessionId);

        //then
        String stationId = entity.getStationId();
        assertThat(result).isEqualTo(stationId);

        ChargingStationDomain resultDomain = repository.findByStationId(stationId);
        assertThat(resultDomain.getStationStatus()).isEqualTo(StationStatus.ACTIVE);
        assertThat(resultDomain.getUpdateStatusTimeStamp()).isAfter(before);
    }
}