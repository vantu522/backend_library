package com.backend.management.controller;

import com.backend.management.model.Book;
import com.backend.management.model.BookCategory;
import com.backend.management.model.PaginatedResponse;
import com.backend.management.service.BookCategoryService;
import com.backend.management.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class BookController {

    // Các service được sử dụng
    @Autowired
    private BookService bookService;

    @Autowired
    private BookCategoryService bookCategoryService;

    // Cấu hình thư mục lưu trữ file
    private final Path fileStorageLocation;
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif"
    );

    public BookController() {
        this.fileStorageLocation = Paths.get("uploads/images")
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục upload", e);
        }
    }

    // Các API quản lý sách
    @GetMapping("/books")
    public ResponseEntity<PaginatedResponse<Book>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Book> books = bookService.getAllBooks(page, size);
            PaginatedResponse<Book> response = PaginatedResponse.of(books);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/books/{bookId}")
    public Optional<Book> getBookByBookId(@PathVariable String bookId) {
        return bookService.getBookByBookId(bookId);
    }

    @PostMapping("/books")
    public Book addBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    @PutMapping("/books/update/{bookId}")
    public ResponseEntity<Book> updateBook(@PathVariable String bookId, @RequestBody Book updatedBook) {
        Book book = bookService.updateBook(bookId, updatedBook);
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/books/delete/{bookId}")
    public void deleteBook(@PathVariable String bookId) {
        bookService.deleteBook(bookId);
    }

    // Các API quản lý danh mục sách
    @GetMapping("/books/categories")
    public ResponseEntity<List<String>> getBigCategories() {
        List<String> bigCategories = bookCategoryService.getAllBigCategories();
        return ResponseEntity.ok(bigCategories);
    }

    @GetMapping("/books/categories/{bigCategorySlug}")
    public List<String> getSmallCategories(@PathVariable String bigCategorySlug) {
        return bookCategoryService.getSmallCategories(bigCategorySlug);
    }

    @GetMapping("/books/categories/{bigCategoryName}/{subCategoryName}/books")
    public ResponseEntity<List<Book>> getBooksBySubCategory(
            @PathVariable String subCategoryName,
            @PathVariable String bigCategoryName) {
        List<Book> books = bookService.getBooksBySubCategory(subCategoryName, bigCategoryName);
        return ResponseEntity.ok(books);
    }

    @PutMapping("/books/update-big-category")
    public ResponseEntity<String> updateBigCategoryName(
            @RequestParam String oldName,
            @RequestParam String newName) {
        bookService.updateBigCategoryName(oldName, newName);
        return ResponseEntity.ok("Thành công");
    }

    @DeleteMapping("/books/delete-big-category")
    public ResponseEntity<String> deleteBigCategory(@RequestParam String bigCategoryName) {
        bookService.deleteBigCategoryName(bigCategoryName);
        return ResponseEntity.ok("Xóa thành công");
    }

    // Các API tìm kiếm và phân tích sách
    @GetMapping("/books/search")
    public List<Book> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author) {
        return bookService.searchBooks(title, author);
    }

    @GetMapping("/books/suggest")
    public ResponseEntity<List<Book>> suggestBooks(@RequestParam String query) {
        return ResponseEntity.ok(bookService.suggestBooks(query));
    }

    @GetMapping("/books/{bookId}/availability")
    public boolean checkAvailability(@PathVariable String bookId) {
        return bookService.isBookAvailable(bookId);
    }

    @GetMapping("/books/total")
    public int getTotalBooksInStock() {
        return bookService.getTotalBooksInStock();
    }

    @GetMapping("/books/category-distribution")
    public Map<String, Long> getCategoryDistribution() {
        return bookService.getCategoryDistribution();
    }

    // Các API quản lý file
    @PostMapping("/files/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            validateFile(file);
            String fileName = generateUniqueFileName(file);
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok()
                    .body(new FileUploadResponse("/images/" + fileName,
                            file.getContentType(),
                            file.getSize()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể upload file: " + e.getMessage());
        }
    }

    @PostMapping("/files/upload-multiple")
    public ResponseEntity<?> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            List<FileUploadResponse> responses = new ArrayList<>();
            for (MultipartFile file : files) {
                validateFile(file);
                String fileName = generateUniqueFileName(file);
                Path targetLocation = this.fileStorageLocation.resolve(fileName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                responses.add(new FileUploadResponse(
                        "/images/" + fileName,
                        file.getContentType(),
                        file.getSize()
                ));
            }
            return ResponseEntity.ok(responses);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể upload file: " + e.getMessage());
        }
    }

    @GetMapping("/files/download/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể tải file: " + e.getMessage());
        }
    }

    @DeleteMapping("/files/delete/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            boolean deleted = Files.deleteIfExists(filePath);

            if (deleted) {
                return ResponseEntity.ok()
                        .body("Xóa file thành công");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể xóa file: " + e.getMessage());
        }
    }

    @GetMapping("/files/list")
    public ResponseEntity<List<FileInfo>> listFiles() {
        try {
            List<FileInfo> fileInfos = Files.list(this.fileStorageLocation)
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try {
                            return new FileInfo(
                                    path.getFileName().toString(),
                                    Files.size(path),
                                    Files.probeContentType(path)
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(fileInfos);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Các phương thức hỗ trợ cho quản lý file
    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File rỗng");
        }

        if (file.getSize() > 5_242_880) {
            throw new IOException("Dung lượng file vượt quá giới hạn 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IOException("Loại file không hợp lệ. Chỉ cho phép JPEG, PNG và GIF");
        }
    }

    private String generateUniqueFileName(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }
}

// Lớp phản hồi cho việc upload file
class FileUploadResponse {
    private String fileUrl;
    private String fileType;
    private long size;

    public FileUploadResponse(String fileUrl, String fileType, long size) {
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.size = size;
    }

    public String getFileUrl() { return fileUrl; }
    public String getFileType() { return fileType; }
    public long getSize() { return size; }
}

// Lớp thông tin file
class FileInfo {
    private String name;
    private long size;
    private String type;

    public FileInfo(String name, long size, String type) {
        this.name = name;
        this.size = size;
        this.type = type;
    }

    public String getName() { return name; }
    public long getSize() { return size; }
    public String getType() { return type; }
}
