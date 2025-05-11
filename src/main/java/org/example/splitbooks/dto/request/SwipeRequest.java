package org.example.splitbooks.dto.request;
import lombok.Data;

@Data
public class SwipeRequest {
        private Long targetProfileId;
        private boolean liked;
}
