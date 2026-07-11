package com.rihyri.NADL.domain.place.controller;

import com.rihyri.NADL.domain.place.dto.PlaceItemDto;
import com.rihyri.NADL.domain.place.service.PlaceService;
import com.rihyri.NADL.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/nearby")
    public ApiResponse<List<PlaceItemDto>> getNearbyPlaces(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5000") double radius
    ) {
        List<PlaceItemDto> places = placeService.getNearbyPlaces(lat, lng, radius);
        return ApiResponse.ok("조회 성공", places);
    }
}
