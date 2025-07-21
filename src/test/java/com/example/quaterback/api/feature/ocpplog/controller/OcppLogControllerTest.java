package com.example.quaterback.api.feature.ocpplog.controller;

import com.example.quaterback.api.feature.ocpplog.dto.request.OcppLogFilterRequest;
import com.example.quaterback.api.feature.ocpplog.dto.response.OcppLogResponse;
import com.example.quaterback.api.feature.ocpplog.dto.response.OcppLogSearchResult;
import com.example.quaterback.api.feature.ocpplog.dto.response.StationActionList;
import com.example.quaterback.api.feature.ocpplog.service.OcppLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OcppLogController.class)
@AutoConfigureMockMvc(addFilters = false)
class OcppLogControllerTest {

    private final MockMvc mockMvc;

    @MockBean private OcppLogService ocppLogService;
    @Autowired private ObjectMapper objectMapper;

    public OcppLogControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void Ocpp_메시지를_필터링하여_조회한다() throws Exception {
        OcppLogFilterRequest request = OcppLogFilterRequest.builder()
                .stationId("s1")
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(1))
                .messageType(1)
                .action("a1")
                .build();
        OcppLogSearchResult response = OcppLogSearchResult.builder()
                .currentPage(0)
                .totalElements(1)
                .totalPages(1)
                .content(List.of(OcppLogResponse.builder()
                                .action("a1")
                                .messageType(1)
                                .stationId("s1")
                                .timestamp(LocalDateTime.now())
                                .rawMessage(objectMapper.createArrayNode())
                        .build()))
                .build();

        given(ocppLogService.search(any(OcppLogFilterRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/ocpp-log/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void 충전소_리스트와_액션_리스트를_조회한다() throws Exception {
        StationActionList response = new StationActionList(List.of("s1", "s2"), List.of("a1", "a2"));

        given(ocppLogService.getStationActionList()).willReturn(response);

        mockMvc.perform(get("/api/ocpp-log/station-action-list"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}