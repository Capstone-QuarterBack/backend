package com.example.quaterback.websocket.mongodb;

import com.example.quaterback.api.feature.ocpplog.dto.request.OcppLogFilterRequest;
import com.example.quaterback.api.feature.ocpplog.dto.response.OcppLogResponse;
import com.example.quaterback.api.feature.ocpplog.dto.response.OcppLogSearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RawOcppMessageRepositoryImpl implements RawOcppMessageRepositoryCustom {

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public OcppLogSearchResult findByFilters(OcppLogFilterRequest filter, Pageable pageable) {
        Query query = new Query();

        if (filter.getStationId() != null) {
            query.addCriteria(Criteria.where("stationId").is(filter.getStationId()));
        }
        if (filter.getStartDate() != null || filter.getEndDate() != null) {
            Criteria criteria = Criteria.where("timestamp");
            if (filter.getStartDate() != null) {
                criteria = criteria.gte(filter.getStartDate().atStartOfDay());
            }
            if (filter.getEndDate() != null) {
                criteria = criteria.lt(filter.getEndDate().plusDays(1).atStartOfDay());
            }
            query.addCriteria(criteria);
        }
        if (filter.getMessageType() != null) {
            query.addCriteria(Criteria.where("message.message.0").is(filter.getMessageType()));
        }
        if (filter.getAction() != null) {
            query.addCriteria(Criteria.where("action").is(filter.getAction()));
        }

        long total = mongoTemplate.count(query, RawOcppMessage.class);
        query.with(pageable);

        List<RawOcppMessage> results = mongoTemplate.find(query, RawOcppMessage.class);

        List<OcppLogResponse> dtoList = new ArrayList<>();
        for (RawOcppMessage rawOcppMessage : results) {
            JsonNode array = objectMapper.valueToTree(rawOcppMessage.getMessage().get("message"));

            Integer messageType = array.get(0).isInt() ? array.get(0).asInt() : null;
            String action = rawOcppMessage.getAction();

            dtoList.add(new OcppLogResponse(
                    rawOcppMessage.getTimestamp().plusHours(9),
                    rawOcppMessage.getStationId(),
                    messageType,
                    action,
                    array
            ));
        }

        Page<OcppLogResponse> pageResult = new PageImpl<>(dtoList, pageable, total);
        return new OcppLogSearchResult(
                pageResult.getNumber(),
                pageResult.getTotalPages(),
                pageResult.getTotalElements(),
                pageResult.getContent()
        );
    }

    @Override
    public String findByMessageId(String messageId) {
        Query query = new Query(Criteria.where("message.1").is(messageId));
        RawOcppMessage rawOcppMessage = mongoTemplate.findOne(query, RawOcppMessage.class);
        if (rawOcppMessage != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode array = objectMapper.valueToTree(rawOcppMessage.getMessage());
            return array.get(2).asText();
        }
        return null;
    }


}
