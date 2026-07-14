package com.rihyri.NADL.domain.place.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rihyri.NADL.domain.place.dto.PlaceDetailDto;
import com.rihyri.NADL.domain.place.dto.PlaceItemDto;
import com.rihyri.NADL.global.common.TourApiClient;
import com.rihyri.NADL.global.common.TourApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {

    private final TourApiClient tourApiClient;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String PLACE_CACHE_PREFIX = "tour:search:";
    private static final Duration PLACE_CACHE_TTL = Duration.ofHours(12);

    private static final String PLACE_DETAIL_CACHE_PREFIX = "place:detail:";
    private static final Duration PLACE_DETAIL_CACHE_TTL = Duration.ofHours(24);

    public List<PlaceItemDto> getNearbyPlaces(double lat, double lng, double radius) {
        String cacheKey = buildPlaceCacheKey(lat, lng, radius);

        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("캐시 HIT - key: {}", cacheKey);
            return deserializePlaces(cached);
        }

        log.info("캐시 MISS - TourAPI 호출, key: {}", cacheKey);
        TourApiResponse<PlaceItemDto> response = tourApiClient.searchNearbyPlaces(lat, lng, radius);
        List<PlaceItemDto> places = response.getResponse().getBody().getItems().getItem();

        redisTemplate.opsForValue().set(cacheKey, serializePlaces(places), PLACE_CACHE_TTL);

        return places;
    }

    private String buildPlaceCacheKey(double lat, double lng, double radius) {
        return PLACE_CACHE_PREFIX + lat + ":" + lng + ":" + (int) radius;
    }

    private String serializePlaces(List<PlaceItemDto> places) {
        try {
            return objectMapper.writeValueAsString(places);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("장소 데이터 직렬화 실패", e);
        }
    }

    private List<PlaceItemDto> deserializePlaces(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<PlaceItemDto>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("장소 데이터 역직렬화 실패", e);
        }
    }

    // 상세(detail) 캐싱
    public PlaceDetailDto getPlaceDetail(String contentId) {
        String cacheKey = PLACE_DETAIL_CACHE_PREFIX + contentId;

        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("장소 상세 캐시 HIT - key: {}", cacheKey);
            return deserializeDetail(cached);
        }

        log.info("장소 상세 캐시 MISS - TourAPI 호출, contentId: {}", contentId);
        TourApiResponse<PlaceDetailDto> response = tourApiClient.getPlaceDetail(contentId);
        PlaceDetailDto detail = response.getResponse().getBody().getItems().getItem().get(0);

        redisTemplate.opsForValue().set(cacheKey, serializeDetail(detail), PLACE_DETAIL_CACHE_TTL);
        return detail;
    }

    private String serializeDetail(PlaceDetailDto detail) {
        try {
            return objectMapper.writeValueAsString(detail);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("장소 상세 직렬화 실패", e);
        }
    }

    private PlaceDetailDto deserializeDetail(String json) {
        try {
            return objectMapper.readValue(json, PlaceDetailDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("장소 상세 역직렬화 실패", e);
        }
    }
}