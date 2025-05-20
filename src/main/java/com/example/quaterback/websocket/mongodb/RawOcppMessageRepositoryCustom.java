package com.example.quaterback.websocket.mongodb;

import com.example.quaterback.api.feature.ocpplog.dto.request.OcppLogFilterRequest;
import com.example.quaterback.api.feature.ocpplog.dto.response.OcppLogSearchResult;
import org.springframework.data.domain.Pageable;

public interface RawOcppMessageRepositoryCustom {
    OcppLogSearchResult findByFilters(OcppLogFilterRequest request, Pageable pageable);
    String findByMessageId(String messageId);
}
