package com.example.quaterback.login.repository;

import com.example.quaterback.login.entity.RefreshEntity;
import com.example.quaterback.login.repository.refresh.RefreshRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakeRefreshRepository implements RefreshRepository {

    private final Map<String, RefreshEntity> storage = new ConcurrentHashMap<>();

    @Override
    public Boolean existsByRefresh(String refresh) {
        return storage.containsKey(refresh);
    }

    @Override
    public void deleteByRefresh(String refresh) {
        storage.remove(refresh);
    }

    @Override
    public RefreshEntity save(RefreshEntity refreshEntity) {
        storage.put(refreshEntity.getRefresh(), refreshEntity);
        return storage.get(refreshEntity.getRefresh());
    }
}
