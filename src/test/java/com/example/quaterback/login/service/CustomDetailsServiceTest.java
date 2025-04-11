package com.example.quaterback.login.service;

import com.example.quaterback.login.entity.UserEntity;
import com.example.quaterback.login.repository.FakeUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomDetailsServiceTest {

    private CustomDetailsService customDetailsService;
    private FakeUserRepository fakeUserRepository;

    @BeforeEach
    void setUp() {
        fakeUserRepository = new FakeUserRepository();
        customDetailsService = new CustomDetailsService(fakeUserRepository);
    }

    @DisplayName("loadUserByUsername - DB에 존재하는 username의 경우 CustomUserDetails 객체 반환")
    @Test
    void loadUserByUsernameSuccess() {

        //given
        UserEntity userEntity = UserEntity.builder()
                .username("userA")
                .password("temp")
                .build();
        fakeUserRepository.save(userEntity.toDomain());

        //when
        UserDetails result = customDetailsService.loadUserByUsername("userA");

        //then
        assertThat(result.getUsername()).isEqualTo("userA");

    }

    @DisplayName("loadUserByUsername - 존재하지 않는 username이면 예외 발생")
    @Test
    void loadUserByUsernameFailed() {

        //when & then
        assertThatThrownBy(() -> customDetailsService.loadUserByUsername("userA"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("username not found");
    }
}