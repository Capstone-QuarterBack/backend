package com.example.quaterback.api.domain.activeStationRecord;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

@Entity
@Table(name = "active_station")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActiveStationRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationId;

    @Column(nullable = false)
    private LocalDateTime startTime;
}
