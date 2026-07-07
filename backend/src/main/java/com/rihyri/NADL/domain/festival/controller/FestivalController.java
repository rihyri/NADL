package com.rihyri.NADL.domain.festival.controller;

import com.rihyri.NADL.dto.ApiResponse;
import com.rihyri.NADL.entity.Festival;
import com.rihyri.NADL.repository.FestivalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
