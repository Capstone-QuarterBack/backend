package com.example.quaterback.login.repository;

import com.example.quaterback.login.domain.UserDomain;
import com.example.quaterback.login.entity.UserEntity;
import com.example.quaterback.login.repository.user.UserRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakeUserRepository implements UserRepository {

    private final Map<String, UserEntity> storage = new ConcurrentHashMap<>();

    @Override
    public Boolean existsByUsername(String username) {
        return storage.containsKey(username);
    }

    @Override
    public UserEntity findByUsername(String username) {
        return storage.get(username);
    }

    @Override
    public UserDomain save(UserDomain userDomain) {
        UserEntity userEntity = UserEntity.from(userDomain);
        storage.put(userEntity.getUsername(), userEntity);
        return storage.get(userEntity.getUsername()).toDomain();
    }
}
