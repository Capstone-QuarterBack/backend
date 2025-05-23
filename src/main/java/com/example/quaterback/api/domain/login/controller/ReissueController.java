package com.example.quaterback.api.domain.login.controller;

import com.example.quaterback.api.domain.login.dto.ReissueResponse;
import com.example.quaterback.api.domain.login.service.ReissueService;
import com.example.quaterback.common.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ReissueController {

    private final ReissueService reissueService;

    @PostMapping("/reissue")
    public ResponseEntity<ReissueResponse> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = reissueService.validateRefresh(request.getCookies());

        String newRefreshToken = reissueService.getNewRefreshToken(refreshToken);
        String newAccessToken = reissueService.getNewAccessToken(refreshToken);
        response.setHeader("Authorization", "Bearer " + newAccessToken);
        response.addCookie(CookieUtil.createCookie("refreshToken", newRefreshToken));
        ReissueResponse result = new ReissueResponse("success");
        return ResponseEntity.ok(result);
    }
}
