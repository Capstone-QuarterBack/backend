package com.example.quaterback.api.feature.dashboard.controller.fixture;

import com.example.quaterback.api.domain.station.constant.StationStatus;
import com.example.quaterback.api.feature.dashboard.dto.query.ChargerUsageQuery;
import com.example.quaterback.api.feature.dashboard.dto.query.DashboardSummaryQuery;
import com.example.quaterback.api.feature.dashboard.dto.query.StationFullInfoQuery;
import com.example.quaterback.api.feature.dashboard.dto.request.CsPriceRequest;
import com.example.quaterback.api.feature.dashboard.dto.response.*;

import java.time.LocalDateTime;
import java.util.List;

public class DashboardFixture {
    public static HourlyDischargeResponse Hourly_샘플_응답() {
        return new HourlyDischargeResponse(1, 10.0);
    }

    public static HourlyDischargeResponse Hourly_샘플_응답2(){
        return new HourlyDischargeResponse(2, 20.0);
    }

    public static DashboardSummaryResponse summaryResponse_샘플_응답() {
        return DashboardSummaryResponse.from(summaryQuery_샘플_응답());
    }

    public static ChargerUsagePageResponse usagePage_샘플_응답() {
        List<ChargerUsageResponse> chargerUsageResponseList = List.of(usageResponse_샘플_응답(), usageResponse_샘플_응답());

        return ChargerUsagePageResponse.builder()
                .usage(chargerUsageResponseList)
                .currentPage(0)
                .totalElements(2L)
                .totalPages(1)
                .build();
    }

    public static CsPriceHistory csPriceHistory_샘플_응답() {
        return CsPriceHistory.builder()
                .csPrice(10.0)
                .updateTime(LocalDateTime.of(1,1,1,1,1,1))
                .build();
    }

    public static CsPriceHistory csPriceHistory_샘플_응답2() {
        return CsPriceHistory.builder()
                .csPrice(20.0)
                .updateTime(LocalDateTime.of(2,2,2,2,2,2))
                .build();
    }

    public static CsPriceRequest csPriceRequest_샘플_요청() {
        return new CsPriceRequest(10.0);
    }

    public static StationFullInfoResponse stationFullInfo_샘플_응답() {
        return StationFullInfoResponse.from(stationFullInfoQuery_샘플_응답());
    }
    private static StationFullInfoQuery stationFullInfoQuery_샘플_응답() {
        return StationFullInfoQuery.builder()
                .stationId("testId")
                .model("testModel")
                .address("testAddress")
                .stationStatus(StationStatus.ACTIVE)
                .updateStatusTimeStamp(LocalDateTime.of(1, 1, 1, 1, 1, 1))
                .chargerCount(10L)
                .activeCount(10L)
                .errorCount(10L)
                .disconnectedCount(10L)
                .build();
    }

    private static DashboardSummaryQuery summaryQuery_샘플_응답() {
        return new DashboardSummaryQuery(10,10,10);
    }


    private static ChargerUsageResponse usageResponse_샘플_응답() {
        return ChargerUsageResponse.from(usageQuery_샘플_응답());
    }

    private static ChargerUsageQuery usageQuery_샘플_응답() {
        return ChargerUsageQuery.builder()
                .time(LocalDateTime.of(1,1,1,1,1,1))
                .stationAddress("testAddress")
                .stationModel("testModel")
                .usageKwh(10.0)
                .priceWon(10.0)
                .confirmCode("testCode")
                .build();
    }


}
