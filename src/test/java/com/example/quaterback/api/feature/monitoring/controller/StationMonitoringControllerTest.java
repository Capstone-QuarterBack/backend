package com.example.quaterback.api.feature.monitoring.controller;

import com.example.quaterback.api.config.MockBeansConfig;
import com.example.quaterback.api.domain.charger.constant.ChargerStatus;
import com.example.quaterback.api.feature.monitoring.controller.fixture.MonitoringFixture;
import com.example.quaterback.api.feature.monitoring.dto.response.*;
import com.example.quaterback.api.feature.monitoring.facade.StationMonitoringFacade;
import com.example.quaterback.websocket.transaction.event.service.TransactionEventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.quaterback.api.feature.monitoring.controller.fixture.MonitoringFixture.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StationMonitoringController.class)
@Import(MockBeansConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class StationMonitoringControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    StationMonitoringFacade stationMonitoringFacade;
    @Autowired
    TransactionEventService transactionEventService;

    @Test
    void 충전내역들을_정상적으로_조회한다() throws Exception {
        // given
        int size = 10;
        int page = 0;
        ChargingRecordResponsePage charingResponsePage = 충전_기록_페이지();
        PageRequest pageable = PageRequest.of(page, size, Sort.by("endedTime").descending());
        when(stationMonitoringFacade.getChargingHistory("테스트ID", pageable)).thenReturn(charingResponsePage);

        // when && then
        mockMvc.perform(get("/api/monitoring/charging-history/테스트ID")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationName").value("테스트이름"));
    }

    @Test
    void 시간당_혼잡량을_정상적으로_반환한다() throws Exception {

        // given
        HourlyCongestion hourlyCongestion1 = 시간당_혼잡(10, 100L, false);
        HourlyCongestion hourlyCongestion2 = 시간당_혼잡(5, 230L, true);
        List<HourlyCongestion> hourlyCongestions = List.of(hourlyCongestion1, hourlyCongestion2);
        when(stationMonitoringFacade.getCongestionChart("테스트ID")).thenReturn(hourlyCongestions);

        // when && then
        mockMvc.perform(get("/api/monitoring/congestion/테스트ID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hour").value(10))
                .andExpect(jsonPath("$[1].hour").value(5))
                .andExpect(jsonPath("$[0].count").value(100L))
                .andExpect(jsonPath("$[1].count").value(230L))
                .andExpect(jsonPath("$[1].isPeak").value(true));
    }

    @Test
    void 충전기_종류와_상태를_반환한다() throws Exception {
        // given
        EvseIdResponse 충전기1 = 충전기_상태(1, ChargerStatus.AVAILABLE);
        EvseIdResponse 충전기2 = 충전기_상태(2, ChargerStatus.OCCUPIED);
        List<EvseIdResponse> evseList = List.of(충전기1, 충전기2);
        when(stationMonitoringFacade.getEvseIds("테스트ID")).thenReturn(evseList);

        // when && then
        mockMvc.perform(get("/api/monitoring/evse-ids/테스트ID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].evseId").value(1))
                .andExpect(jsonPath("$[0].chargerStatus").value(ChargerStatus.AVAILABLE.name()))
                .andExpect(jsonPath("$[1].evseId").value(2))
                .andExpect(jsonPath("$[1].chargerStatus").value(ChargerStatus.OCCUPIED.name()));
    }

    @Test
    void 이용가능한_충전기_정보를_정상적으로_반환한다() throws Exception {
        // given
        int size = 10;
        int page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by("endedTime").descending());
        AvailableChargerPageResponse 가용_충전기_페이지_응답 = 가용_충전기_페이지_응답();
        when(stationMonitoringFacade.getAvailableChargerInfo("테스트ID", 1, pageable)).thenReturn(가용_충전기_페이지_응답);

        // when && then
        mockMvc.perform(get("/api/monitoring/charger-info/테스트ID/available/1")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalChargedEnergy").value(321.5));
    }

    @Test
    void 이용_불가능한_총전기_페이지_정보를_반환한다() throws Exception {
        // given
        int size = 10;
        int page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by("endedTime").descending());
        UnavailableChargerPageResponse 비가용_충전기_페이지_응답 = MonitoringFixture.비가용_충전기_페이지_응답();

        when(stationMonitoringFacade.getUnavailableChargerInfo("테스트ID", 1, pageable))
                .thenReturn(비가용_충전기_페이지_응답);

        // when && then
        mockMvc.perform(get("/api/monitoring/charger-info/테스트ID/unavailable/1")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());
    }

    @Test
    void 배터리량을_정상적으로_반환한다() throws Exception {
        // given
        EssValueResponse essValueResponse = new EssValueResponse(10.0);
        when(stationMonitoringFacade.getEssValue("테스트ID")).thenReturn(essValueResponse);

        // when && then
        mockMvc.perform(get("/api/monitoring/ess-value/테스트ID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.essValue").value(10.0));

    }

    @Test
    void 시간별_사용량을_정상적으로_반환한다() throws Exception {
        // given
        TimeAndValueDto timeAndValueDto = new TimeAndValueDto(LocalDateTime.of(1, 1, 1, 1, 1, 1), 10.0);
        List<TimeAndValueDto> timeAndValueDtos = List.of(timeAndValueDto);
        when(transactionEventService.getLiveInfo(1, "테스트ID")).thenReturn(timeAndValueDtos);

        // when && then
        mockMvc.perform(get("/api/monitoring/live-streaming/1/테스트ID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value").value(10.0));
    }
}