package com.collector.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        Map result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", "collector",
                    "resource_type", "image",
                    "width", 800,
                    "height", 600,
                    "crop", "limit",
                    "quality", "auto",
                    "fetch_format", "auto"
                )
            );
        String url = (String) result.get("secure_url");
        log.info("Image uploadée sur Cloudinary : {}", url);
        return url;
    }

    public void deleteImage(String imageUrl) throws IOException {
        String publicId = extractPublicId(imageUrl);
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        log.info("Image supprimée de Cloudinary : {}", publicId);
    }

    private String extractPublicId(String imageUrl) {
        String[] parts = imageUrl.split("/");
        String filename = parts[parts.length - 1];
        return "collector/" + filename.split("\\.")[0];
    }
}