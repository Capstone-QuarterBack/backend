package com.example.quaterback.login.service;

import com.example.quaterback.exception.DuplicateJoinException;
import com.example.quaterback.login.dto.JoinRequest;
import com.example.quaterback.login.entity.UserEntity;
import com.example.quaterback.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public String joinProcess(JoinRequest joinRequest){

        String username = joinRequest.username();
        String password = joinRequest.password();

        if (userRepository.existsByUsername(username)){
            throw new DuplicateJoinException("이미 존재하는 ID입니다.");
        }

        UserEntity data = UserEntity.of(username, bCryptPasswordEncoder.encode(password));

        return userRepository.save(data).getUsername();

    }
}
