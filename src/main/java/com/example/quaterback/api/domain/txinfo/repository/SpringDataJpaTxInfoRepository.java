package com.example.quaterback.api.domain.txinfo.repository;

import com.example.quaterback.api.domain.txinfo.entity.TransactionInfoEntity;
import com.example.quaterback.api.feature.statistics.dto.query.DayNightMeterValueDto;
import com.example.quaterback.api.feature.statistics.dto.query.MonthlyTransactionStatistics;
import com.example.quaterback.api.feature.statistics.dto.response.StatisticsData;
import com.example.quaterback.api.feature.statistics.dto.query.StationTotalPriceDto;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.quaterback.api.feature.dashboard.dto.query.ChargerUsageQuery;
import com.example.quaterback.api.feature.dashboard.dto.query.DashboardSummaryQuery;
import com.example.quaterback.api.feature.monitoring.dto.query.ChargingRecordQuery;
import com.example.quaterback.api.feature.monitoring.dto.query.DailyUsageQuery;
import com.example.quaterback.api.feature.monitoring.dto.query.HourlyCongestionQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

public interface SpringDataJpaTxInfoRepository extends JpaRepository<TransactionInfoEntity, Long> {
    Optional<TransactionInfoEntity> findByTransactionId(String transactionId);
    Page<TransactionInfoEntity> findByIdTokenOrderByStartedTimeDesc(String idToken, Pageable pageable);

    @Query("select t from TransactionInfoEntity t " +
           "where t.idToken = :idToken " +
           "and date(t.startedTime) between :start and :end " +
           "order by t.startedTime desc")
    Page<TransactionInfoEntity> findByIdTokenAndStartedTimeBetweenOrderByStartedTimeDesc(
            @Param("idToken") String idToken,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable);

    @Query("""
    SELECT FUNCTION('HOUR', t.startedTime), SUM(t.totalMeterValue)
    FROM TransactionInfoEntity t
    WHERE t.totalMeterValue IS NOT NULL
    GROUP BY FUNCTION('HOUR', t.startedTime)
    ORDER BY FUNCTION('HOUR', t.startedTime)
""")
    List<Object[]> findTotalDischargePerHour();

    @Query("""
    SELECT new com.example.quaterback.api.feature.dashboard.dto.query.DashboardSummaryQuery(
        COUNT(t), COALESCE(SUM(t.totalPrice), 0), COALESCE(SUM(t.totalMeterValue), 0)
    )
    FROM TransactionInfoEntity t
""")
    DashboardSummaryQuery findDashboardSummary();

    @Query("""
    SELECT new com.example.quaterback.api.feature.dashboard.dto.query.ChargerUsageQuery(
        t.startedTime,
        cs.address,
        cs.model,
        t.totalMeterValue,
        t.totalPrice,
        t.transactionId
    )
    FROM TransactionInfoEntity t
    JOIN ChargingStationEntity cs ON t.stationId = cs.stationId
    ORDER BY t.startedTime DESC
""")
    Page<ChargerUsageQuery> findWithStationInfo(Pageable pageable);

    @Query("""
    SELECT new com.example.quaterback.api.feature.monitoring.dto.query.ChargingRecordQuery(
        t.startedTime,
        t.endedTime,
        t.totalPrice,
        t.transactionId
    )
    FROM TransactionInfoEntity t
    WHERE t.stationId = :stationId
""")
    Page<ChargingRecordQuery> findChargerUsageByStationId(
            @Param("stationId") String stationId,
            Pageable pageable
    );

    @Query("""
        SELECT new com.example.quaterback.api.feature.monitoring.dto.query.HourlyCongestionQuery(
            HOUR(t.startedTime), COUNT(t)
        )
        FROM TransactionInfoEntity t
        WHERE t.stationId = :stationId 
        GROUP BY HOUR(t.startedTime)
        ORDER BY HOUR(t.startedTime)
    """)
    List<HourlyCongestionQuery> findHourlyCountsByStationId(@Param("stationId") String stationId);

    @Query("select t from TransactionInfoEntity t where t.evseId.id=:evseId and t.stationId =:stationId")
    Page<TransactionInfoEntity> findAllByEvseId(@Param("stationId")String stationId, @Param("evseId") Long evseId, Pageable pageable);

    @Query("""
    SELECT new com.example.quaterback.api.feature.monitoring.dto.query.DailyUsageQuery(
        SUM(t.totalMeterValue),         
        COUNT(t),                       
        SUM(t.totalPrice)              
    )
    FROM TransactionInfoEntity t
    WHERE t.evseId.id = :evseId and t.stationId = :stationId
      AND FUNCTION('DATE', t.startedTime) = :date
""")
    Optional<DailyUsageQuery> findDailyUsageByEvseIdAndDate(
            @Param("stationId") String stationId,
            @Param("evseId") Long evseId,
            @Param("date") LocalDate date
    );

    @Query("""
    SELECT t FROM TransactionInfoEntity t
    WHERE t.evseId.id = :evseId
      AND (
        (t.startedTime BETWEEN :from AND :to)
        OR (t.endedTime BETWEEN :from AND :to)
        OR (t.startedTime <= :from AND t.endedTime >= :to)
      )
""")
    List<TransactionInfoEntity> findByChargerPkAndTimeRange(
            @Param("evseId") Long chargerPk,
            @Param("from") LocalDateTime start,
            @Param("to") LocalDateTime end);

    @Query("""
        SELECT t FROM TransactionInfoEntity t
        WHERE t.evseId.station.stationId = :stationId
        AND t.startedTime BETWEEN :start AND :end
    """)
    Page<TransactionInfoEntity> findByStationIdAndPeriod(
            @Param("stationId") String stationId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    Optional<TransactionInfoEntity> findFirstByOrderByTransactionIdDesc();

    @Query("""
        SELECT t FROM TransactionInfoEntity t
        WHERE t.stationId = :stationId
        AND t.totalMeterValue IS NULL
    """)
    List<TransactionInfoEntity> findNotEndedTxInfos(String stationId);

    @Query("""
    SELECT t FROM TransactionInfoEntity t
    WHERE t.endedTime BETWEEN :startTime AND :endTime
""")
    List<TransactionInfoEntity> findByEndedTimeBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );


    @Query(value = """
    SELECT
        COALESCE(SUM(t.total_meter_value), 0) AS totalChargingVolume,
        COUNT(t.id) AS totalChargingCount,
        COALESCE(SUM(t.total_price), 0) AS totalRevenue,
        COALESCE(AVG(TIMESTAMPDIFF(SECOND, t.started_time, t.ended_time)), 0) AS averageChargingTime
        FROM tx_info t
        WHERE MONTH(t.started_time) = :month AND YEAR(t.started_time) = :year
    """, nativeQuery = true)
    MonthlyTransactionStatistics getMonthlyStatistics(@Param("year") int year, @Param("month") int month);

    @Query(value = """
            SELECT 
                DATE(t.ended_time) AS `date`,
                SUM(t.total_price) AS dailyRevenue
            FROM tx_info t
            WHERE t.ended_time < NOW()
            GROUP BY DATE(t.ended_time)
            ORDER BY DATE(t.ended_time)
            """, nativeQuery = true)
    List<Object[]> findDailyRevenueLast7DaysRaw();

    @Query(value = """
    SELECT 
        DATE(t.ended_time) AS `date`,
        SUM(t.total_meter_value) AS dailyUsage
    FROM tx_info t
    WHERE t.ended_time < NOW()
    GROUP BY DATE(t.ended_time)
    ORDER BY DATE(t.ended_time)
    """, nativeQuery = true)
    List<Object[]> findDailyUsageLast7DaysRaw();


    @Query(value = """
    SELECT 
        SUM(CASE WHEN TIMESTAMPDIFF(SECOND, t.started_time, t.ended_time) < 20 THEN 1 ELSE 0 END) AS rapidCount,
        SUM(CASE WHEN TIMESTAMPDIFF(SECOND, t.started_time, t.ended_time) >= 20 THEN 1 ELSE 0 END) AS slowCount
    FROM tx_info t
    WHERE MONTH(t.started_time) = :month AND YEAR(t.started_time) = :year
    """, nativeQuery = true)
    List<Object[]> countChargingSpeedByMonth(@Param("year") int year, @Param("month") int month);

    @Query(value = """
    SELECT 
        DATE(ended_time) AS date,
        COUNT(*) AS tx_count
    FROM tx_info
    WHERE ended_time < NOW()
    GROUP BY DATE(ended_time)
    ORDER BY DATE(ended_time) DESC
    """, nativeQuery = true)
    List<Object[]> findDailyTxCount();

    @Query("SELECT t.stationId AS stationId, SUM(t.totalPrice) AS totalPrice " +
            "FROM TransactionInfoEntity t " +
            "GROUP BY t.stationId")
    List<StationTotalPriceDto> findTotalPriceGroupedByStationId();

    @Query(value = """
        SELECT 
            CASE 
                WHEN HOUR(started_time) >= 6 AND HOUR(started_time) < 18 THEN 'DAY'
                ELSE 'NIGHT'
            END AS timeType,
            SUM(total_meter_value) AS totalMeterValue
        FROM tx_info
        GROUP BY timeType
        """, nativeQuery = true)
    List<DayNightMeterValueDto> findMeterValueGroupedByTimeType();
}
