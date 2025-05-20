package com.example.quaterback.api.domain.charger.domain;

import com.example.quaterback.api.domain.charger.constant.ChargerStatus;
import com.example.quaterback.api.domain.charger.entity.ChargerEntity;
import com.example.quaterback.api.feature.overview.dto.response.CsAndChargerParams;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChargerDomain {
    private Long id;
    private Integer evseId;
    private ChargerStatus chargerStatus;
    private LocalDateTime updateStatusTimeStamp;
    private String stationId;
    private Integer troubleCnt;
    public void updateChargerStatus(ChargerStatus status) {
        if (!chargerStatus.equals(status)) {
            this.chargerStatus = status;
            this.updateStatusTimeStamp = LocalDateTime.now();
        }
    }

    public void addTrouble(){
        if(this.troubleCnt == null){
            troubleCnt = 1;
        }
        troubleCnt += 1;
    }
    public static ChargerDomain fromEntityToDomain(ChargerEntity entity){
        return ChargerDomain.builder()
                .id(entity.getId())
                .evseId(entity.getEvseId())
                .chargerStatus(entity.getChargerStatus())
                .updateStatusTimeStamp(entity.getUpdateStatusTimeStamp())
                .stationId(entity.getStation().getStationId())
                .build();
    }
    public static ChargerDomain fromRequestToDomain(Integer evseId, String stationId){
        return ChargerDomain.builder()
                .evseId(evseId)
                .stationId(stationId)
                .chargerStatus(ChargerStatus.UNAVAILABLE)
                .updateStatusTimeStamp(LocalDateTime.now())
                .build();
    }
}
