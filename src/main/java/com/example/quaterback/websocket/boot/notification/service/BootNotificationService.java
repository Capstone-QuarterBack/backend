package com.example.quaterback.websocket.boot.notification.service;

import com.example.quaterback.mapping.repository.MappingSessionIdWithStationIdRepository;
import com.example.quaterback.station.constant.StationStatus;
import com.example.quaterback.station.domain.ChargingStationDomain;
import com.example.quaterback.station.repository.ChargingStationRepository;
import com.example.quaterback.websocket.boot.notification.converter.BootNotificationConverter;
import com.example.quaterback.websocket.boot.notification.domain.BootNotificationDomain;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BootNotificationService {

    private final ChargingStationRepository chargingStationRepository;
    private final BootNotificationConverter converter;
    private final MappingSessionIdWithStationIdRepository mappingRepository;

    @Transactional
    public String updateStationStatus(JsonNode jsonNode, String sessionId) {
        BootNotificationDomain bootNotificationDomain = converter.convertToBootNotificationDomain(jsonNode);

        mappingRepository.update(bootNotificationDomain.extractStationId(), sessionId);

        ChargingStationDomain chargingStationDomain = chargingStationRepository.findByStationId(bootNotificationDomain.extractStationId());

        chargingStationDomain.updateStationStatus(StationStatus.ACTIVE);
        String stationId = chargingStationRepository.update(chargingStationDomain);

        return stationId;
    }
}
