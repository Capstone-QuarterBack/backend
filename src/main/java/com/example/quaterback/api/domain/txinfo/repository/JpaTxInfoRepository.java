package com.example.quaterback.api.domain.txinfo.repository;

import com.example.quaterback.api.domain.charger.entity.ChargerEntity;
import com.example.quaterback.api.domain.charger.repository.SpringDataJpaChargerRepository;
import com.example.quaterback.api.domain.txinfo.domain.TransactionInfoDomain;
import com.example.quaterback.api.domain.txinfo.entity.TransactionInfoEntity;
import com.example.quaterback.api.feature.dashboard.dto.query.ChargerUsageQuery;
import com.example.quaterback.api.feature.dashboard.dto.query.DashboardSummaryQuery;
import com.example.quaterback.api.feature.monitoring.dto.query.ChargingRecordQuery;
import com.example.quaterback.api.feature.monitoring.dto.query.DailyUsageQuery;
import com.example.quaterback.api.feature.monitoring.dto.query.HourlyCongestionQuery;
import com.example.quaterback.api.feature.statistics.dto.query.DayNightMeterValueDto;
import com.example.quaterback.api.feature.statistics.dto.query.MonthlyTransactionStatistics;
import com.example.quaterback.api.feature.statistics.dto.request.ChartType;
import com.example.quaterback.api.feature.statistics.dto.query.StationTotalPriceDto;
import com.example.quaterback.api.feature.statistics.dto.response.StatisticsData;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDate;

@RequiredArgsConstructor
@Repository
public class JpaTxInfoRepository implements TxInfoRepository {

    private final SpringDataJpaTxInfoRepository springDataJpaTxInfoRepository;
    private final SpringDataJpaChargerRepository springDataJpaChargerRepository;

    @Override
    public String save(TransactionInfoDomain domain) {
        String stationId = domain.getStationId();
        Integer evseId = domain.getEvseId();

        ChargerEntity chargerEntity = springDataJpaChargerRepository.findByStation_StationIdAndEvseId(stationId, evseId)
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));

        TransactionInfoEntity entity = TransactionInfoEntity.fromTransactionInfoDomain(domain, chargerEntity);
        springDataJpaTxInfoRepository.save(entity);
        return entity.getTransactionId();
    }

    @Override
    public String updateEndTime(TransactionInfoDomain domain) {
        TransactionInfoEntity entity = springDataJpaTxInfoRepository.findByTransactionId(domain.getTransactionId())
                .orElseThrow(() -> new EntityNotFoundException("tx info entity not found"));

        if (domain.getEndedTime() != null && domain.getTotalMeterValue() != null && domain.getTotalPrice() != null) {
            String returnTxId = entity.updateEndTimeAndTotalValues(domain);
            return returnTxId;
        }
        return null;
    }

    @Override
    public List<Object[]> findTotalDischargePerHour() {
        return springDataJpaTxInfoRepository.findTotalDischargePerHour();
    }

    @Override
    public DashboardSummaryQuery findDashboardSummary() {
        return springDataJpaTxInfoRepository.findDashboardSummary();
    }

    @Override
    public Page<ChargerUsageQuery> findWithStationInfo(Pageable pageable) {
        return springDataJpaTxInfoRepository.findWithStationInfo(pageable);
    }

    @Override
    public Page<ChargingRecordQuery> findChargerUsageByStationId(String stationId, Pageable pageable) {
        return springDataJpaTxInfoRepository.findChargerUsageByStationId(stationId, pageable);
    }

    @Override
    public List<HourlyCongestionQuery> findHourlyCountsByStationId(String stationId) {
        return springDataJpaTxInfoRepository.findHourlyCountsByStationId(stationId);
    }

    @Override
    public Page<TransactionInfoDomain> findAllByEvseId(String stationId, Integer evseId, Pageable pageable) {
        ChargerEntity chargerEntity = springDataJpaChargerRepository.findByStation_StationIdAndEvseId(stationId, evseId)
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));

        Page<TransactionInfoEntity> entityPage = springDataJpaTxInfoRepository.findAllByEvseId(stationId, chargerEntity.getId(), pageable);

        return entityPage.map(TransactionInfoEntity::toDomain);
    }

    @Override
    public DailyUsageQuery findDailyUsageByEvseIdAndDate(String stationId, Integer evseId, LocalDate date) {
        ChargerEntity chargerEntity = springDataJpaChargerRepository.findByStation_StationIdAndEvseId(stationId, evseId)
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));

        return springDataJpaTxInfoRepository.findDailyUsageByEvseIdAndDate(stationId, chargerEntity.getId(), date)
                .orElseThrow(() -> new EntityNotFoundException("tx info entity not found"));
    }

    @Override
    public Page<TransactionInfoDomain> findByIdTokenOrderByStartedTimeDesc(String idToken, Pageable pageable) {
        Page<TransactionInfoEntity> entityPage = springDataJpaTxInfoRepository.findByIdTokenOrderByStartedTimeDesc(idToken, pageable);
        return entityPage.map(TransactionInfoEntity::toDomain);
    }

    @Override
    public Page<TransactionInfoDomain> findByIdTokenAndStartedTimeBetweenOrderByStartedTimeDesc(String idToken, LocalDate startTime, LocalDate endTime, Pageable pageable) {
        Page<TransactionInfoEntity> entityPage = springDataJpaTxInfoRepository.findByIdTokenAndStartedTimeBetweenOrderByStartedTimeDesc(idToken, startTime, endTime, pageable);
        return entityPage.map(TransactionInfoEntity::toDomain);
    }


    @Override
    public List<TransactionInfoDomain> findByChargerPkAndCreatedAtBetween(TransactionInfoDomain domain,
                                                                          Long chargerPk) {
        LocalDateTime start = domain.getStartedTime();
        LocalDateTime end = domain.getEndedTime();

        List<TransactionInfoEntity> txEntities = springDataJpaTxInfoRepository.findByChargerPkAndTimeRange(
                chargerPk
                ,start
                ,end);
        List<TransactionInfoDomain> txDomains = txEntities.stream()
                .map(TransactionInfoDomain::fromTxEntityDomain)
                .collect(Collectors.toList());
        return txDomains;
    }

    @Override
    public Page<TransactionInfoDomain> findByStationIdAndCreatedAtBetween(TransactionInfoDomain domain,
                                                                          String stationId,
                                                                          Pageable pageable) {
        LocalDateTime start = domain.getStartedTime();
        LocalDateTime end = domain.getEndedTime();

        Page<TransactionInfoEntity> txInfoEntities =  springDataJpaTxInfoRepository.findByStationIdAndPeriod(
                stationId, start, end, pageable
        );

        Page<TransactionInfoDomain> txInfoDomains = txInfoEntities
                .map(entity -> TransactionInfoDomain.fromTxEntityDomain(entity));
        return txInfoDomains;
    }

    @Override
    public TransactionInfoDomain getOneTxInfoByTxId(TransactionInfoDomain txInfoDomain) {
        TransactionInfoEntity txEntity = springDataJpaTxInfoRepository.findByTransactionId(txInfoDomain.getTransactionId())
                .orElseThrow(() -> new EntityNotFoundException("tx info entity not found"));
        return TransactionInfoDomain.fromTxEntityDomain(txEntity);
    }

    @Override
    public TransactionInfoDomain findByTxId(String txId) {
        TransactionInfoEntity txEntity = springDataJpaTxInfoRepository.findByTransactionId(txId)
                .orElseThrow(() -> new EntityNotFoundException("tx info entity not found"));
        return TransactionInfoDomain.fromTxEntityDomain(txEntity);
    }

    @Override
    public List<TransactionInfoDomain> findNotEnded(String stationId) {
        return springDataJpaTxInfoRepository.findNotEndedTxInfos(stationId)
                .stream().map(TransactionInfoEntity::toDomain).toList();
    }

    @Override
    public MonthlyTransactionStatistics getMonthlyStatisticsByYearAndMonth(int year, int month) {
        return springDataJpaTxInfoRepository.getMonthlyStatistics(year, month);
    }

    @Override
    public List<StatisticsData.ChartData> findDailyRevenueLast7DaysRaw() {
        return springDataJpaTxInfoRepository.findDailyRevenueLast7DaysRaw()
                .stream()
                .map(row -> StatisticsData.ChartData.builder()
                        .label(row[0].toString())
                        .value(((Number) row[1]).doubleValue())
                        .build())
                .toList();
    }

    @Override
    public List<StatisticsData.ChartData> findDailyUsageLast7DayRaw() {
        return springDataJpaTxInfoRepository.findDailyUsageLast7DaysRaw()
                .stream()
                .map(row -> StatisticsData.ChartData.builder()
                        .label(row[0].toString())
                        .value(((Number) row[1]).doubleValue())
                        .build())
                .toList();
    }

    @Override
    public List<StatisticsData.ChartData> countChargingSpeedByMonth(int year, int month) {
        List<Object[]> resultsList = springDataJpaTxInfoRepository.countChargingSpeedByMonth(year, month);
        Object[] results = resultsList.get(0);
        double rapid = ((Number) results[0]).doubleValue();
        double slow = ((Number) results[1]).doubleValue();
        double rapidPercent = rapid / (rapid + slow) * 100;
        double slowPercent = 100 - rapidPercent;
        return List.of(
                StatisticsData.ChartData.builder()
                        .id("급속")
                        .label("급속")
                        .value(rapidPercent)
                        .build(),
                StatisticsData.ChartData.builder()
                        .id("완속")
                        .label("완속")
                        .value(slowPercent)
                        .build()
        );

    }

    @Override
    public List<StatisticsData.ChartData> findDailyTxCount(ChartType chartType) {

         return springDataJpaTxInfoRepository.findDailyTxCount()
                 .stream()
                 .map(row -> StatisticsData.ChartData.builder()
                         .label(row[0].toString())
                         .value(((Number) row[1]).doubleValue())
                         .build())
                 .toList();
    }

    @Override
    public List<StatisticsData.ChartData> findTotalPriceGroupedByStationId() {
        List<StationTotalPriceDto> list = springDataJpaTxInfoRepository.findTotalPriceGroupedByStationId();
        return list.stream()
                .map(dto -> StatisticsData.ChartData.builder()
                        .label(dto.getStationId())
                        .value(dto.getTotalPrice() != null ? dto.getTotalPrice() : 0.0)
                        .build())
                .toList();
    }

    @Override
    public List<StatisticsData.ChartData> findMeterValueGroupedByTimeType() {
        List<DayNightMeterValueDto> list = springDataJpaTxInfoRepository.findMeterValueGroupedByTimeType();
        double dayTotal = 0.0;
        double nightTotal = 0.0;
        for (DayNightMeterValueDto dto : list) {
            if ("DAY".equals(dto.getTimeType())) {
                dayTotal = dto.getTotalMeterValue() != null ? dto.getTotalMeterValue() : 0.0;
            }
            else if ("NIGHT".equals(dto.getTimeType())) {
                nightTotal = dto.getTotalMeterValue() != null ? dto.getTotalMeterValue() : 0.0;
            }
        }
        return List.of(
                StatisticsData.ChartData.builder()
                        .label("주간").value(dayTotal)
                        .build(),
                StatisticsData.ChartData.builder()
                        .label("야간").value(nightTotal)
                        .build()
        );
    }

    @Override
    public List<TransactionInfoEntity> findTxInfoByTerm(LocalDateTime startTime, LocalDateTime endTime) {
        return springDataJpaTxInfoRepository.findByEndedTimeBetween(startTime, endTime);
    }
}
