package com.example.quaterback.login.controller;

import com.example.quaterback.login.service.ReissueService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ReissueController.class)
class ReissueControllerTest {

    private final MockMvc mockMvc;

    @MockitoBean
    private ReissueService reissueService;

    ReissueControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @DisplayName("reissue - 재발급 성공 시 reissued refresh token 출력")
    @Test
    void reissueSuccess() throws Exception {

        //given
        String oldRefreshToken = "oldRefreshToken";
        String newRefreshToken = "newRefreshToken";
        String newAccessToken = "newAccessToken";

        Cookie cookie = new Cookie("refreshToken", oldRefreshToken);

        given(reissueService.validateRefresh(any())).willReturn(oldRefreshToken);
        given(reissueService.getNewAccessToken(oldRefreshToken)).willReturn(newAccessToken);
        given(reissueService.getNewRefreshToken(oldRefreshToken)).willReturn(newRefreshToken);

        //when & then
        mockMvc.perform(post("/reissue")
                        .cookie(cookie)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer " + newAccessToken))
                .andExpect(cookie().value("refreshToken", newRefreshToken))
                .andExpect(content().string("reissued refresh token"));
    }
}