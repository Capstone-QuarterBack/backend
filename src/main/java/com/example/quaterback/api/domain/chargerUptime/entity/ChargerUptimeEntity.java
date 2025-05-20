package com.example.quaterback.api.domain.chargerUptime.entity;

import com.example.quaterback.api.domain.station.entity.ChargingStationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "charger_uptime")
public class ChargerUptimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double upTime;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_station")
    private ChargingStationEntity station;

    private String reason;
    /**
     * 신규 세그먼트 생성용 팩토리
     */
    public static ChargerUptimeEntity of(ChargingStationEntity station, double initialUpTime, LocalDateTime timestamp, String reason) {
        return ChargerUptimeEntity.builder()
                .station(station)
                .upTime(initialUpTime)
                .createdAt(timestamp)
                .reason(reason)
                .build();
    }

    /**
     * 기존 엔티티에 추가 가동률을 누적하고 업데이트 타임스탬프를 갱신
     */
    public void accumulate(double additionalPercent, LocalDateTime timestamp) {
        this.upTime += additionalPercent;
        this.createdAt = timestamp;
    }
    public void updateReason(String reason){
        this.reason = reason;
    }
}
