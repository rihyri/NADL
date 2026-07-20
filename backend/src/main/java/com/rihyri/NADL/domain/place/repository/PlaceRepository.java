package com.rihyri.NADL.domain.place.repository;

import com.rihyri.NADL.domain.place.entity.Place;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByExternalIdAndSourceApi(String externalId, String sourceApi);

    @Modifying
    @Query("UPDATE place p SET p.bookmarkCount = p.bookmarkCount + 1 WHERE p.id = :id")
    void incrementBookmarkCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE place p SET p.bookmarkCount = p.bookmarkCount - 1 WHERE p.id = :id AND p.bookmarkCount > 0")
    void decrementBookmarkCount(@Param("id") Long id);
}
