package com.rihyri.NADL.domain.festival.service;

import com.rihyri.NADL.domain.festival.dto.FestivalItemDto;
import com.rihyri.NADL.domain.festival.dto.TourApiResponse;
import com.rihyri.NADL.domain.festival.entity.Festival;
import com.rihyri.NADL.domain.festival.repository.FestivalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourApiService {

    private final WebClient webClient;
    private final FestivalRepository festivalRepository;

    @Value("${tourapi.service-key}")
    private String serviceKey;

    public int syncFestivals() {
        String eventStartDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        TourApiResponse<FestivalItemDto> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/searchFestival2")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("numOfRows", 100)
                        .queryParam("pageNo", 1)
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", "Nadeul")
                        .queryParam("_type", "json")
                        .queryParam("eventStartDate", eventStartDate)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<TourApiResponse<FestivalItemDto>>() {})
                .block();

        List<FestivalItemDto> items = response.getResponse().getBody().getItems().getItem();

        int savedCount = 0;
        for (FestivalItemDto item : items) {

            log.info("contentid: {}, title: {}", item.getContentId(), item.getTitle());

            if (festivalRepository.existsByExternalId(item.getContentId())) {
                continue; // 이미 있으면 skip
            }

            Festival festival = Festival.builder()
                    .externalId(item.getContentId())
                    .sourceApi("TourAPI")
                    .name(item.getTitle())
                    .address(item.getAddress())
                    .latitude(parseDoubleOrNull(item.getMapy()))
                    .longitude(parseDoubleOrNull(item.getMapx()))
                    .startDate(parseDateOrNull(item.getEventStartDate()))
                    .endDate(parseDateOrNull(item.getEventEndDate()))
                    .category(item.getCat3())
                    .imageUrl(item.getImageUrl())
                    .build();

            festivalRepository.save(festival);
            savedCount++;
        }

        log.info("TourAPI 동기화 완료 - 조회 {}건, 신규 저장 {}건", items.size(), savedCount);
        return savedCount;
    }

    private Double parseDoubleOrNull(String value) {
        return (value == null || value.isBlank()) ? null : Double.parseDouble(value);
    }

    private LocalDate parseDateOrNull(String value) {
        return (value == null || value.isBlank())
                ? null
                : LocalDate.parse(value, DateTimeFormatter.BASIC_ISO_DATE);
    }
}
