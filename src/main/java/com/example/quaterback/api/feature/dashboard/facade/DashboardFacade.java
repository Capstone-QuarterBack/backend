package com.example.quaterback.api.feature.dashboard.facade;

import com.example.quaterback.api.domain.charger.service.ChargerService;
import com.example.quaterback.api.domain.price.service.KepcoService;
import com.example.quaterback.api.domain.price.service.PriceService;
import com.example.quaterback.api.domain.station.service.StationService;
import com.example.quaterback.api.feature.dashboard.dto.response.*;
import com.example.quaterback.common.annotation.Facade;
import com.example.quaterback.websocket.transaction.event.service.TransactionEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Facade
@RequiredArgsConstructor
@Slf4j
public class DashboardFacade {

    private final TransactionEventService transactionEventService;
    private final ChargerService chargerService;
    private final StationService stationService;
    private final PriceService priceService;
    private final KepcoService kepcoService;

    public List<HourlyDischargeResponse> getHourlyDischarge() {
       return transactionEventService.getHourlyDischarge();
    }

    public DashboardSummaryResponse getSummary() {
        return transactionEventService.getDailySummary();
    }

    public ChargerUsagePageResponse getChargerUsage(Pageable pageable) {
        return transactionEventService.getChargerUsage(pageable);
    }

    public List<StationFullInfoResponse> getStations() {
        return stationService.getStationInfos();
    }

    public StationFullInfoResponse getStation(String stationName) {
        return stationService.getStationInfo(stationName);
    }

    public DeleteResultResponse removeStation(String stationName) {
         return stationService.removeStation(stationName);
    }

    public List<CsPriceHistory> getCsPriceHistory() {
        return priceService.getCsPriceHistory();
    }

    public UpdateCsPriceResponse updateCsPrice(double price) {
        priceService.updatePrice(price);
        return new UpdateCsPriceResponse("success");
    }

    public KepcoResponse getKepcoPrice() {
        return kepcoService.getCurrentKepcoPrice();
    }
}
