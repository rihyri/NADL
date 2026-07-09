package com.rihyri.NADL.global.common;

import com.rihyri.NADL.domain.festival.dto.FestivalItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class TourApiClient {

    private final WebClient webClient;

    @Value("${tourapi.service-key}")
    private String serviceKey;

    public TourApiResponse<FestivalItemDto> searchFestivals(String eventStartDate) {
        return webClient.get()
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
    }
}
