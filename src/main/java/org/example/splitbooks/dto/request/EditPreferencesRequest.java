package org.example.splitbooks.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class EditPreferencesRequest {
    private List<Long> preferredLanguages;
    private List<Long> preferredFormats;
}
