package com.rihyri.NADL.domain.festival.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FestivalItemDto {

    @JsonProperty("contentid")
    private String contentId;

    private String title;

    @JsonProperty("addr1")
    private String address;

    @JsonProperty("eventstartdate")
    private String eventStartDate;

    @JsonProperty("eventenddate")
    private String eventEndDate;

    private String mapx;    // 경도
    private String mapy;    // 위도

    @JsonProperty("firstimage")
    private String imageUrl;

    private String cat3;    // 카테고리
}
