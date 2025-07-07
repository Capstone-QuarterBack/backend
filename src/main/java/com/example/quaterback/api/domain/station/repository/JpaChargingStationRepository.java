package com.example.quaterback.api.domain.station.repository;

import com.example.quaterback.api.domain.station.constant.StationStatus;
import com.example.quaterback.api.domain.station.domain.ChargingStationDomain;
import com.example.quaterback.api.domain.station.entity.ChargingStationEntity;
import com.example.quaterback.api.feature.dashboard.dto.query.StationFullInfoQuery;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JpaChargingStationRepository implements ChargingStationRepository {

    private final SpringDataJpaChargingStationRepository chargingStationRepository;

    @Override
    public ChargingStationDomain findByStationId(String stationId) {
        ChargingStationEntity entity = chargingStationRepository.findByStationId(stationId)
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));

        return entity.toDomain();
    }

    public String update(ChargingStationDomain domain) {
        ChargingStationEntity entity = chargingStationRepository.findByStationId(domain.getStationId())
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));

        entity.updateStationStatus(domain.getStationStatus());

        return entity.getStationId();
    }

    @Override
    public String updateEss(ChargingStationDomain domain) {
        ChargingStationEntity entity = chargingStationRepository.findByStationId(domain.getStationId())
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));

        entity.updateStationEssValue(domain.getEssValue());

        return entity.getStationId();
    }

    @Override
    public List<StationFullInfoQuery> getFullStationInfos() {
        return chargingStationRepository.getFullStationInfos();
    }

    @Override
    public StationFullInfoQuery getFullStationInfo(String stationName) {
        log.info("stationName : {}", stationName);
        return chargingStationRepository.getFullStationInfo(stationName)
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));
    }

    @Override
    public int deleteByName(String stationName) {
        int row = chargingStationRepository.deleteByName(stationName);

        if(row== 0){
            throw new EntityNotFoundException("삭제 대상이 없습니다: " + stationName);
        }
        return row;
    }

    @Override
    public List<ChargingStationDomain> findAll() {
        List<ChargingStationEntity> stationEntitys = chargingStationRepository.findAll();
        //List<ChargingStationEntity> stationEntitys = chargingStationRepository.findAllWithFetchJoin();

        return stationEntitys.stream()
                .map(ChargingStationEntity::toDomain)
                .toList();
    }

    @Override
    public ChargingStationDomain findByStationName(String stationName) {
        ChargingStationEntity stationEntity = chargingStationRepository.findByStationName(stationName)
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));
        return stationEntity.toDomain();
    }

    @Override
    public void save(ChargingStationDomain chargingStationDomain) {
        ChargingStationEntity csEntity = ChargingStationEntity.fromCsDomain(chargingStationDomain);
        chargingStationRepository.save(csEntity);
    }

    @Override
    @Transactional
    public String updateStationStatus(String stationId, StationStatus status) {
        ChargingStationEntity entity = chargingStationRepository.findByStationId(stationId)
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));

        entity.updateStationStatus(status);
        ChargingStationEntity savedEntity = chargingStationRepository.save(entity);
        return savedEntity.getStationId();
    }

    @Override
    public String findStationIdByStationName(String stationName) {
        ChargingStationEntity entity = chargingStationRepository.findByStationName(stationName)
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));
        return entity.getStationId();
    }


}
