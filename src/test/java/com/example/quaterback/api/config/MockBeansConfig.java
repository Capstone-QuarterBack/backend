package com.example.quaterback.api.config;

import com.example.quaterback.api.feature.dashboard.facade.DashboardFacade;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class MockBeansConfig {

    @Bean
    public DashboardFacade getDashboardFacade() {
        return mock(DashboardFacade.class);
    }
}
