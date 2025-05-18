package com.example.quaterback.api.feature.statistics.dto;

import com.example.quaterback.api.domain.price.constant.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PowerTradingPriceData {
    private double overallAverage;
    private List<TimeSlotPriceDetail> slotDetails;
}
