package com.example.quaterback.api.feature.managing.controller;

import com.example.quaterback.api.domain.customer.service.CustomerService;
import com.example.quaterback.api.feature.managing.dto.request.CustomerUpdateRequest;
import com.example.quaterback.api.feature.managing.dto.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CustomerManageController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerManageControllerTest {

    private final MockMvc mockMvc;

    @MockBean private CustomerService customerService;
    @Autowired private ObjectMapper objectMapper;

    CustomerManageControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void 전체_고객_목록을_조회한다() throws Exception {
        CustomerListResponse response = CustomerListResponse.builder()
                .customerList(List.of(CustomerResponse.builder()
                                .customerId("c1")
                                .customerName("n1")
                                .idToken("t1")
                                .vehicleNo("v1")
                                .registrationDate(LocalDateTime.now().toString())
                        .build()))
                .currentPage(0)
                .totalElements(1L)
                .totalPages(1)
                .build();
        given(customerService.findAllCustomers(any(Pageable.class))).willReturn(response);

        mockMvc.perform(get("/api/managing/customer/customers")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void 고객_id로_검색한다() throws Exception {
        CustomerListResponse response = CustomerListResponse.builder()
                .customerList(List.of(CustomerResponse.builder()
                        .customerId("c1")
                        .customerName("n1")
                        .idToken("t1")
                        .vehicleNo("v1")
                        .registrationDate(LocalDateTime.now().toString())
                        .build()))
                .currentPage(0)
                .totalElements(1L)
                .totalPages(1)
                .build();
        given(customerService.searchCustomersByCustomerId(eq("c1"), any(Pageable.class))).willReturn(response);

        mockMvc.perform(get("/api/managing/customer/customers/search")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortDir", "desc")
                    .param("searchType", "customerId")
                    .param("keyword", "c1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void 고객_이름으로_검색한다() throws Exception {
        CustomerListResponse response = CustomerListResponse.builder()
                .customerList(List.of(CustomerResponse.builder()
                        .customerId("c1")
                        .customerName("n1")
                        .idToken("t1")
                        .vehicleNo("v1")
                        .registrationDate(LocalDateTime.now().toString())
                        .build()))
                .currentPage(0)
                .totalElements(1L)
                .totalPages(1)
                .build();
        given(customerService.searchCustomersByCustomerName(eq("n1"), any(Pageable.class))).willReturn(response);

        mockMvc.perform(get("/api/managing/customer/customers/search")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortDir", "desc")
                        .param("searchType", "customerName")
                        .param("keyword", "n1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void 고객의_정보를_상세_조회한다() throws Exception {
        CustomerDetailResponse response = CustomerDetailResponse.builder()
                .customerId("c1")
                .customerName("n1")
                .idToken("t1")
                .vehicleNo("v1")
                .registrationDate(LocalDateTime.now().toString())
                .email("email")
                .phone("phone")
                .build();
        given(customerService.findByCustomerId("c1")).willReturn(response);

        mockMvc.perform(get("/api/managing/customer/c1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void 고객_정보를_수정한다() throws Exception {
        CustomerUpdateRequest request = CustomerUpdateRequest.builder()
                .customerName("n1")
                .phone("01234")
                .email("email@naver")
                .vehicleNo("v1")
                .build();
        CustomerUpdateResponse response = CustomerUpdateResponse.builder()
                .customerId("c1")
                .build();

        given(customerService.updateCustomerInfo(eq("c1"), any(CustomerUpdateRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/managing/customer/c1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void 고객의_전체_충전_기록을_조회한다() throws Exception {
        CustomerChargedLogListResponse response = CustomerChargedLogListResponse.builder()
                .customerChargedLogList(List.of(CustomerChargedLogResponse.builder()
                        .startedTime(LocalDateTime.now().toString())
                        .endedTime(LocalDateTime.now().toString())
                        .vehicleNo("v1")
                        .transactionId("tx-001")
                        .totalMeterValue(10.0)
                        .totalPrice(10.0)
                        .build()))
                .currentPage(0)
                .totalElements(1L)
                .totalPages(1)
                .build();
        given(customerService.findAllChargedLogListByCustomerId(eq("c1"), any(Pageable.class))).willReturn(response);

        mockMvc.perform(get("/api/managing/customer/chargedLog/c1")
                    .param("page", "0")
                    .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void 기간_필터를_적용하여_고객의_충전_기록을_조회한다() throws Exception {
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = start.plusDays(1);
        CustomerChargedLogListResponse response = CustomerChargedLogListResponse.builder()
                .customerChargedLogList(List.of(CustomerChargedLogResponse.builder()
                        .startedTime(LocalDateTime.now().toString())
                        .endedTime(LocalDateTime.now().toString())
                        .vehicleNo("v1")
                        .transactionId("tx-001")
                        .totalMeterValue(10.0)
                        .totalPrice(10.0)
                        .build()))
                .currentPage(0)
                .totalElements(1L)
                .totalPages(1)
                .build();
        given(customerService.findChargedLogListByCustomerIdAndPeriod(eq("c1"), eq(start), eq(end), any(Pageable.class))).willReturn(response);

        mockMvc.perform(get("/api/managing/customer/chargedLog/c1/search")
                    .param("startDate", start.toString())
                    .param("endDate", end.toString())
                    .param("page", "0")
                    .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}