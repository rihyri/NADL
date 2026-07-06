package com.rihyri.NADL.domain.festival.repository;

import com.rihyri.NADL.domain.festival.entity.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

    boolean existsByExternalId(String externalId);
}
