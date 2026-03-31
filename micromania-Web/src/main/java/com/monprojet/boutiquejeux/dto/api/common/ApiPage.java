package com.monprojet.boutiquejeux.dto.api.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiPage<T>(
        List<T> content,
        int number,
        int size,
        int totalPages,
        long totalElements,
        boolean first,
        boolean last,
        boolean empty
) {}