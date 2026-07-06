package com.rihyri.NADL.domain.festival.controller;

import com.rihyri.NADL.domain.festival.service.TourApiService;
import com.rihyri.NADL.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/tourapi")
@RequiredArgsConstructor
public class TourApiTestController {

    private final TourApiService tourApiService;

    @PostMapping("/sync-festivals")
    public ApiResponse<String> syncFestivals() {
        int count = tourApiService.syncFestivals();
        return ApiResponse.ok(count + "건 저장 완료");
    }
}
