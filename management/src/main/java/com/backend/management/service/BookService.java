package com.backend.management.service;

import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Book;
import com.backend.management.repository.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
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
    public List<Book> searchBooks(String name, String author){
        String nameSlug = name != null ? toSlug(name): null;
        String authorSlug = author != null ? toSlug(author): null;
        if(nameSlug != null && authorSlug != null ){
            return bookRepo.findAll().stream()
                    .filter(book ->toSlug(book.getName()).equals(nameSlug)
                            && book.getAuthor().stream().anyMatch(a -> toSlug(a).equals(authorSlug)))
                    .collect(Collectors.toList());
        } else if (author != null) {
            return bookRepo.findAll().stream()
                    .filter(book -> book.getAuthor().stream().anyMatch(a->toSlug(a).equals(authorSlug)))
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
   public Book updateBook(String BookId, Book updatedBook){
        Book existingBook = bookRepo.findByBookId(BookId)
                .orElseThrow(() -> new ResourceNotFoundException("book not found with id"+ BookId));
        if(updatedBook.getbookId() != null){
            existingBook.setbookId(updatedBook.getbookId());
        }
        if(updatedBook.getName() != null){
            existingBook.setName(updatedBook.getName());
        }
        if(updatedBook.getAuthor() != null){
            existingBook.setAuthor(updatedBook.getAuthor());
        }
        if(updatedBook.getDescription() != null){
            existingBook.setDescription(updatedBook.getDescription());
       }
        if(updatedBook.getPublicationYear() != null){
            existingBook.setPublicationYear(updatedBook.getPublicationYear());
        }
        if (updatedBook.getBigCategory() != null){
            existingBook.setCategory(updatedBook.getBigCategory());
        }
        if(updatedBook.getQuantity() != null){
            existingBook.setQuantity(updatedBook.getQuantity());
        }
        if (updatedBook.getAvailability() != null){
            existingBook.setAvailability(updatedBook.getAvailability());
        }
        if(updatedBook.getNxb() != null){
            existingBook.setNxb(updatedBook.getNxb());
        }

        return bookRepo.save(existingBook);

   }



    // xoa sach theo id
    public void deleteBook(String BookID) {
        bookRepo.deleteById(BookID);
    }


    public List<Book> getBooksBySubCategory(String subCategoryName, String bigCategoryName){
        String subSlug = toSlug(subCategoryName);
        String bigSlug = toSlug(bigCategoryName);
        return bookRepo.findAll().stream()
                .filter(book -> book.getBigCategory().stream()
                        .anyMatch(bigCategory -> bigCategory.getSmallCategory().stream()
                                .anyMatch(smallCategoty -> toSlug(smallCategoty).equals(subSlug))))
                .collect(Collectors.toList());
    }


    private String toSlug(String input) {
        if (input == null) return "";

        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll("/"," ")
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

    //kiem tra sach co san hay hoc
    public boolean isBookAvailable(String BookId){
        Optional<Book> book = bookRepo.findByBookId(BookId);
        return book.map(Book::getAvailability).orElse(false);
    }

}

