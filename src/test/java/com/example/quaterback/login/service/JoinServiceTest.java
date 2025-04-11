package com.example.quaterback.login.service;

import com.example.quaterback.exception.DuplicateJoinException;
import com.example.quaterback.login.repository.FakeUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JoinServiceTest {

    private FakeUserRepository userRepository;
    private BCryptPasswordEncoder encoder;
    private JoinService joinService;

    @BeforeEach
    void setUp() {
        userRepository = new FakeUserRepository();
        encoder = new BCryptPasswordEncoder();
        joinService = new JoinService(userRepository, encoder);
    }

    @DisplayName("joinProcess - 회원 가입 정상 처리")
    @Test
    void joinProcessSuccess() {

        //given
        String username = "newuser";
        String password = "1234";

        //when
        String result = joinService.joinProcess(username, password);

        //then
        assertThat(result).isEqualTo(username);
        assertThat(userRepository.existsByUsername(username)).isTrue();
        assertThat(password).isNotEqualTo(userRepository.findByUsername(username).getPassword());
    }

    @DisplayName("joinProcess - 중복된 username으로 가입 시 예외 발생")
    @Test
    void joinProcessDuplicateJoinException() {

        //given
        String username = "newuser";
        String password = "1234";
        joinService.joinProcess(username, password);

        //when & then
        assertThatThrownBy(() -> joinService.joinProcess(username, password))
                .isInstanceOf(DuplicateJoinException.class)
                .hasMessage("이미 존재하는 ID입니다.");

    }
}