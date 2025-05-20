package com.example.quaterback.api.feature.monitoring.dto.response;

import com.example.quaterback.api.domain.txlog.entity.TransactionLogEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeAndValueDto {
    LocalDateTime timestamp;
    Double value;

    public static TimeAndValueDto from(TransactionLogEntity entity){
        return new TimeAndValueDto(entity.getTimestamp(), entity.getMeterValue());
    }
}
