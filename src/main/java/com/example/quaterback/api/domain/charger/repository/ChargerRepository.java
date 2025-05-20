package com.example.quaterback.api.domain.charger.repository;

import com.example.quaterback.api.domain.charger.constant.ChargerStatus;
import com.example.quaterback.api.domain.charger.domain.ChargerDomain;
import com.example.quaterback.api.domain.charger.entity.ChargerEntity;
import com.example.quaterback.api.domain.station.constant.StationStatus;
import com.example.quaterback.api.feature.statistics.dto.response.StatisticsData;

import java.util.List;


public interface ChargerRepository {

    ChargerDomain findByStationIdAndEvseId(String stationId, Integer evseId);
    Integer update(ChargerDomain domain);
    List<ChargerDomain> findAllByStationId(String stationId);
    List<ChargerDomain> findByStationID(String stationId);
    void save(ChargerDomain chargerDomain);
    List<ChargerEntity> findAllCharger();
    void updateTroubleAndStatus(ChargerDomain domain);
    List<StatisticsData.ChartData> countFaultAndNormalChargers();

    ChargerDomain updateChargerStatus(String stationId, Integer evseId, ChargerStatus status);
}
