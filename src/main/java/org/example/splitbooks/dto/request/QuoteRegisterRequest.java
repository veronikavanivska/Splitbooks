package org.example.splitbooks.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class QuoteRegisterRequest {
    private List<String> quotes;
}
