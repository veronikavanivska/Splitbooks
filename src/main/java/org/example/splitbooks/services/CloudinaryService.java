package org.example.splitbooks.services;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface CloudinaryService {
    public void deleteAvatarByUrl(String secureUrl);
    public String uploadAvatar(MultipartFile file);
}
