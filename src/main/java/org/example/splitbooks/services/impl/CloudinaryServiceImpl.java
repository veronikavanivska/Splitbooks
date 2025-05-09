package org.example.splitbooks.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.example.splitbooks.services.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

        private final Cloudinary cloudinary;

        public CloudinaryServiceImpl(Cloudinary cloudinary) {
            this.cloudinary = cloudinary;
        }

        public String uploadAvatar(MultipartFile file) {
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                        "folder", "avatars",
                        "resource_type", "image"
                ));
                return uploadResult.get("secure_url").toString();
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload avatar to Cloudinary", e);
            }
        }

        public void deleteAvatarByUrl(String secureUrl) {
            try {
                String publicId = extractPublicId(secureUrl);
                cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                        "resource_type", "image"
                ));
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete avatar from Cloudinary", e);
            }
        }

        private String extractPublicId(String secureUrl) {
            int lastSlash = secureUrl.lastIndexOf('/');
            int dot = secureUrl.lastIndexOf('.');
            String filename = secureUrl.substring(lastSlash + 1, dot);
            return "avatars/" + filename;
        }


}

