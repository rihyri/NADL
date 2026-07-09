package com.rihyri.NADL.domain.place.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceItemDto {

    @JsonProperty("contentid")
    private String contentId;

    private String title;

    @JsonProperty("addr1")
    private String address;

    private String mapx;
    private String mapy;

    @JsonProperty("firstimage")
    private String imageUrl;

    private String cat3;

    private String dist;    // 기준점으로부터의 거리 (미터)
}
