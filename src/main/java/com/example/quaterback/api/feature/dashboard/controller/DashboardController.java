package com.example.quaterback.api.feature.dashboard.controller;

import com.example.quaterback.api.feature.dashboard.dto.request.CsPriceRequest;
import com.example.quaterback.api.feature.dashboard.dto.request.StationAddRequest;
import com.example.quaterback.api.feature.dashboard.dto.response.*;
import com.example.quaterback.api.feature.dashboard.facade.DashboardFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
@Slf4j
public class DashboardController {

    private final DashboardFacade dashboardFacade;

    @GetMapping("/discharge-by-hour")
    public List<HourlyDischargeResponse> getDischargeByHour() {
        return dashboardFacade.getHourlyDischarge();
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse getDashboardSummary() {
        return dashboardFacade.getSummary();
    }

    @GetMapping("/chargers/usage")
    public ChargerUsagePageResponse getChargerUsage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return dashboardFacade.getChargerUsage(pageable);
    }

    @GetMapping("/electricity-price/cs-price")
    public List<CsPriceHistory> getCsElectricityPrice() {
        return dashboardFacade.getCsPriceHistory();
    }

    @PostMapping("/electricity-price/cs-price")
    public UpdateCsPriceResponse setCsPrice(@RequestBody CsPriceRequest request) {
        return dashboardFacade.updateCsPrice(request.getPrice());
    }

    @GetMapping("/electricity-price/kepco-price")
    public KepcoResponse getKepcoElectricityPrice() {
        return dashboardFacade.getKepcoPrice();
    }

    @GetMapping("/stations")
    public List<StationFullInfoResponse> getStations() {
        return dashboardFacade.getStations();
    }

    @GetMapping("/stations/{stationName}")
    public StationFullInfoResponse getStation(@PathVariable(name = "stationName") String stationName) {
        return dashboardFacade.getStation(stationName);
    }

    @PostMapping("/stations")
    public Long addStation(@RequestBody StationAddRequest stationAddRequest) {
        return 1L;
        //추후 개발 예정
    }

    @DeleteMapping("/stations/{stationName}")
    public DeleteResultResponse deleteStation(@PathVariable(name = "stationName") String stationName) {
        return dashboardFacade.removeStation(stationName);
    }
}
