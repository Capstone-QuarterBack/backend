package com.example.quaterback.api.domain.station.domain;

import com.example.quaterback.api.domain.charger.domain.ChargerDomain;
import com.example.quaterback.api.domain.station.constant.StationStatus;
import com.example.quaterback.api.feature.overview.dto.response.CsAndChargerParams;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChargingStationDomain {
    private String stationId;
    private String stationName;
    private String model;
    private String vendorId;
    private Double latitude;
    private Double longitude;
    private String address;
    private LocalDateTime updateStatusTimeStamp;
    private StationStatus stationStatus;
    private Double essValue;
    private List<ChargerDomain> chargers;

    public void updateStationStatus(StationStatus status) {
        if (!this.stationStatus.equals(status)) {
            this.stationStatus = status;
            this.updateStatusTimeStamp = LocalDateTime.now();
        }
    }

    public void updateStationEssValue(Double essValue){
        essValue = Math.round(essValue * 100.0) / 100.0;
        this.essValue = essValue;
    }

    public static ChargingStationDomain fromRequestToCsDomain(CsAndChargerParams request){
        return  ChargingStationDomain.builder()
                .stationId(request.getStationId())
                .stationName(request.getStationName())
                .model(request.getModel())
                .vendorId(request.getVendorId())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .updateStatusTimeStamp(LocalDateTime.now())
                .stationStatus(StationStatus.INACTIVE)
                .build();
    }
}
