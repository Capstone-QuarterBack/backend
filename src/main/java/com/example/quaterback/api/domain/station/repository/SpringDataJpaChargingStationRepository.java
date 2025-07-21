package com.example.quaterback.api.domain.station.repository;

import com.example.quaterback.api.domain.charger.entity.ChargerEntity;
import com.example.quaterback.api.domain.station.entity.ChargingStationEntity;
import com.example.quaterback.api.feature.dashboard.dto.query.StationFullInfoQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataJpaChargingStationRepository extends JpaRepository<ChargingStationEntity, Long> {
    Optional<ChargingStationEntity> findByStationId(String stationId);

    Optional<ChargingStationEntity> findByStationName(String stationName);

    @Query("""
    SELECT new com.example.quaterback.api.feature.dashboard.dto.query.StationFullInfoQuery(
        cs.stationId,
        cs.model,
        cs.address,
        cs.stationStatus,
        cs.updateStatusTimeStamp,
        COUNT(c.id),
        SUM(CASE WHEN c.chargerStatus = 'AVAILABLE' THEN 1 ELSE 0 END),
        SUM(CASE WHEN c.chargerStatus = 'OCCUPIED' THEN 1 ELSE 0 END),
        SUM(CASE WHEN c.chargerStatus = 'UNAVAILABLE' THEN 1 ELSE 0 END)
    )
    FROM ChargingStationEntity cs
    JOIN ChargerEntity c ON cs.stationId = c.station.stationId
    GROUP BY cs.stationId
""")
    List<StationFullInfoQuery> getFullStationInfos();

    @Query("""
    SELECT new com.example.quaterback.api.feature.dashboard.dto.query.StationFullInfoQuery(
        cs.stationId,
        cs.model,
        cs.address,
        cs.stationStatus,
        cs.updateStatusTimeStamp,
        COUNT(c.id),
        SUM(CASE WHEN c.chargerStatus = 'AVAILABLE' THEN 1 ELSE 0 END),
        SUM(CASE WHEN c.chargerStatus = 'OCCUPIED' THEN 1 ELSE 0 END),
        SUM(CASE WHEN c.chargerStatus = 'UNAVAILABLE' THEN 1 ELSE 0 END)
    )
    FROM ChargingStationEntity cs
    JOIN ChargerEntity c ON cs.stationId = c.station.stationId
    WHERE cs.stationName = :stationName
    GROUP BY cs.stationId
""")
    Optional<StationFullInfoQuery> getFullStationInfo(@Param("stationName") String stationName);

    @Modifying
    @Query("delete from ChargingStationEntity cs where cs.stationName = :stationName")
    int deleteByName(@Param("stationName")String stationName);

    @Query("select t from ChargingStationEntity t join fetch t.chargerEntityList")
    List<ChargingStationEntity> findAllWithFetchJoin();

}
