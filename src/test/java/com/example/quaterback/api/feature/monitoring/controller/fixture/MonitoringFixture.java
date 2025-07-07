package com.example.quaterback.api.feature.monitoring.controller.fixture;

import com.example.quaterback.api.domain.charger.constant.ChargerStatus;
import com.example.quaterback.api.domain.txinfo.domain.TransactionInfoDomain;
import com.example.quaterback.api.feature.monitoring.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

public class MonitoringFixture {

    public static ChargingRecordResponsePage 충전_기록_페이지(){
        ChargingRecordResponse recordResponse = 충전_기록();
        List<ChargingRecordResponse> recordResponseList = List.of(recordResponse);

        return ChargingRecordResponsePage.builder()
                .stationName("테스트이름")
                .essValue("테스트값")
                .page(0)
                .size(10)
                .totalPages(1)
                .totalElements(1)
                .records(recordResponseList)
                .build();
    }

    public static HourlyCongestion 시간당_혼잡(int hour, Long count, boolean isPeak){
        return HourlyCongestion.builder()
                .count(count)
                .hour(hour)
                .isPeak(isPeak)
                .build();
    }

    public static EvseIdResponse 충전기_상태(Integer evseId, ChargerStatus status) {
        return EvseIdResponse.builder()
                .evseId(evseId)
                .chargerStatus(status)
                .build();
    }

    public static AvailableChargerPageResponse 가용_충전기_페이지_응답() {
        // 일일 요약 DTO
        DailyUsageDto dailyUsageDto = DailyUsageDto.builder()
                .totalChargedEnergy(321.5)
                .totalVehicleCount(3L)
                .totalRevenue(45000.0)
                .chargedEnergyDiffPercent(12)
                .vehicleCountDiffPercent(5)
                .revenueDiffPercent(8)
                .build();

        // 트랜잭션 도메인 페이지 생성
        List<TransactionInfoDomain> domainList = IntStream.range(0, 3)
                .mapToObj(i -> TransactionInfoDomain.builder()
                        .transactionId("tx-" + i)
                        .startedTime(LocalDateTime.now().minusHours(2 + i))
                        .endedTime(LocalDateTime.now().minusHours(1 + i))
                        .vehicleNo("12가345" + i)
                        .idToken("idToken-" + i)
                        .stationId("STATION-001")
                        .evseId(i + 1)
                        .totalMeterValue(50.0 + i * 10)
                        .totalPrice(1000.0 + i * 500)
                        .errorCode(null)
                        .build()
                )
                .toList();

        Page<TransactionInfoDomain> domainPage = new PageImpl<>(
                domainList,
                PageRequest.of(0, 10),
                domainList.size()
        );

        return AvailableChargerPageResponse.from(dailyUsageDto, domainPage);
    }

    public static UnavailableChargerPageResponse 비가용_충전기_페이지_응답() {
        List<TransactionInfoDomain> domainList = IntStream.range(0, 2)
                .mapToObj(i -> TransactionInfoDomain.builder()
                        .transactionId("unavailable-tx-" + i)
                        .startedTime(LocalDateTime.now().minusHours(3 + i))
                        .endedTime(LocalDateTime.now().minusHours(2 + i))
                        .vehicleNo("98나765" + i)
                        .idToken("idToken-unavailable-" + i)
                        .stationId("STATION-002")
                        .evseId(i + 10)
                        .totalMeterValue(30.0 + i * 5)
                        .totalPrice(800.0 + i * 200)
                        .errorCode("ConnectorError")
                        .build()
                )
                .toList();

        Page<TransactionInfoDomain> domainPage = new PageImpl<>(
                domainList,
                PageRequest.of(0, 10),
                domainList.size()
        );

        return UnavailableChargerPageResponse.from(domainPage);
    }


    private static ChargingRecordResponse 충전_기록() {
        return ChargingRecordResponse.builder()
                .startTime(LocalDateTime.of(1,1,1,1,1,1))
                .endTime(LocalDateTime.of(1,1,1,1,1,1))
                .transactionId("1")
                .priceKRW(10.0)
                .build();
    }


}
