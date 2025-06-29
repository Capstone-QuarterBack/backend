package com.example.quaterback.api.feature.dashboard.controller;

import com.example.quaterback.api.config.MockBeansConfig;
import com.example.quaterback.api.feature.dashboard.controller.fixture.DashboardFixture;
import com.example.quaterback.api.feature.dashboard.dto.request.CsPriceRequest;
import com.example.quaterback.api.feature.dashboard.dto.response.*;
import com.example.quaterback.api.feature.dashboard.facade.DashboardFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import static com.example.quaterback.api.feature.dashboard.controller.fixture.DashboardFixture.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MockBeansConfig.class)
class DashboardControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    DashboardFacade dashboardFacade;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 시간대별_배출량을_정상적으로_조회한다() throws Exception {

        // given
        HourlyDischargeResponse sample1 = Hourly_샘플_응답();
        HourlyDischargeResponse sample2 = Hourly_샘플_응답2();
        when(dashboardFacade.getHourlyDischarge()).thenReturn(List.of(sample1, sample2));

        // when && then
        mockMvc.perform(get("/api/dashboard/discharge-by-hour"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hour").value(1))
                .andExpect(jsonPath("$[1].hour").value(2))
                .andExpect(jsonPath("$[0].dischargeKwh").value(10.0))
                .andExpect(jsonPath("$[1].dischargeKwh").value(20.0));
    }

    @Test
    void 대시보드_요약정보를_정상적으로_조회한다() throws Exception {
        // given
        DashboardSummaryResponse summaryResponse = summaryResponse_샘플_응답();
        when(dashboardFacade.getSummary()).thenReturn(summaryResponse);

        // when && then
        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usage").value(10))
                .andExpect(jsonPath("$.profit").value(10))
                .andExpect(jsonPath("$.discharge").value(10));
    }

    @Test
    void 충전소_사용량을_정상적으로_반환한다() throws Exception {
        // given
        int page = 0;
        int size = 10;
        ChargerUsagePageResponse response = usagePage_샘플_응답();
        Pageable pageable = PageRequest.of(page, size);
        when(dashboardFacade.getChargerUsage(pageable)).thenReturn(response);
        // when && then
        mockMvc.perform(get("/api/dashboard/chargers/usage")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usage[0].price").value("10.00 (KRW)"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void 전기가격을_정상적으로_반환한다() throws Exception {
        // given
        CsPriceHistory csPriceHistory = csPriceHistory_샘플_응답();
        CsPriceHistory csPriceHistory2 = csPriceHistory_샘플_응답2();
        List<CsPriceHistory> csPriceHistories = List.of(csPriceHistory, csPriceHistory2);
        when(dashboardFacade.getCsPriceHistory()).thenReturn(csPriceHistories);

        // when && then
        mockMvc.perform(get("/api/dashboard/electricity-price/cs-price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].csPrice").value(10.0))
                .andExpect(jsonPath("$[1].csPrice").value(20.0));
    }

    @Test
    void 전기가격_갱신을_정상적으로_수행한다() throws Exception {
        // given
        CsPriceRequest csPriceRequest = csPriceRequest_샘플_요청();
        UpdateCsPriceResponse response = new UpdateCsPriceResponse("업데이트가 정상적으로 완료되었습니다");
        when(dashboardFacade.updateCsPrice(csPriceRequest.getPrice())).thenReturn(response);

        // when && then
        mockMvc.perform(post("/api/dashboard/electricity-price/cs-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(csPriceRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("업데이트가 정상적으로 완료되었습니다"));
    }

    @Test
    void 외부_전기가격을_정상적으로_조회한다() throws Exception {
        // given
        KepcoResponse kepcoResponse = new KepcoResponse(10.0);
        when(dashboardFacade.getKepcoPrice()).thenReturn(kepcoResponse);

        // when && then
        mockMvc.perform(get("/api/dashboard/electricity-price/kepco-price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kepcoPrice").value(10.0));
    }

    @Test
    void 충전소_정보를_정상적으로_반환한다() throws Exception {
        // given
        StationFullInfoResponse response = stationFullInfo_샘플_응답();
        when(dashboardFacade.getStations()).thenReturn(List.of(response));

        // when && then
        mockMvc.perform(get("/api/dashboard/stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stationName").value("testModel"));
    }

    @Test
    void 이름으로_충전소_정보를_정상적으로_반환한다() throws Exception {
        // given
        String stationName = "testModel";
        StationFullInfoResponse response = stationFullInfo_샘플_응답();
        when(dashboardFacade.getStation(stationName)).thenReturn(response);

        // when && then
        mockMvc.perform(get("/api/dashboard/stations/" + stationName)
                        .param("stationName", stationName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationName").value(stationName));
    }

    @Test
    void 이름으로_충전소_정보를_삭제한다() throws Exception {
        // given
        String stationName = "testModel";
        DeleteResultResponse response = new DeleteResultResponse("testModel", "정상 삭제되었습니다.");
        when(dashboardFacade.removeStation(stationName)).thenReturn(response);

        // when && then
        mockMvc.perform(delete("/api/dashboard/stations/"+stationName)
                .param("stationName",stationName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationName").value(stationName))
                .andExpect(jsonPath("$.message").value("정상 삭제되었습니다."));
    }
}
