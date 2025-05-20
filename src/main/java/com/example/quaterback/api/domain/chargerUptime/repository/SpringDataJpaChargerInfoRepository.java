package com.example.quaterback.api.domain.chargerUptime.repository;

import com.example.quaterback.api.domain.chargerUptime.entity.ChargerUptimeEntity;
import com.example.quaterback.api.domain.station.entity.ChargingStationEntity;
import com.example.quaterback.api.feature.statistics.dto.query.DayOperatingDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataJpaChargerInfoRepository extends JpaRepository<ChargerUptimeEntity, Long> {

    @Query("SELECT c FROM ChargerUptimeEntity c WHERE c.createdAt BETWEEN :start AND :end")
    List<ChargerUptimeEntity> findByTimeRange(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    @Query("""
      SELECT e
      FROM ChargerUptimeEntity e
      WHERE e.station = :station
        AND e.createdAt BETWEEN :dayStart AND :dayEnd
    """)
    Optional<ChargerUptimeEntity> findByStationAndCreatedAtBetween(
            @Param("station") ChargingStationEntity station,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd")   LocalDateTime dayEnd
    );

    @Query("""
    SELECT DATE(cu.createdAt) AS timeType, SUM(cu.upTime) AS totalUpTime
    FROM ChargerUptimeEntity cu
    GROUP BY DATE(cu.createdAt)
    ORDER BY DATE(cu.createdAt)
""")
    List<DayOperatingDto> findOperatingRate();
}

