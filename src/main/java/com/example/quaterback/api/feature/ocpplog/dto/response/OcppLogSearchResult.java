package com.example.quaterback.api.feature.ocpplog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class OcppLogSearchResult {
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private List<OcppLogResponse> content;
}
