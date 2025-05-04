package org.example.splitbooks.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class GoogleBooksResponse {

    private String kind;
    private int totalItems;
    private List<Item> items;


    @Data
    public static class Item {
        private String kind;
        private String selfLink;
        private VolumeInfo volumeInfo;
    }


    @Data
    public static class VolumeInfo {
        private String title;
        private List<String> authors;
        private String publisher;
        private String publishedDate;
        private String description;
        private String isbn;
        private ImageLinks imageLinks;
    }

    @Data
    public static class ImageLinks {
        private String smallThumbnail;
        private String thumbnail;
    }
}
