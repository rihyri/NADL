package com.rihyri.NADL.domain.festival.repository;

import com.rihyri.NADL.domain.festival.entity.Festival;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

    boolean existsByExternalId(String externalId);

    @Query(value = """
            SELECT * FROM festivals f
            WHERE ST_DWithin(
                ST_MakePoint(f.longitude, f.latitude)::geography,
                ST_MakePoint(:lng, :lat)::geography,
                :radiusMeters
            )
            AND f.is_active = true
            """, nativeQuery = true)
    List<Festival> findNearby(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMeters") double radiusMeters
    );
}