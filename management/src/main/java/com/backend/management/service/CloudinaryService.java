package com.backend.management.service;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "posts", // Thư mục lưu ảnh trên Cloudinary
                            "resource_type", "auto"
                    )
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new IOException("Error uploading image to Cloudinary", e);
        }
    }
}
