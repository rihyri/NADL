package com.rihyri.NADL.domain.place.controller;

import com.rihyri.NADL.domain.place.dto.PlaceDetailDto;
import com.rihyri.NADL.domain.place.dto.PlaceItemDto;
import com.rihyri.NADL.domain.place.service.BookmarkService;
import com.rihyri.NADL.domain.place.service.PlaceService;
import com.rihyri.NADL.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final BookmarkService bookmarkService;

    @GetMapping("/nearby")
    public ApiResponse<List<PlaceItemDto>> getNearbyPlaces(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5000") double radius
    ) {
        List<PlaceItemDto> places = placeService.getNearbyPlaces(lat, lng, radius);
        return ApiResponse.ok("조회 성공", places);
    }

    @GetMapping("/{contentId}")
    public ApiResponse<PlaceDetailDto> getPlaceDetail(@PathVariable String contentId) {
        PlaceDetailDto detail = placeService.getPlaceDetail(contentId);
        return ApiResponse.ok("상세 조회 성공", detail);
    }

    @GetMapping("/category-code")
    public String getCategoryCode(
            @RequestParam(required = false) String cat1,
            @RequestParam(required = false) String cat2
    ) {
        return placeService.getCategoryCodeRawForDebug(cat1, cat2);
    }

    @GetMapping("/category-code/all")
    public String getAllCategoryCodes() {
        return placeService.getAllCategoryCodesForDebug();
    }

    @PostMapping("/{contentId}/bookmark")
    public ResponseEntity<Void> bookmarkPlace(
            @PathVariable String contentId, @AuthenticationPrincipal UserDetails userDetails) {
        bookmarkService.bookmarkPlace(userDetails.getUsername(), contentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{placeId}/bookmark")
    public ResponseEntity<Void> unbookmarkPlace(@PathVariable Long placeId, @AuthenticationPrincipal UserDetails userDetails) {
        bookmarkService.unbookmarkPlace(userDetails.getUsername(), placeId);
        return ResponseEntity.noContent().build();
    }
}
