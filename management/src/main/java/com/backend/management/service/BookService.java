package com.backend.management.service;

import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Book;
import com.backend.management.repository.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private BookRepo bookRepo;

    // lay tat ca cac sach
    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    //lay sach theo id
    public Optional<Book> getBookByBookId(String bookId) {
        return bookRepo.findByBookId(bookId);
    }

    // lay sach theo ten hoac tac gia
    public List<Book> searchBooks(String name, String author) {
        String nameSlug = name != null ? toSlug(name) : null;
        String authorSlug = author != null ? toSlug(author) : null;
        if (nameSlug != null && authorSlug != null) {
            return bookRepo.findAll().stream()
                    .filter(book -> toSlug(book.getName()).equals(nameSlug)
                            && book.getAuthor().stream().anyMatch(a -> toSlug(a).equals(authorSlug)))
                    .collect(Collectors.toList());
        } else if (author != null) {
            return bookRepo.findAll().stream()
                    .filter(book -> book.getAuthor().stream().anyMatch(a -> toSlug(a).equals(authorSlug)))
                    .collect(Collectors.toList());
        } else if (name != null) {
            return bookRepo.findAll().stream()
                    .filter(book -> toSlug(book.getName()).equals(nameSlug))
                    .collect((Collectors.toList()));
        }
        return getAllBooks();
    }

    //them sach
    public Book addBook(Book book) {
        return bookRepo.save(book);
    }

    // lay sach theo id va cap nhat sach
    public Book updateBook(String bookId, Book updatedBook) {
        Book existingBook = bookRepo.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("book not found with id" + bookId));
        if (updatedBook.getBookId() != null) {
            existingBook.setBookId(updatedBook.getBookId());
        }
        if (updatedBook.getName() != null) {
            existingBook.setName(updatedBook.getName());
        }
        if (updatedBook.getAuthor() != null) {
            existingBook.setAuthor(updatedBook.getAuthor());
        }
        if (updatedBook.getDescription() != null) {
            existingBook.setDescription(updatedBook.getDescription());
        }
        if (updatedBook.getPublicationYear() != null) {
            existingBook.setPublicationYear(updatedBook.getPublicationYear());
        }
        if (updatedBook.getBigCategory() != null) {
            existingBook.setCategory(updatedBook.getBigCategory());
        }
        if (updatedBook.getQuantity() != null) {
            existingBook.setQuantity(updatedBook.getQuantity());
        }
        if (updatedBook.getAvailability() != null) {
            existingBook.setAvailability(updatedBook.getAvailability());
        }
        if (updatedBook.getNxb() != null) {
            existingBook.setNxb(updatedBook.getNxb());
        }

        return bookRepo.save(existingBook);

    }


    // xoa sach theo id
    public void deleteBook(String bookId) {
        bookRepo.deleteById(bookId);
    }


    public List<Book> getBooksBySubCategory(String subCategoryName, String bigCategoryName) {
        String subSlug = toSlug(subCategoryName);
        String bigSlug = toSlug(bigCategoryName);

        return bookRepo.findAll().stream()
                .filter(book -> book.getBigCategory() != null &&  // Kiểm tra `null` cho `getBigCategory`
                        book.getBigCategory().stream()
                                .anyMatch(bigCategory -> bigCategory.getName() != null &&  // Kiểm tra `null` cho `getName`
                                        toSlug(bigCategory.getName()).equals(bigSlug) &&
                                        bigCategory.getSmallCategory() != null &&  // Kiểm tra `null` cho `getSmallCategory`
                                        bigCategory.getSmallCategory().stream()
                                                .anyMatch(smallCategory -> toSlug(smallCategory).equals(subSlug))))
                .collect(Collectors.toList());
    }


    private String toSlug(String input) {
        if (input == null) return "";

        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll("/", " ")
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

    //kiem tra sach co san hay hoc
    public boolean isBookAvailable(String bookId) {
        Optional<Book> book = bookRepo.findByBookId(bookId);
        return book.map(Book::getAvailability).orElse(false);
    }


    //dem so luong sach trong kho
    public int getTotalBooksInStock() {
        try {
            List<Map<String, Object>> result = bookRepo.getTotalBooksInStock();

            if (result.isEmpty() || result.get(0).get("totalQuantity") == null) {
                return 0;  // Trả về 0 nếu không có kết quả hoặc không có giá trị tổng số sách
            }

            // Trả về tổng số sách
            return ((Number) result.get(0).get("totalQuantity")).intValue();
        } catch (Exception e) {
            // Log lỗi hoặc xử lý lỗi nếu có
            e.printStackTrace();

            // Có thể trả về một giá trị mặc định (ví dụ: 0) hoặc ném lại exception tùy vào yêu cầu
            throw new RuntimeException("An error occurred while calculating total books in stock.", e);
        }
    }



    public Map<String, Long> getCategoryDistribution() {
        List<Book> books = bookRepo.findAll();
        Map<String, Long> categoryCount = new HashMap<>();

        books.forEach(book -> {
            if (book.getBigCategory() != null) {
                book.getBigCategory().forEach(category -> {
                    String categoryName = category.getName();
                    categoryCount.put(categoryName, categoryCount.getOrDefault(categoryName, 0L) + 1);
                });
            }
        });

        return categoryCount;

    }


}

