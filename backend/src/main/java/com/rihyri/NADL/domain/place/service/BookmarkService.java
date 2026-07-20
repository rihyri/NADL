package com.rihyri.NADL.domain.place.service;

import com.rihyri.NADL.domain.place.dto.PlaceDetailDto;
import com.rihyri.NADL.domain.place.entity.Bookmark;
import com.rihyri.NADL.domain.place.entity.Place;
import com.rihyri.NADL.domain.place.repository.BookmarkRepository;
import com.rihyri.NADL.domain.place.repository.PlaceRepository;
import com.rihyri.NADL.domain.user.entity.User;
import com.rihyri.NADL.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookmarkService {

    private static final String SOURCE_API = "TOUR_API";

    private final PlaceService placeService;
    private final PlaceRepository placeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    @Transactional
    public void bookmarkPlace(String loginId, String contentId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));

        Place place = findOrCreatePlace(contentId);

        if (bookmarkRepository.existsByUserIdAndPlaceId(user.getId(), place.getId())) {
            throw new IllegalStateException("이미 북마크한 장소입니다.");
        }

        bookmarkRepository.save(Bookmark.builder()
                .user(user)
                .place(place)
                .build());

        placeRepository.incrementBookmarkCount(place.getId());
    }

    @Transactional
    public void unbookmarkPlace(String loginId, Long placeId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));

        Bookmark bookmark = bookmarkRepository.findByUserIdAndPlaceId(user.getId(), placeId)
                .orElseThrow(() -> new IllegalStateException("북마크한 적 없는 장소입니다."));

        bookmarkRepository.delete(bookmark);
        placeRepository.decrementBookmarkCount(placeId);
    }

    private Place findOrCreatePlace(String contentId) {
        Optional<Place> existing = placeRepository.findByExternalIdAndSourceApi(contentId, SOURCE_API);
        if (existing.isPresent()) {
            return existing.get();
        }

        PlaceDetailDto detail = placeService.getPlaceDetail(contentId);
        Place newPlace = Place.fromDetailDto(detail, SOURCE_API);

        try {
            return placeRepository.save(newPlace);
        } catch (DataIntegrityViolationException e) {
            log.warn("Place 저장 중 중복 발생, 재조회 - contentId: {}", contentId);
            return placeRepository.findByExternalIdAndSourceApi(contentId, SOURCE_API)
                    .orElseThrow(() -> new IllegalStateException("Place 저장/조회 실패"));
        }
    }
}
