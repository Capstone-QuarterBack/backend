package com.example.quaterback.api.domain.txinfo.entity;

import com.example.quaterback.api.domain.charger.entity.ChargerEntity;
import com.example.quaterback.api.domain.station.entity.ChargingStationEntity;
import com.example.quaterback.api.domain.txinfo.domain.TransactionInfoDomain;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tx_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String transactionId;
    private LocalDateTime startedTime;
    private LocalDateTime endedTime;
    private String vehicleNo;
    private String idToken;
    private String stationId;

    @ManyToOne
    @JoinColumn(name = "evse_id")
    private ChargerEntity charger;
    private Integer totalMeterValue;
    private Integer totalPrice;

    public static TransactionInfoEntity fromTransactionInfoDomain(TransactionInfoDomain domain, ChargerEntity chargerEntity) {
        return TransactionInfoEntity.builder()
                .transactionId(domain.getTransactionId())
                .startedTime(domain.getStartedTime())
                .vehicleNo(domain.getVehicleNo())
                .idToken(domain.getIdToken())
                .charger(chargerEntity)
                .build();
    }

    public String updateEndTimeAndTotalValues(TransactionInfoDomain domain) {
        endedTime = domain.getEndedTime();
        totalMeterValue = domain.getTotalMeterValue();
        totalPrice = domain.getTotalPrice();
        return transactionId;
    }



    public TransactionInfoDomain toDomain() {
        return TransactionInfoDomain.builder()
                .transactionId(transactionId)
                .startedTime(startedTime)
                .endedTime(endedTime)
                .vehicleNo(vehicleNo)
                .totalMeterValue(totalMeterValue)
                .totalPrice(totalPrice)
                .build();
    }
}
