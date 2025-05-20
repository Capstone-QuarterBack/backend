package com.example.quaterback.api.domain.charger.entity;

import com.example.quaterback.api.domain.charger.constant.ChargerStatus;
import com.example.quaterback.api.domain.charger.domain.ChargerDomain;
import com.example.quaterback.api.domain.station.entity.ChargingStationEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "charger_info")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer evseId;

    @Enumerated(EnumType.STRING)
    private ChargerStatus chargerStatus;

    private LocalDateTime updateStatusTimeStamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", referencedColumnName = "stationId")
    private ChargingStationEntity station;

    private Integer troubleCnt;

/***연관 관계 메서드 작성 필요!!!!***/


    public ChargerDomain toDomain() {
        return ChargerDomain.builder()
                .stationId(station.getStationId())
                .evseId(evseId)
                .chargerStatus(chargerStatus)
                .updateStatusTimeStamp(updateStatusTimeStamp)
                .troubleCnt(troubleCnt)
                .build();
    }

    public void updateChargerStatus(ChargerStatus status) {
        if (!status.equals(chargerStatus)) {
            chargerStatus = status;
            updateStatusTimeStamp = LocalDateTime.now();
        }
    }
    public void addTrouble(){
        if(this.troubleCnt == null){
            troubleCnt = 1;
        }
        else {
            troubleCnt += 1;
        }
    }
    public void assignStation(ChargingStationEntity stationEntity) {
        station = stationEntity;
        if (!stationEntity.getChargerEntityList().contains(this)) {
            stationEntity.getChargerEntityList().add(this);
        }
    }

    public static ChargerEntity fromChargerDomainToEntity(ChargerDomain chargerDomain, ChargingStationEntity csEntity){
        return ChargerEntity.builder()
                .chargerStatus(chargerDomain.getChargerStatus())
                .evseId(chargerDomain.getEvseId())
                .updateStatusTimeStamp(chargerDomain.getUpdateStatusTimeStamp())
                .station(csEntity)
                .build();

    }
}
