package com.example.quaterback.api.domain.txinfo.repository;

import com.example.quaterback.api.domain.txinfo.domain.TransactionInfoDomain;
import com.example.quaterback.api.domain.txinfo.entity.TransactionInfoEntity;
import com.example.quaterback.api.feature.statistics.dto.query.MonthlyTransactionStatistics;
import com.example.quaterback.api.feature.statistics.dto.request.ChartType;
import com.example.quaterback.api.feature.statistics.dto.response.StatisticsData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import com.example.quaterback.api.feature.dashboard.dto.query.ChargerUsageQuery;
import com.example.quaterback.api.feature.dashboard.dto.query.DashboardSummaryQuery;
import com.example.quaterback.api.feature.monitoring.dto.query.ChargingRecordQuery;
import com.example.quaterback.api.feature.monitoring.dto.query.DailyUsageQuery;
import com.example.quaterback.api.feature.monitoring.dto.query.HourlyCongestionQuery;

import java.time.LocalDateTime;
import java.util.List;

public interface TxInfoRepository {
    String save(TransactionInfoDomain domain);

    String updateEndTime(TransactionInfoDomain domain);

    List<TransactionInfoDomain> findByChargerPkAndCreatedAtBetween(TransactionInfoDomain domain,
                                                                   Long chargerPk);

    Page<TransactionInfoDomain> findByStationIdAndCreatedAtBetween(TransactionInfoDomain domain,
                                                                   String stationId,
                                                                   Pageable pageable);

    TransactionInfoDomain getOneTxInfoByTxId(TransactionInfoDomain txInfoDomain);

    Page<TransactionInfoDomain> findByIdTokenOrderByStartedTimeDesc(String idToken, Pageable pageable);
    Page<TransactionInfoDomain> findByIdTokenAndStartedTimeBetweenOrderByStartedTimeDesc(String idToken, LocalDate startTime, LocalDate endTime, Pageable pageable);

    List<Object[]> findTotalDischargePerHour();

    DashboardSummaryQuery findDashboardSummary();

    Page<ChargerUsageQuery> findWithStationInfo(Pageable pageable);

    Page<ChargingRecordQuery> findChargerUsageByStationId(String stationId, Pageable pageable);

    List<HourlyCongestionQuery> findHourlyCountsByStationId(String stationId);

    Page<TransactionInfoDomain> findAllByEvseId(String stationId, Integer evseId, Pageable pageable);

   DailyUsageQuery findDailyUsageByEvseIdAndDate(String stationId, Integer evseId, LocalDate date);

    TransactionInfoDomain findByTxId(String txId);

    List<TransactionInfoDomain> findNotEnded(String stationId);

    MonthlyTransactionStatistics getMonthlyStatisticsByYearAndMonth(int year, int month);

    List<StatisticsData.ChartData> findDailyRevenueLast7DaysRaw();

    List<StatisticsData.ChartData> findDailyUsageLast7DayRaw();

    List<StatisticsData.ChartData> countChargingSpeedByMonth();

    List<TransactionInfoEntity> findTxInfoByTerm(LocalDateTime startTime, LocalDateTime endTime);

    List<StatisticsData.ChartData> findDailyTxCount(ChartType chartType);

    List<StatisticsData.ChartData> findTotalPriceGroupedByStationId();

    List<StatisticsData.ChartData> findMeterValueGroupedByTimeType();
}
