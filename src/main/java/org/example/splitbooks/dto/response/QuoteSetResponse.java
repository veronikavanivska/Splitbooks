package org.example.splitbooks.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class QuoteSetResponse {
    private Long profileId;
    private List<String> quotes;
}
