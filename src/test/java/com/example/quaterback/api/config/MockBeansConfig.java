package com.example.quaterback.api.config;

import com.example.quaterback.api.feature.dashboard.facade.DashboardFacade;
import com.example.quaterback.api.feature.monitoring.facade.StationMonitoringFacade;
import com.example.quaterback.websocket.transaction.event.service.TransactionEventService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class MockBeansConfig {

    @Bean
    public DashboardFacade getDashboardFacade() {
        return mock(DashboardFacade.class);
    }

    @Bean
    public StationMonitoringFacade getStationMonitoringFacade() {
        return mock(StationMonitoringFacade.class);
    }

    @Bean
    public TransactionEventService getTransactionEventService() {
        return mock(TransactionEventService.class);
    }
}
