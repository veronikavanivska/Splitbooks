package org.example.splitbooks.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.splitbooks.entity.ProfileType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortProfileResponse {
        private Long id;
        private String username;
        private String avatarUrl;
        private ProfileType type;


}
