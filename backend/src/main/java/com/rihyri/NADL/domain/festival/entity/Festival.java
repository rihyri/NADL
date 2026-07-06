package com.rihyri.NADL.domain.festival.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "festivals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String externalId;  // TourAPI ContentId
    private String sourceApi;   // "TourAPI"

    private String name;
    private String description;
    private String address;

    private Double latitude;
    private Double longitude;

    private LocalDate startDate;
    private LocalDate endDate;
    private String eventTime;

    private String category;
    private String imageUrl;

    private Long viewCount = 0L;
    private Long bookmarkCount = 0L;

    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Festival(String externalId, String sourceApi, String name, String description,
                    String address, Double latitude, Double longitude,
                    LocalDate startDate, LocalDate endDate, String eventTime,
                    String category, String imageUrl) {
        this.externalId = externalId;
        this.sourceApi = sourceApi;
        this.name = name;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventTime = eventTime;
        this.category = category;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
