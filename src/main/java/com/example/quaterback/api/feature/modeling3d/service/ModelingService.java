package com.example.quaterback.api.feature.modeling3d.service;

import com.example.quaterback.api.domain.charger.constant.ChargerStatus;
import com.example.quaterback.api.domain.charger.domain.ChargerDomain;
import com.example.quaterback.api.domain.charger.repository.ChargerRepository;
import com.example.quaterback.api.domain.charger.repository.SpringDataJpaChargerRepository;
import com.example.quaterback.api.domain.station.constant.StationStatus;
import com.example.quaterback.api.domain.station.domain.ChargingStationDomain;
import com.example.quaterback.api.domain.station.repository.ChargingStationRepository;
import com.example.quaterback.api.domain.station.repository.SpringDataJpaChargingStationRepository;
import com.example.quaterback.api.feature.modeling3d.dto.StationInfoResponse;
import com.example.quaterback.api.feature.modeling3d.dto.UpdateChargerStatusResponse;
import com.example.quaterback.websocket.OcppWebSocketHandler;
import com.example.quaterback.websocket.ReactWebSocketHandler;
import com.example.quaterback.websocket.mongodb.MongoDBService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelingService {
    private final ChargingStationRepository chargingStationRepository;
    private final ChargerRepository chargerRepository;
    private final OcppWebSocketHandler ocppWebSocketHandler;
    private final ReactWebSocketHandler reactWebSocketHandler;
    private final MongoDBService mongoDBService;

    public StationInfoResponse getStationsInfo() {
        List<ChargingStationDomain> stations = chargingStationRepository.findAll();
        return StationInfoResponse.from(stations);
    }

    public String updateStation(String stationId, StationStatus status) {
        String updatedStationId = chargingStationRepository.updateStationStatus(stationId, status);
        return updatedStationId;
    }

    public UpdateChargerStatusResponse updateCharger(String stationId, Integer evseId, ChargerStatus status) throws IOException {
        ChargerDomain before = chargerRepository.findByStationIdAndEvseId(stationId, evseId);
        String operationStatus = status.equals(ChargerStatus.AVAILABLE) ? "Operative" : "Inoperative";
        if (before.getChargerStatus().equals(ChargerStatus.AVAILABLE) && operationStatus.equals("Operative")) {
            return UpdateChargerStatusResponse.from(before);
        }
        if (before.getChargerStatus().equals(ChargerStatus.OCCUPIED) && operationStatus.equals("Operative")) {
            return UpdateChargerStatusResponse.from(before);
        }
        if (before.getChargerStatus().equals(ChargerStatus.UNAVAILABLE) && operationStatus.equals("Inoperative")) {
            return UpdateChargerStatusResponse.from(before);
        }
        if (before.getChargerStatus().equals(ChargerStatus.FAULT)) {
            return UpdateChargerStatusResponse.from(before);
        }
        String action = before.getChargerStatus().equals(ChargerStatus.OCCUPIED) ? "RequestStopTransaction" : "ChangeAvailability";
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode rootArray = mapper.createArrayNode();
        rootArray.add(2);
        rootArray.add(UUID.randomUUID().toString());
        if (action.equals("ChangeAvailability")) {
            rootArray.add(action);
            ObjectNode payload = mapper.createObjectNode();
            payload.put("operationalStatus", operationStatus);
            ObjectNode evse = mapper.createObjectNode();
            evse.put("id", evseId);
            payload.set("evse", evse);
            rootArray.add(payload);
        }
        else {
            rootArray.add(action);
            ObjectNode payload = mapper.createObjectNode();
            payload.put("evseId", evseId);
            rootArray.add(payload);
        }

        for (WebSocketSession client : ocppWebSocketHandler.getSessions()) {
            if (client.isOpen()) {
                client.sendMessage(new TextMessage(rootArray.toString()));
                mongoDBService.saveMessage(mapper.writeValueAsString(rootArray), stationId, action);
                break;
            }
        }
        log.info("Sent Websocket message to CS : {}", rootArray);
        for (WebSocketSession client : reactWebSocketHandler.getSessions()) {
            if (client.isOpen()) {
                client.sendMessage(new TextMessage(rootArray.toString()));
            }
        }

        return UpdateChargerStatusResponse.from(before);
    }
}
