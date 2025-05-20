package com.example.quaterback.api.feature.statistics.service;

import com.example.quaterback.api.domain.charger.constant.ChargerStatus;
import com.example.quaterback.api.domain.charger.domain.ChargerDomain;
import com.example.quaterback.api.domain.charger.entity.ChargerEntity;
import com.example.quaterback.api.domain.charger.repository.ChargerRepository;
import com.example.quaterback.api.domain.chargerUptime.entity.ChargerUptimeEntity;
import com.example.quaterback.api.domain.chargerUptime.repository.ChargerInfoRepository;
import com.example.quaterback.api.domain.price.constant.Season;
import com.example.quaterback.api.domain.price.constant.TimeSlot;
import com.example.quaterback.api.domain.price.entity.KepcoPrice;
import com.example.quaterback.api.domain.price.entity.PricePerMwh;
import com.example.quaterback.api.domain.price.repository.KepcoRepository;
import com.example.quaterback.api.domain.price.repository.PriceRepository;
import com.example.quaterback.api.domain.price.service.KepcoService;
import com.example.quaterback.api.domain.station.repository.ChargingStationRepository;
import com.example.quaterback.api.domain.txinfo.entity.TransactionInfoEntity;
import com.example.quaterback.api.domain.txinfo.repository.TxInfoRepository;
import com.example.quaterback.api.feature.statistics.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final ChargerInfoRepository chargerInfoRepository;
    private final ChargerRepository chargerRepository;
    private final TxInfoRepository txInfoRepository;
    private final PriceRepository priceRepository;
    private final KepcoService kepcoService;
    private final ChargingStationRepository chargingStationRepository;


    public ChargerUptimeData getChargerRate(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDate today = LocalDate.now();
        LocalDate prevMonthStart = today.minusMonths(1).withDayOfMonth(1);
        LocalDate prevMonthEnd = today.minusMonths(1);

        LocalDateTime prevStartTime = prevMonthStart.atStartOfDay();
        LocalDateTime prevEndTime = prevMonthEnd.atTime(endTime.toLocalTime());

        List<ChargerUptimeEntity> prevChargerInfoEntities = chargerInfoRepository.getInfosByTimeRange(
                prevStartTime, prevEndTime);

        List<ChargerUptimeEntity> chargerInfoEntities = chargerInfoRepository.getInfosByTimeRange(
                startTime, endTime);

        // 현재 기간 평균
        double overallUptime = chargerInfoEntities.stream()
                .mapToDouble(ChargerUptimeEntity::getUpTime)
                .average()
                .orElse(0.0);

        // 이전 기간 평균
        double preOverallUptime = prevChargerInfoEntities.stream()
                .mapToDouble(ChargerUptimeEntity::getUpTime)
                .average()
                .orElse(0.0);

        // 변화율 계산
        double changePercent = 0.0;
        if (preOverallUptime > 0) {
            changePercent = ((overallUptime - preOverallUptime) / preOverallUptime) * 100;
        }

        // 충전소별 평균 uptime 계산
        Map<String, Double> avgUptimeByStation = chargerInfoEntities.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getStation().getStationName(),
                        Collectors.averagingDouble(ChargerUptimeEntity::getUpTime)
                ));

        // DTO 리스트로 변환
        List<ChargerUptimeData.StationUptime> stationUptimeList = avgUptimeByStation.entrySet().stream()
                .map(entry -> new ChargerUptimeData.StationUptime(
                        entry.getKey(),
                        entry.getValue()
                ))
                .collect(Collectors.toList());

        return new ChargerUptimeData(
                overallUptime,
                changePercent,
                stationUptimeList
        );
    }

    public ChargerTroubleData getChargerTrouble(){
        List<ChargerEntity> chargerEntities = chargerRepository.findAllCharger();

        List<ChargerTroubleData.ChargerFailure> failureList = chargerEntities.stream()
                .map(entity -> new ChargerTroubleData.ChargerFailure(
                        entity.getStation().getStationName(),                                 // id
                        "충전기 #" + entity.getEvseId(),                                 // name
                        entity.getTroubleCnt() != null ? entity.getTroubleCnt() : 0     // failureCount
                ))
                .toList();

        ChargerTroubleData responseDto = new ChargerTroubleData(failureList);
        return responseDto;
    }

    public PowerTradingRevenueData getPowerTradingData(LocalDateTime startTime, LocalDateTime endTime){
        LocalDate today = LocalDate.now();
        LocalDate prevMonthStart = today.minusMonths(1).withDayOfMonth(1);
        LocalDate prevMonthEnd = today.minusMonths(1);

        LocalDateTime prevStartTime = prevMonthStart.atStartOfDay();
        LocalDateTime prevEndTime = prevMonthEnd.atTime(endTime.toLocalTime());

        List<TransactionInfoEntity> txList = txInfoRepository.findTxInfoByTerm(startTime, endTime);
        List<TransactionInfoEntity> prevTxList = txInfoRepository.findTxInfoByTerm(prevStartTime, prevEndTime);

        double netRevenue = txList.stream()
                .mapToDouble(tx -> tx.getTotalPrice() != null ? tx.getTotalPrice() : 0.0)
                .sum();

        double prevNetRevenue = prevTxList.stream()
                .mapToDouble(tx-> tx.getTotalPrice() != null ? tx.getTotalPrice() : 0.0)
                .sum();

        String percentText;
        if (prevNetRevenue == 0.0) {
            if (netRevenue == 0.0) {
                percentText = "0%";
            } else {
                percentText = "신규 수익 발생"; // or "∞%"
            }
        } else {
            double percent = (netRevenue / prevNetRevenue - 1.0) * 100;
            percentText = String.format("%.1f%%", percent);
        }

        return new PowerTradingRevenueData(netRevenue, percentText);
    }

    public PowerTradingVolumeData getPowerVolumeData(LocalDateTime startTime, LocalDateTime endTime){
        List<TransactionInfoEntity> txList = txInfoRepository.findTxInfoByTerm(startTime, endTime);
        List<TransactionInfoEntity> filtered = txList.stream()
                .filter(tx -> tx.getTotalMeterValue() != null)
                .toList();

        double netVolume = filtered.stream()
                .mapToDouble(TransactionInfoEntity::getTotalMeterValue)
                .sum();
        // 최소값 객체 찾기
        TransactionInfoEntity minEntity = filtered.stream()
                .min(Comparator.comparing(TransactionInfoEntity::getTotalMeterValue))
                .orElse(null);

        // 최대값 객체 찾기
        TransactionInfoEntity maxEntity = filtered.stream()
                .max(Comparator.comparing(TransactionInfoEntity::getTotalMeterValue))
                .orElse(null);

        // 값과 날짜 분리
        double minVolume = minEntity != null ? minEntity.getTotalMeterValue() : 0.0;
        LocalDateTime minDate = minEntity != null ? minEntity.getStartedTime() : null;

        double maxVolume = maxEntity != null ? maxEntity.getTotalMeterValue() : 0.0;
        LocalDateTime maxDate = maxEntity != null ? maxEntity.getStartedTime() : null;


        LocalDate today = LocalDate.now();
        LocalDate prevMonthStart = today.minusMonths(1).withDayOfMonth(1);
        LocalDate prevMonthEnd = today.minusMonths(1);

        LocalDateTime prevStartTime = prevMonthStart.atStartOfDay();
        LocalDateTime prevEndTime = prevMonthEnd.atTime(endTime.toLocalTime());

        List<TransactionInfoEntity> prevtxList = txInfoRepository.findTxInfoByTerm(prevStartTime, prevEndTime);
        List<Double> prevMeterValues = prevtxList.stream()
                .map(TransactionInfoEntity::getTotalMeterValue)
                .filter(Objects::nonNull)
                .toList();
        double prevNetVolume = prevMeterValues.stream().mapToDouble(Double::doubleValue).sum();

        String percentText;
        if (prevNetVolume == 0.0) {
            if (netVolume == 0.0) {
                percentText = "0%";
            } else {
                percentText = "신규 전력거래 발생"; // or "∞%"
            }
        } else {
            double percent = (netVolume / prevNetVolume - 1.0) * 100;
            percentText = String.format("%.1f%%", percent);
        }

        return new PowerTradingVolumeData(netVolume, percentText, minVolume, minDate,  maxVolume, maxDate);
    }

    public PowerTradingPriceData getTradingPriceByMonth(LocalDateTime startTime, LocalDateTime endTime){
        List<PricePerMwh> prices = priceRepository.findByUpdatedDateTimeBetween(startTime, endTime);
        // TimeSlot -> Price 리스트 매핑
        Map<TimeSlot, List<Double>> grouped = prices.stream()
                .collect(Collectors.groupingBy(
                        p -> kepcoService.determineTimeSlot(p.getUpdatedDateTime()),
                        Collectors.mapping(PricePerMwh::getPricePerMwh, Collectors.toList())
                ));
        // 각 TimeSlot별 평균 및 리스트 구성
        List<TimeSlotPriceDetail> details = Arrays.stream(TimeSlot.values())
                .map(slot -> {
                    List<Double> values = grouped.getOrDefault(slot, List.of());
                    double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                    avg = Math.round(avg * 100) / 100.0;
                    return new TimeSlotPriceDetail(slot.name(), avg);
                })
                .collect(Collectors.toList());
        // 전체 평균 계산
        double overall = prices.stream()
                .mapToDouble(PricePerMwh::getPricePerMwh)
                .average()
                .orElse(0.0);
        overall = Math.round(overall * 100) / 100.0;

        return new PowerTradingPriceData(overall, details);
    }




    @Transactional
    public void reportTrouble(String stationName, Integer evseId){
        String stationId = chargingStationRepository.findStationIdByStationName(stationName);
        ChargerDomain chargerDomain = chargerRepository.findByStationIdAndEvseId(stationId, evseId);
        chargerDomain.updateChargerStatus(ChargerStatus.FAULT);
        chargerDomain.addTrouble();
        chargerRepository.updateTroubleAndStatus(chargerDomain);
    }
}