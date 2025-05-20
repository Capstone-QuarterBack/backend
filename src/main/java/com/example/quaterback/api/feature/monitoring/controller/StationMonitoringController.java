package com.example.quaterback.api.feature.monitoring.controller;

import com.example.quaterback.api.feature.monitoring.dto.response.*;
import com.example.quaterback.api.feature.monitoring.facade.StationMonitoringFacade;
import com.example.quaterback.websocket.transaction.event.service.TransactionEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/monitoring")
public class StationMonitoringController {

    private final StationMonitoringFacade stationMonitoringFacade;
    private final TransactionEventService transactionEventService;

    @GetMapping("/charging-history/{stationId}")
    public ChargingRecordResponsePage getChargingHistoryPaged(
            @PathVariable(name = "stationId") String stationId,
            @RequestParam(name="page",defaultValue = "0") int page,
            @RequestParam(name="size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("endedTime").descending());
        return stationMonitoringFacade.getChargingHistory(stationId, pageable);
    }

    @GetMapping("/congestion/{stationId}")
    public List<HourlyCongestion> getHourlyCongestion(
            @PathVariable(name = "stationId") String stationId
    ) {
        return stationMonitoringFacade.getCongestionChart(stationId);
    }
    @GetMapping("/evse-ids/{stationId}")
    public List<EvseIdResponse> getEvseIds(
            @PathVariable(name="stationId") String stationId
    ){
        return stationMonitoringFacade.getEvseIds(stationId);
    }

    @GetMapping("/charger-info/{stationId}/available/{evseId}")
    public AvailableChargerPageResponse getAvailableChargerInfo(
            @PathVariable(name="stationId") String stationId,
            @PathVariable(name="evseId") Integer evseId,
            @RequestParam(name="page",defaultValue = "0") int page,
            @RequestParam(name ="size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("endedTime").descending());
        return stationMonitoringFacade.getAvailableChargerInfo(stationId, evseId, pageable);
    }

    @GetMapping("/charger-info/{stationId}/unavailable/{evseId}")
    public UnavailableChargerPageResponse getUnavailableChargerInfo(
            @PathVariable(name="stationId") String stationId,
            @PathVariable(name="evseId") Integer evseId,
            @RequestParam(name="page",defaultValue = "0") int page,
            @RequestParam(name ="size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("endedTime").descending());
        return stationMonitoringFacade.getUnavailableChargerInfo(stationId, evseId, pageable);
    }

    //todo 충전요금 추후 계발
    @GetMapping("/charger-money/{stationId}")
    public Long todoCharger() {
        return 1L;
    }

    @GetMapping("/ess-value/{stationId}")
    public EssValueResponse getEssValue(@PathVariable(name = "stationId") String stationId) {
        return stationMonitoringFacade.getEssValue(stationId);
    }

    @GetMapping("/live-streaming/{evseId}/{stationId}")
    public List<TimeAndValueDto> getLive(
            @PathVariable(name = "evseId") Integer evseId,
            @PathVariable(name = "stationId") String stationId
    ){
         return transactionEventService.getLiveInfo(evseId,stationId);

    }
}
