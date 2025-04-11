package com.example.quaterback.login.service;

import com.example.quaterback.login.entity.RefreshEntity;
import com.example.quaterback.login.jwt.JWTUtil;
import com.example.quaterback.login.repository.FakeRefreshRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Date;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReissueServiceTest {

    private String secret = "ThisIsAFakeSecretKeyForTestingDontUseInProduction12345";
    private JWTUtil jwtUtil;
    private FakeRefreshRepository fakeRefreshRepository;
    private ReissueService reissueService;

    @BeforeEach
    void setUp() {
        jwtUtil = new JWTUtil(secret);
        fakeRefreshRepository = new FakeRefreshRepository();
        reissueService = new ReissueService(jwtUtil, fakeRefreshRepository);
    }

    @DisplayName("validateRefresh - 쿠키에 유효한 토큰이 있을 경우 토큰 반환")
    @Test
    void validateRefreshSuccess() {

        //given
        String username = "userA";
        String refreshToken = jwtUtil.createJwt("refreshToken", username, 86400000L);
        Date date = new Date(System.currentTimeMillis() + 86400000L);
        fakeRefreshRepository.save(RefreshEntity.builder()
                .username(username)
                .refresh(refreshToken)
                .expiration(date.toString())
                .build());

        //when
        String result = reissueService.validateRefresh(new Cookie[]{new Cookie("refreshToken", refreshToken)});

        //then
        assertThat(result).isEqualTo(refreshToken);

    }

    static Stream<Arguments> invalidRefreshTokens(JWTUtil jwtUtil) throws InterruptedException {
        String fakeToken = "fake";
        String expiredToken = jwtUtil.createJwt("refreshToken", "userA", 1L);
        Thread.sleep(2);
        String nullToken = null;
        String accessToken = jwtUtil.createJwt("accessToken", "userA", 86400000L);
        String validButNotSavedToken = jwtUtil.createJwt("refreshToken", "userA", 86400000L);

        return Stream.of(
                Arguments.of(new Cookie("refreshToken", fakeToken), "잘못된 형식의 토큰"),
                Arguments.of(new Cookie("refreshToken", expiredToken), "만료된 토큰"),
                Arguments.of(new Cookie("refreshToken", nullToken), "토큰 null"),
                Arguments.of(new Cookie("refreshToken", accessToken), "refresh가 아닌 토큰"),
                Arguments.of(new Cookie("refreshToken", validButNotSavedToken), "유효하지만 저장되지 않은 토큰")
        );
    }

    static Stream<Arguments> invalidRefreshTokensWrapper() throws InterruptedException {
        JWTUtil tempUtil = new JWTUtil("ThisIsAFakeSecretKeyForTestingDontUseInProduction12345");
        return invalidRefreshTokens(tempUtil);
    }

    @DisplayName("validateRefresh - 유효하지 않은 토큰인 경우 예외 발생")
    @ParameterizedTest(name = "[{index}] {1}")
    @MethodSource("invalidRefreshTokensWrapper")
    void validateRefreshFailed(Cookie cookie, String caseDescription) {

        //when & then
        assertThatThrownBy(() ->
                reissueService.validateRefresh(new Cookie[]{cookie}))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("invalid token");

    }

    @DisplayName("getNewAccessToken - refreshToken으로 새로운 accessToken을 반환")
    @Test
    void getNewAccessTokenSuccess() {

        //given
        String username = "userA";
        String refreshToken = jwtUtil.createJwt("refreshToken", username, 86400000L);

        //when
        String newAccessToken = reissueService.getNewAccessToken(refreshToken);

        //then
        assertThat(jwtUtil.getUsername(newAccessToken)).isEqualTo(username);
        assertThat(jwtUtil.getCategory(newAccessToken)).isEqualTo("accessToken");
    }

    @DisplayName("getNewRefreshToken - refreshToken으로 새로운 refreshToken을 반환 후 기존 토큰은 DB에서 제거, 새 토큰은 저장")
    @Test
    void getNewRefreshTokenSuccess() throws InterruptedException {

        //given
        String username = "userA";
        String oldRefreshToken = jwtUtil.createJwt("refreshToken", username, 86400000L);
        Date date = new Date(System.currentTimeMillis() + 86400000L);
        fakeRefreshRepository.save(RefreshEntity.builder()
                .username(username)
                .refresh(oldRefreshToken)
                .expiration(date.toString())
                .build());

        //when
        String newRefreshToken = reissueService.getNewRefreshToken(oldRefreshToken);

        //then
        assertThat(newRefreshToken).isNotEqualTo(oldRefreshToken);
        assertThat(fakeRefreshRepository.existsByRefresh(oldRefreshToken)).isFalse();
        assertThat(fakeRefreshRepository.existsByRefresh(newRefreshToken)).isTrue();
        assertThat(jwtUtil.getUsername(newRefreshToken)).isEqualTo(username);
        assertThat(jwtUtil.getCategory(newRefreshToken)).isEqualTo("refreshToken");
    }

    @DisplayName("addRefreshEntity - DB에 refreshToken을 저장")
    @Test
    void addRefreshEntitySuccess() {

        //given
        String username = "userA";
        String refreshToken = jwtUtil.createJwt("refreshToken", username, 86400000L);
        Long expiredMs = 86400000L;

        //when
        reissueService.addRefreshEntity(username, refreshToken, expiredMs);

        //then
        assertThat(fakeRefreshRepository.existsByRefresh(refreshToken)).isTrue();

    }
}