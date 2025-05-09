package com.example.quaterback.api.domain.charger.repository;

import com.example.quaterback.api.domain.charger.domain.ChargerDomain;

import java.util.List;

public interface ChargerRepository {

    ChargerDomain findByStationIdAndEvseId(String stationId, Integer evseId);
    Integer update(ChargerDomain domain);
    List<ChargerDomain> findAllByStationId(String stationId);


}
