package com.backend.management.service;

import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Book;
import com.backend.management.model.Member;
import com.backend.management.model.Transaction;
import com.backend.management.repository.BookRepo;
import com.backend.management.repository.MemberRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private MemberRepo memberRepo;


    private Member member;

    // Phương thức mượn sách
    public String borrowBook(String memberId, String bookId) {
        Optional<Member> memberOpt = memberRepo.findById(memberId);
        Optional<Book> bookOpt = bookRepo.findById(bookId);

        if (memberOpt.isEmpty() || bookOpt.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy thành viên hoặc sách");
        }

        Book book = bookOpt.get();
        if (book.getQuality() == 0) {
            return "Sách hiện không có sẵn";
        }
        if(member.getBooksBorrowed() == 5) {
            return "Không thể mượn quá năm quyển sách";
        }

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plus(14, ChronoUnit.DAYS);

        book.setQuality(book.getQuality() - 1);
        member.setBooksBorrowed(member.getBooksBorrowed()+1);
        if (book.getQuality() == 0) {
            book.setAvailability(false);
        }

        memberOpt.get().getTransactions().add(new Transaction(book, borrowDate, dueDate));
        bookRepo.save(book);
        memberRepo.save(memberOpt.get());

        return "Mượn sách thành công. Hạn trả là " + dueDate;
    }

    // Phương thức trả sách
    public String returnBook(String memberId, String bookId) {
        Optional<Member> memberOpt = memberRepo.findById(memberId);
        Optional<Book> bookOpt = bookRepo.findById(bookId);

        if (memberOpt.isEmpty() || bookOpt.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy thành viên hoặc sách");
        }

        Member member = memberOpt.get();
        Book book = bookOpt.get();

        // Kiểm tra và xoá giao dịch mượn sách trong danh sách sách đã mượn của thành viên
        boolean returned = member.getTransactions().removeIf(transaction -> transaction.getBook().getIdBook().equals(bookId));

        if (!returned) {
            return "Thành viên chưa mượn sách này";
        }

        // Tăng số lượng sách lên 1 và cập nhật trạng thái available nếu cần
        book.setQuality(book.getQuality() + 1);
        if (book.getQuality() > 0) {
            book.setAvailability(true);
        }

        bookRepo.save(book);
        memberRepo.save(member);

        return "Trả sách thành công";
    }

    public String renewBook(String memberId, String bookId) {
        Optional<Member> memberOpt = memberRepo.findById(memberId);

        if (memberOpt.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy thành viên");
        }

        Member member = memberOpt.get();

        // Tìm giao dịch mượn sách trong danh sách sách đã mượn của thành viên
        for (Transaction transaction : member.getTransactions()) {
            if (transaction.getBook().getIdBook().equals(bookId)) {
                LocalDate newDueDate = transaction.getDueDate().plus(7, ChronoUnit.DAYS);
                transaction.setDueDate(newDueDate);
                memberRepo.save(member);
                return "Gia hạn sách thành công. Hạn mới là " + newDueDate;
            }
        }

        return "Thành viên chưa mượn sách này";
    }
}
