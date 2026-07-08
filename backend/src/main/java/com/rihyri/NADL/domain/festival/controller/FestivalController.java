package com.rihyri.NADL.domain.festival.controller;

import com.rihyri.NADL.domain.festival.entity.Festival;
import com.rihyri.NADL.domain.festival.repository.FestivalRepository;
import com.rihyri.NADL.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/festivals")
@RequiredArgsConstructor
public class FestivalController {

    private final FestivalRepository festivalRepository;

    @GetMapping
    public ApiResponse<List<Festival>> getFestivals() {
        List<Festival> festivals = festivalRepository.findAll();
        return ApiResponse.ok("조회 성공", festivals);
    }

    // 5km내 축제 검색
    @GetMapping("/nearby")
    public ApiResponse<List<Festival>> getNearbyFestivals(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5000") double radius
    ) {
        List<Festival> festivals = festivalRepository.findNearby(lat, lng, radius);
        return ApiResponse.ok("조회 성공", festivals);
    }
}