package org.example.splitbooks.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class EditGenresRequest {
    private List<Long> selectedGenres;
}
