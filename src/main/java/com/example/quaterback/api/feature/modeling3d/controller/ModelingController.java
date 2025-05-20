package com.example.quaterback.api.feature.modeling3d.controller;

import com.example.quaterback.api.domain.charger.constant.ChargerStatus;
import com.example.quaterback.api.domain.station.constant.StationStatus;
import com.example.quaterback.api.feature.modeling3d.dto.StationInfoResponse;
import com.example.quaterback.api.feature.modeling3d.dto.UpdateChargerStatusResponse;
import com.example.quaterback.api.feature.modeling3d.service.ModelingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/modeling")
public class ModelingController {

    private final ModelingService modelingService;

    @GetMapping("/stations-info")
    public ResponseEntity<StationInfoResponse> StationsInfos() {
        StationInfoResponse response = modelingService.getStationsInfo();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/stations/{stationId}/{status}")
    public ResponseEntity<String> updateStationState(
            @PathVariable(name="stationId") String stationId,
            @PathVariable(name="status") StationStatus status
    ){
        String updatedStationId = modelingService.updateStation(stationId, status);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedStationId);
    }

    @PutMapping("/stations/{stationId}/chargers/{evseId}/{status}")
    public ResponseEntity<UpdateChargerStatusResponse> updateChargerState(
            @PathVariable(name="stationId") String stationId,
            @PathVariable(name="evseId")Integer evseId,
            @PathVariable(name="status") ChargerStatus status
    ) throws IOException {
        UpdateChargerStatusResponse updateResponse = modelingService.updateCharger(stationId, evseId, status);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updateResponse);
    }
}
