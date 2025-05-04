package org.example.splitbooks.dto.request;

import lombok.Data;
import org.example.splitbooks.entity.SearchType;

@Data
public class BooksSearchRequest {
    private SearchType searchType = SearchType.BY_TITLE;
    private String searchQuery;
}
