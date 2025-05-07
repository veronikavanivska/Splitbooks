package org.example.splitbooks.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl {

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
    }

