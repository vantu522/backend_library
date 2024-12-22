package com.backend.management.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class ImgUtils {
    private final Path ImgUtils;

    public ImgUtils() {
        // Set up upload directory in the project
        this.ImgUtils = Paths.get("uploads/images")
                .toAbsolutePath().normalize();

        // Create directory if it doesn't exist
        try {
            Files.createDirectories(this.ImgUtils);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory");
        }
    }

    public String saveImage(MultipartFile file) throws IOException {
        // Validate file
        if (file.isEmpty()) {
            throw new IOException("Cannot save empty file");
        }

        // Check if it's an image
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("File must be an image");
        }

        // Create unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;

        // Save the file
        Path destinationPath = this.ImgUtils.resolve(newFilename);
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        // Return the path that will be stored in database
        return "/images/" + newFilename;
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl != null && imageUrl.startsWith("/images/")) {
            try {
                String filename = imageUrl.substring("/images/".length());
                Path filePath = this.ImgUtils.resolve(filename);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log error but don't throw exception
                System.err.println("Error deleting file: " + e.getMessage());
            }
        }
    }
}