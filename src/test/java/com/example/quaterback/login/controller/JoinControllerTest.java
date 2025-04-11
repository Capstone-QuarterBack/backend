package com.example.quaterback.login.controller;

import com.example.quaterback.login.service.JoinService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(JoinController.class)
class JoinControllerTest {

    private final MockMvc mockMvc;

    @MockitoBean
    private JoinService joinService;

    JoinControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @DisplayName("join - 회원 가입 성공 시 username을 출력")
    @Test
    void joinSuccess() throws Exception {

        //given
        String username = "userA";
        String password = "tempPw";
        given(joinService.joinProcess(username, password)).willReturn(username);

        //when & then
        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", username)
                        .param("password", password)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(username));
    }
}