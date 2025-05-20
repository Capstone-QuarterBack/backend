package com.example.quaterback.api.feature.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PowerTradingRevenueData {
    private double netRevenue;
    private String percentText;
}
