package com.example.quaterback.api.feature.statistics.service;

import com.example.quaterback.api.domain.charger.repository.ChargerRepository;
import com.example.quaterback.api.domain.chargerUptime.repository.ChargerInfoRepository;
import com.example.quaterback.api.domain.price.repository.PriceRepository;
import com.example.quaterback.api.domain.txinfo.repository.TxInfoRepository;
import com.example.quaterback.api.feature.statistics.converter.StatisticsConverter;
import com.example.quaterback.api.feature.statistics.dto.query.MonthlyTransactionStatistics;
import com.example.quaterback.api.feature.statistics.dto.request.ChartType;
import com.example.quaterback.api.feature.statistics.dto.response.StatisticsData;
import com.example.quaterback.api.feature.statistics.dto.response.StatisticsSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final TxInfoRepository txInfoRepository;
    private final ChargerRepository chargerRepository;
    private final PriceRepository priceRepository;
    private final ChargerInfoRepository ChargerInfoRepository;
    ;

    private final StatisticsConverter converter;

    public StatisticsSummary getSummary() {
        MonthlyTransactionStatistics cur = txInfoRepository.getMonthlyStatisticsByYearAndMonth(
                LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonthValue());
        MonthlyTransactionStatistics prev = txInfoRepository.getMonthlyStatisticsByYearAndMonth(
                LocalDate.now().minusMonths(1).getYear(),
                LocalDate.now().minusMonths(1).getMonthValue()
        );

        double volumeChangePercent = 0.0;
        double countChangePercent = 0.0;
        double revenueChangePercent = 0.0;
        double timeChangePercent = 0.0;
        if (prev.getTotalChargingVolume() != 0) {
            volumeChangePercent = ((double) (cur.getTotalChargingVolume() - prev.getTotalChargingVolume()) / prev.getTotalChargingVolume()) * 100;
            volumeChangePercent = Math.round(volumeChangePercent * 100.0) / 100.0;
        }
        if (prev.getTotalChargingCount() != 0) {
            countChangePercent = ((double) (cur.getTotalChargingCount() - prev.getTotalChargingCount()) / prev.getTotalChargingCount()) * 100;
            countChangePercent = Math.round(countChangePercent * 100.0) / 100.0;
        }
        if (prev.getTotalRevenue() != 0) {
            revenueChangePercent = ((double) (cur.getTotalRevenue() - prev.getTotalRevenue()) / prev.getTotalRevenue()) * 100;
            revenueChangePercent = Math.round(revenueChangePercent * 100.0) / 100.0;
        }
        if (prev.getAverageChargingTime() != 0) {
            timeChangePercent = ((double) (cur.getAverageChargingTime() - prev.getAverageChargingTime()) / prev.getAverageChargingTime()) * 100;
            timeChangePercent = Math.round(timeChangePercent * 100.0) / 100.0;
        }

        return StatisticsSummary.builder()
                .totalChargingVolume(cur.getTotalChargingVolume())
                .totalChargingCount(cur.getTotalChargingCount())
                .totalRevenue(cur.getTotalRevenue())
                .averageChargingTime(cur.getAverageChargingTime())
                .comparisonWithPrevious(
                        StatisticsSummary.ComparisonWithPrevious.builder()
                                .volumeChangePercent(volumeChangePercent)
                                .countChangePercent(countChangePercent)
                                .revenueChangePercent(revenueChangePercent)
                                .timeChangePercent(timeChangePercent)
                                .build()
                )
                .build();
    }

    public StatisticsData getCostStatistics(ChartType chartType) {
        List<StatisticsData.ChartData> results = txInfoRepository.findDailyRevenueLast7DaysRaw();
        return converter.toStatisticsData(results, chartType);
    }

    public StatisticsData getChargingVolumeStatistics(ChartType chartType) {
        List<StatisticsData.ChartData> results = txInfoRepository.findDailyUsageLast7DayRaw();
        return converter.toStatisticsData(results, chartType);
    }

    public StatisticsData getChargingInfoStatistics(ChartType chartType) {
        List<StatisticsData.ChartData> results = txInfoRepository.countChargingSpeedByMonth();
        return converter.toStatisticsData(results, chartType);
    }

    public StatisticsData getChargerStatusStatistics(ChartType chartType) {
        List<StatisticsData.ChartData> results = chargerRepository.countFaultAndNormalChargers();
        return converter.toStatisticsData(results, chartType);
    }

    public StatisticsData getPowerTradingStatistics(ChartType chartType) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<StatisticsData.ChartData> results = priceRepository.findDailyCsPrice7DayRaw()
                .stream().map(price -> StatisticsData.ChartData.builder()
                        .label(price.getUpdatedDateTime().format(formatter))  // 여기 수정
                        .value(price.getPricePerMwh())
                        .build()
                ).toList();

        return converter.toStatisticsData2(results, chartType);
    }

    public StatisticsData getTransactionCountStatistics(ChartType chartType) {
        List<StatisticsData.ChartData> results = txInfoRepository.findDailyTxCount(chartType);
        return converter.toStatisticsData(results, chartType);
    }

    public StatisticsData getTotalPriceGroupedByStationId(ChartType chartType) {
        List<StatisticsData.ChartData> results = txInfoRepository.findTotalPriceGroupedByStationId();
        return converter.toStatisticsData(results, chartType);
    }

    public StatisticsData getMeterValueGroupedByTimeType(ChartType chartType) {
        List<StatisticsData.ChartData> results = txInfoRepository.findMeterValueGroupedByTimeType();
        return converter.toStatisticsData(results, chartType);
    }

    public StatisticsData getOperatingRate(ChartType chartType) {
        List<StatisticsData.ChartData> results = ChargerInfoRepository.findOperatingRate();
        return converter.toStatisticsData(results,chartType);
    }
}
