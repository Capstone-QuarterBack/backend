package com.example.quaterback.login.controller;

import com.example.quaterback.login.service.ReissueService;
import com.example.quaterback.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReissueController {

    private final ReissueService reissueService;

    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken;
        try{
            refreshToken = reissueService.validateRefresh(request.getCookies());
        } catch (RuntimeException e){
            throw e;
        }

        String newRefreshToken = reissueService.getNewRefreshToken(refreshToken);
        String newAccessToken = reissueService.getNewAccessToken(refreshToken);
        response.setHeader("Authorization", "Bearer " + newAccessToken);
        response.addCookie(CookieUtil.createCookie("refreshToken", newRefreshToken));
        return new ResponseEntity<>("reissued refresh token", HttpStatus.OK);
    }

}
