package com.rihyri.NADL.global.common;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TourApiResponse<T> {

    private Response<T> response;

    @Getter
    @NoArgsConstructor
    public static class Response<T> {
        private Body<T> body;
    }

    @Getter
    @NoArgsConstructor
    public static class Body<T> {
        private Items<T> items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;
    }

    @Getter
    @NoArgsConstructor
    public static class Items<T> {
        private List<T> item;
    }
}
