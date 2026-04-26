package com.monprojet.boutiquejeux.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PageDto<T> {
    public List<T> content;
    public int totalElements;
    public int totalPages;
    public int size;
    public int number;

    public List<T> content() {
        return content != null ? content : List.of();
    }
}
