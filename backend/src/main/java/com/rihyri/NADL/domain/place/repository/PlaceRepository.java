package com.rihyri.NADL.domain.place.repository;

import com.rihyri.NADL.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    boolean existsByExternalId(String externalId);
}
