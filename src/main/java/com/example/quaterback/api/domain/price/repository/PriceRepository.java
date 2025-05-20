package com.example.quaterback.api.domain.price.repository;

import com.example.quaterback.api.domain.price.entity.PricePerMwh;
import com.example.quaterback.api.feature.statistics.dto.response.StatisticsData;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceRepository extends JpaRepository<PricePerMwh, Long> {
    PricePerMwh findTopByOrderByUpdatedDateTimeDesc();
    List<PricePerMwh> findAllByOrderByUpdatedDateTimeDesc();

    @Query(value = """
    SELECT p.*
    FROM cs_price p
    INNER JOIN (
        SELECT DATE(updated_date_time) AS date_only, MAX(updated_date_time) AS max_time
        FROM cs_price
        WHERE updated_date_time < NOW()
        GROUP BY DATE(updated_date_time)
    ) latest_per_day
    ON DATE(p.updated_date_time) = latest_per_day.date_only
    AND p.updated_date_time = latest_per_day.max_time
    ORDER BY p.updated_date_time DESC
    """, nativeQuery = true)
    List<PricePerMwh> findDailyCsPrice7DayRaw();

    @Query("""
        SELECT p FROM PricePerMwh p
        WHERE p.updatedDateTime BETWEEN :start AND :end
        ORDER BY p.updatedDateTime ASC
    """)
    List<PricePerMwh> findByUpdatedDateTimeBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
