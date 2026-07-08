package com.rihyri.NADL.domain.place.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "places")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String externalId;
    private String sourceApi;

    private String name;
    private String description;
    private String address;

    private Double latitude;
    private Double longitude;

    private String category;
    private String indoorOutdoorType;
    private String imageUrl;

    private Long viewCount = 0L;
    private Long bookmarkCount = 0L;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Place(String externalId, String sourceApi, String name, String description,
                 String address, Double latitude, Double longitude,
                 String category, String indoorOutdoorType, String imageUrl) {
        this.externalId = externalId;
        this.sourceApi = sourceApi;
        this.name = name;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.indoorOutdoorType = indoorOutdoorType;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
