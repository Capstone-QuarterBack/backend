package com.example.quaterback.api.domain.charger.repository;

import com.example.quaterback.api.domain.charger.constant.ChargerStatus;
import com.example.quaterback.api.domain.charger.entity.ChargerEntity;
import com.example.quaterback.api.domain.charger.domain.ChargerDomain;
import com.example.quaterback.api.domain.station.constant.StationStatus;
import com.example.quaterback.api.domain.station.entity.ChargingStationEntity;
import com.example.quaterback.api.domain.station.repository.ChargingStationRepository;
import com.example.quaterback.api.domain.station.repository.SpringDataJpaChargingStationRepository;
import com.example.quaterback.api.feature.statistics.dto.response.StatisticsData;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Transient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;



@Repository
@RequiredArgsConstructor
public class JpaChargerRepository implements ChargerRepository {
    private final SpringDataJpaChargingStationRepository springDataJpaChargingStationRepository;
    private final SpringDataJpaChargerRepository chargerRepository;
    private final SpringDataJpaChargerRepository springDataJpaChargerRepository;

    @Override
    public ChargerDomain findByStationIdAndEvseId(String stationId, Integer evseId) {
        ChargerEntity entity = chargerRepository.findByStation_StationIdAndEvseId(stationId, evseId)
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));

        return entity.toDomain();
    }

    @Override
    public Integer update(ChargerDomain domain) {
        ChargerEntity entity = chargerRepository.findByStation_StationIdAndEvseId(domain.getStationId(), domain.getEvseId())
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));

        entity.updateChargerStatus(domain.getChargerStatus());

        return entity.getEvseId();
    }

    @Override
    public List<ChargerDomain> findByStationID(String stationId) {
        List<ChargerEntity> chargerEntities = chargerRepository.findByStation_StationId(stationId);
        List<ChargerDomain> chargerDomains = chargerEntities.stream()
                .map(ChargerDomain :: fromEntityToDomain)
                .collect(Collectors.toList());
        return chargerDomains;
    }

    @Override
    public void save(ChargerDomain chargerDomain) {
        String stationId = chargerDomain.getStationId();
        ChargingStationEntity chargingStationEntity =  springDataJpaChargingStationRepository.findByStationId(stationId)
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));
        ChargerEntity chargerEntity = ChargerEntity.fromChargerDomainToEntity(chargerDomain, chargingStationEntity);
        chargerRepository.save(chargerEntity);
    }

    @Override
    public List<ChargerEntity> findAllCharger() {
        return chargerRepository.findAll();
    }

    @Override
    public void updateTroubleAndStatus(ChargerDomain domain) {
        ChargerEntity entity = chargerRepository.findByStation_StationIdAndEvseId(domain.getStationId(), domain.getEvseId())
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));

        entity.updateChargerStatus(domain.getChargerStatus());
        entity.addTrouble();
    }

    @Override
    public List<ChargerDomain> findAllByStationId(String stationId) {
        List<ChargerEntity> entityList = chargerRepository.findAllByStationId(stationId);

        return entityList.stream()
                .map(ChargerEntity::toDomain)
                .toList();
    }

    @Override
    public List<StatisticsData.ChartData> countFaultAndNormalChargers() {
        List<Object[]> resultList = chargerRepository.countFaultAndNormalChargers();
        Object[] results = resultList.get(0);
        double normalChargers = ((Number) results[0]).doubleValue();
        double faultChargers = ((Number) results[1]).doubleValue();
        return List.of(
                StatisticsData.ChartData.builder()
                        .label("정상")
                        .value(normalChargers)
                        .build(),
                StatisticsData.ChartData.builder()
                        .label("고장")
                        .value(faultChargers)
                        .build()
        );
    }

    @Override
    @Transactional
    public ChargerDomain updateChargerStatus(String stationId, Integer evseId, ChargerStatus status) {
        ChargerEntity entity = springDataJpaChargerRepository.findOneCharger(stationId, evseId)
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));

        entity.updateChargerStatus(status);
        ChargerEntity updatedEntity = springDataJpaChargerRepository.save(entity);
        return updatedEntity.toDomain();
    }
}
