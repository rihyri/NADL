package com.rihyri.NADL.global.common;

import com.rihyri.NADL.domain.festival.dto.FestivalItemDto;
import com.rihyri.NADL.domain.place.dto.PlaceDetailDto;
import com.rihyri.NADL.domain.place.dto.PlaceItemDto;
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

    public TourApiResponse<PlaceItemDto> searchNearbyPlaces(double lat, double lng, double radius) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/locationBasedList2")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("numOfRows", 50)
                        .queryParam("pageNo", 1)
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", "Nadeul")
                        .queryParam("_type", "json")
                        .queryParam("mapX", lng)
                        .queryParam("mapY", lat)
                        .queryParam("radius", (int) radius)
                        .queryParam("contentTypeId", 12)
                        .queryParam("arrange", "E")
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<TourApiResponse<PlaceItemDto>>() {})
                .block();
    }

    public TourApiResponse<PlaceDetailDto> getPlaceDetail(String contentId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/detailCommon2")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("numOfRows", 10)
                        .queryParam("pageNo", 1)
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", "Nadeul")
                        .queryParam("_type", "json")
                        .queryParam("contentId", contentId)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<TourApiResponse<PlaceDetailDto>>() {})
                .block();
    }
}
