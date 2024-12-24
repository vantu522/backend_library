package com.backend.management.service;

import com.backend.management.exception.EntityNotFoundException;
import com.backend.management.exception.ImageValidationException;
import com.backend.management.model.Post;
import com.backend.management.repository.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepo postRepo;

    public Post createPost(Post post, MultipartFile img) throws IOException{
        if (img != null) {
            validateAndSetImage(post, img);
        }
        post.setCreatedAt(LocalDateTime.now());
        return postRepo.save(post);
    }

    public List<Post> getPublicPosts() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return postRepo.findByStatus("công khai", sort); // Returns public posts sorted by createdAt
    }

    public Post updatePost(String id, Post postDetails, MultipartFile img) throws IOException {
        // Tìm Post theo ID, nếu không tìm thấy thì ném ngoại lệ
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("POST NOT FOUND WITH ID " + id));

        // Cập nhật các trường thông tin khác
        post.setTitle(postDetails.getTitle());
        post.setAuthor(postDetails.getAuthor());
        post.setContent(postDetails.getContent());
        post.setStatus(postDetails.getStatus());

        // Kiểm tra và xử lý ảnh mới nếu có
        if (img != null) {
            validateAndSetImage(post, img);
        }

        // Lưu lại thay đổi
        return postRepo.save(post);
    }


    public void deletePost(String id) {
        if (id == null || id.isEmpty()) {
            System.err.println("Lỗi: ID không được để trống");
            throw new IllegalArgumentException("ID không được để trống");
        }

        System.out.println("Bắt đầu tìm bài viết với ID: " + id);

        Post post = postRepo.findById(id)
                .orElseThrow(() -> {
                    System.err.println("Lỗi: Không tìm thấy bài viết với ID: " + id);
                    return new EntityNotFoundException("Không tìm thấy bài viết với id: " + id);
                });

        System.out.println("Tìm thấy bài viết: " + post);

        try {
            postRepo.delete(post);
            System.out.println("Đã xóa bài viết với ID: " + id);
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa bài viết với ID: " + id);
            e.printStackTrace(); // In stack trace đầy đủ vào log
            throw new RuntimeException("Đã xảy ra lỗi khi xóa bài viết");
        }
    }

    public void validateAndSetImage(Post post, MultipartFile image) throws IOException {
        validateImage(image);
        byte[] compressedImageBytes = compressImage(image);
        String base64Image = Base64.getEncoder().encodeToString(compressedImageBytes);
        post.setImg(base64Image);
    }
    private void validateImage(MultipartFile image) throws IOException {
        // Kiểm tra null
        if (image == null) {
            throw new ImageValidationException("Image file is required");
        }

        // Kiểm tra kích thước
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (image.getSize() > maxSize) {
            throw new ImageValidationException("Image size must be less than 5MB");
        }

        // Kiểm tra định dạng
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ImageValidationException("File must be an image");
        }

        List<String> allowedTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");
        if (!allowedTypes.contains(contentType)) {
            throw new ImageValidationException("Only JPEG, PNG and GIF images are allowed");
        }

        // Kiểm tra kích thước ảnh
        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
        if (bufferedImage == null) {
            throw new ImageValidationException("Invalid image file");
        }

        int maxDimension = 2000;
        if (bufferedImage.getWidth() > maxDimension || bufferedImage.getHeight() > maxDimension) {
            throw new ImageValidationException(
                    "Image dimensions must be less than " + maxDimension + "x" + maxDimension
            );
        }
    }

    private byte[] compressImage(MultipartFile image) throws IOException {
        BufferedImage originalImage = ImageIO.read(image.getInputStream());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IOException("No writer available for JPEG format");
        }

        ImageWriter writer = writers.next();
        ImageWriteParam params = writer.getDefaultWriteParam();

        if (params.canWriteCompressed()) {
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(0.7f); // 70% quality
        }

        ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
        writer.setOutput(ios);
        writer.write(null, new IIOImage(originalImage, null, null), params);

        writer.dispose();
        ios.close();
        return outputStream.toByteArray();
    }
}