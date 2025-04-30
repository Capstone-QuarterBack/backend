package com.example.quaterback.mapping.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sessionid_stationid")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MappingSessionIdWithStationIdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;
    private String stationId;
}
