package com.example.quaterback.websocket.transaction.event.domain;

import com.example.quaterback.websocket.sub.MeterValue;
import com.example.quaterback.websocket.transaction.event.domain.sub.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionEventDomain {
    private String messageTypeId;
    private String messageId;
    private String action;

    private String eventType;
    private LocalDateTime timestamp;
    private String triggerReason;
    private Integer seqNo;

    private TransactionInfo transactionInfo;
    private Evse evse;
    private TxIdToken txIdToken;
    private TransactionCustomData customData;
    private List<MeterValue> meterValue;

    public String extractTransactionId() {
        return transactionInfo.getTransactionId();
    }

    public Integer extractEvseId() {
        return evse.getId();
    }

    public String extractIdToken() { return txIdToken.getIdToken(); }

    public String extractVendorId() {
        return customData.getVendorId();
    }

    public String extractVehicleNo() {
        return customData.getVehicleInfo().getVehicleNo();
    }

    public String extractUserId() {
        return txIdToken.getIdToken();
    }

    public Integer extractMeterValue() {
        return meterValue.get(0).getSampledValues().get(0).getValue();
    }

}
