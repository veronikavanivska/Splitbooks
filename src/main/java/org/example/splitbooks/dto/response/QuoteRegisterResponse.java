package org.example.splitbooks.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class QuoteRegisterResponse {
    private Long profileId;
    private List<String> quotes;
}
