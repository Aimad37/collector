package com.collector.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        // Permet de retourner le mock uploader quand cloudinary.uploader() est appelé
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void uploadImage_shouldReturnSecureUrl_whenUploadIsSuccessful() throws IOException {
        // GIVEN
        byte[] fileBytes = "test image content".getBytes();
        Map<String, Object> mockResponse = Map.of("secure_url", "https://res.cloudinary.com/demo/image/upload/v12345/collector/image1.jpg");
        
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        when(uploader.upload(eq(fileBytes), any(Map.class))).thenReturn(mockResponse);

        // WHEN
        String resultUrl = cloudinaryService.uploadImage(multipartFile);

        // THEN
        assertNotNull(resultUrl);
        assertEquals("https://res.cloudinary.com/demo/image/upload/v12345/collector/image1.jpg", resultUrl);
        verify(uploader, times(1)).upload(eq(fileBytes), any(Map.class));
    }

    @Test
    void deleteImage_shouldCallDestroyWithCorrectPublicId() throws IOException {
        // GIVEN
        String imageUrl = "https://res.cloudinary.com/demo/image/upload/v12345/collector/mon_image_sneaker.jpg";
        String expectedPublicId = "collector/mon_image_sneaker";
        
        when(uploader.destroy(eq(expectedPublicId), any(Map.class))).thenReturn(Map.of("result", "ok"));

        // WHEN
        cloudinaryService.deleteImage(imageUrl);

        // THEN
        verify(uploader, times(1)).destroy(eq(expectedPublicId), any(Map.class));
    }
}