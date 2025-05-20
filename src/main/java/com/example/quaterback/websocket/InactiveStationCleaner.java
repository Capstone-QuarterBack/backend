package com.example.quaterback.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class InactiveStationCleaner {

    private final InactiveStationService inactiveStationService;

    @Scheduled(fixedRate = 15_000) // 15초마다 실행
    public void cleanInactiveStations() {
        inactiveStationService.cleanInactiveStationsWithTx();
    }
}
