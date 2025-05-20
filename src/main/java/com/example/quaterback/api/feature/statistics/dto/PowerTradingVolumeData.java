package com.example.quaterback.api.feature.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PowerTradingVolumeData {
    private double netVolume; // 총 사용량 합
    private String percentText;
    private double minVolume; // 가장 적게 사용한 트랜잭션
    private LocalDateTime minVolumeDate;
    private double maxVolume; // 가장 많이 사용한 트랜잭션
    private LocalDateTime maxVolumeDate;

}