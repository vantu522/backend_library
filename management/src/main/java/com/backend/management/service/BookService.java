package com.backend.management.service;

import com.backend.management.exception.ImageValidationException;
import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Book;
import com.backend.management.model.CategoryCount;
import com.backend.management.repository.BookRepo;
import com.backend.management.utils.SlugUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Query;


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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    // lay tat ca cac sach
    public Page<Book> getAllBooks(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return bookRepo.findAll(pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching books: " + e.getMessage());
        }
    }



    // lay sach theo id
    public Optional<Book> getBookByBookId(String bookId) {
        return bookRepo.findByBookId(bookId);
    }

    // them sach
    public Book addBook(Book book, MultipartFile image) throws IOException {
        if (image != null) {
            validateAndSetImage(book, image);
        }
        return bookRepo.save(book);
    }

    // lay sach theo id va cap nhat sach
    public Book updateBook(String bookId, Book updatedBook, MultipartFile image) throws IOException {
        Book existingBook = bookRepo.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        if (updatedBook.getTitle() != null) existingBook.setTitle(updatedBook.getTitle());
        if (updatedBook.getDescription() != null) existingBook.setDescription(updatedBook.getDescription());
        if (updatedBook.getAuthor() != null) existingBook.setAuthor(updatedBook.getAuthor());
        if (updatedBook.getPublicationYear() != null) existingBook.setPublicationYear(updatedBook.getPublicationYear());
        if (updatedBook.getBigCategory() != null) existingBook.setCategory(updatedBook.getBigCategory());
        if (updatedBook.getQuantity() != null) existingBook.setQuantity(updatedBook.getQuantity());
        if (updatedBook.getAvailability() != null) existingBook.setAvailability(updatedBook.getAvailability());
        if (updatedBook.getNxb() != null) existingBook.setNxb(updatedBook.getNxb());

        if (image != null) {
            validateAndSetImage(existingBook, image);
        }

        return bookRepo.save(existingBook);
    }
    public String getBookImage(String bookId) {
        Book book = bookRepo.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        return book.getImg();
    }

    // xoa sach theo id
    public void deleteBook(String bookId) {
        bookRepo.deleteById(bookId);
    }


    // kiem tra sach co san hay hoc
    public boolean isBookAvailable(String bookId) {
        Optional<Book> book = bookRepo.findByBookId(bookId);
        return book.map(Book::getAvailability).orElse(false);
    }

    // dem tong so sach dang co
    public int getTotalBooksInStock() {
        List<Book> books = bookRepo.findAllQuantities();
        return books.stream()
                .mapToInt(Book::getQuantity)
                .sum();
    }

    // dem so sach cua moi the loai
    public Map<String, Long> getCategoryDistribution() {
        List<CategoryCount> results = bookRepo.getCategoryDistribution();
        Map<String, Long> categoryCount = new HashMap<>();

        for (CategoryCount result : results) {
            if (result.getId() != null) {
                categoryCount.put(result.getId(), result.getCount());
            }
        }

        return categoryCount;
    }

    public List<Book> searchBooks(String title, String author) {
        Criteria criteria = new Criteria();

        if (title != null) {
            criteria.and("title").regex(".*" + title + ".*", "i");
        }

        if (author != null) {
            criteria.and("author").regex(".*" + author + ".*", "i");
        }

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Book.class);
    }


    public List<Book> suggestBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Criteria criteria = new Criteria().orOperator(
                Criteria.where("title").regex("^" + query, "i"),
                Criteria.where("author").regex("^" + query, "i")
        );

        Query querys = new Query(criteria).limit(10);
        return mongoTemplate.find(querys, Book.class);
    }

    public List<Book> getBooksBySubCategory(String subCategoryName, String bigCategoryName) {
        String subSlug = SlugUtil.toSlug(subCategoryName);
        String bigSlug = SlugUtil.toSlug(bigCategoryName);

        return bookRepo.findAll().stream()
                .filter(book -> book.getBigCategory().stream()
                        .anyMatch(bigCategory -> {
                            String currentBigSlug = SlugUtil.toSlug(bigCategory.getName());
                            if (!currentBigSlug.equals(bigSlug)) {
                                return false;
                            }

                            return bigCategory.getSmallCategory().stream()
                                    .anyMatch(smallCategory -> {
                                        String[] subCategories = smallCategory.split("/");

                                        // Chuyển từng phần tử thành slug và so sánh
                                        for (String subCategory : subCategories) {
                                            String currentSubSlug = SlugUtil.toSlug(subCategory.trim());
                                            // So sánh một phần của slug
                                            if (subSlug.contains(currentSubSlug) || currentSubSlug.contains(subSlug)) {
                                                return true;
                                            }
                                        }
                                        return false;
                                    });
                        }))
                .collect(Collectors.toList());
    }



    public void updateBigCategoryName(String oldName, String newName){

        Query query = new Query(Criteria.where("bigCategory.name").is(oldName));

        Update update = new Update().set("bigCategory.$.name", newName);

        // Thực hiện cập nhật trên tất cả sách phù hợp
        mongoTemplate.updateMulti(query, update, Book.class);
    }

    // xoa ten the loai lon
    public void deleteBigCategoryName(String bigCategoryName){
        Query query = new Query(Criteria.where("bigCategory.name").is(bigCategoryName));
        mongoTemplate.remove(query,Book.class);
    }

    public void validateAndSetImage(Book book, MultipartFile image) throws IOException {
        validateImage(image);
        byte[] compressedImageBytes = compressImage(image);
        String base64Image = Base64.getEncoder().encodeToString(compressedImageBytes);
        book.setImg(base64Image);
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

//    public void updateSmallCategory(String bigCategoryName, String oldSmallCategoryName, String newSmallCategoryName) {
//        // Tạo truy vấn tìm các document phù hợp
//        Query query = new Query(Criteria.where("bigCategory")
//                .elemMatch(Criteria.where("name").is(bigCategoryName)
//                        .and("smallCategory").is(oldSmallCategoryName)));
//
//        // Tạo update để thay đổi giá trị
//        Update update = new Update()
//                .set("bigCategory.$[elem].smallCategory.$[subelem]", newSmallCategoryName);
//
//        // Thực hiện update nhiều document với collection và options cụ thể
//        mongoTemplate.updateMulti(
//                query,
//                update,
//                Book.class,
//                mongoTemplate.getCollectionName(Book.class)
//        );
//    }

}
