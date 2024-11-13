package com.backend.management.service;

import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Book;
import com.backend.management.model.Member;
import com.backend.management.model.Transaction;
import com.backend.management.model.TransactionHistory;
import com.backend.management.repository.BookRepo;
import com.backend.management.repository.MemberRepo;
import com.backend.management.repository.TransactionHistoryRepo;
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
    @Autowired
    private  TransactionHistoryRepo transactionHistoryRepo;

  //  private Transaction transaction;
    private Member member;

    // Phương thức mượn sách
    public  String  borrowBook(String memberId, String bookId) {
        Optional<Member> memberOpt = memberRepo.findById(memberId);
        Optional<Book> bookOpt = bookRepo.findById(bookId);

        if (memberOpt.isEmpty() || bookOpt.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy thành viên hoặc sách");
        }

        Book book = bookOpt.get();
        Member member = memberOpt.get();

        if (book.getQuantity() == 0) {
            return "Sách hiện không có sẵn";
        }

        if (member.getBooksBorrowed() == 5) {
            return "Không thể mượn quá năm quyển sách";
        }

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plus(14, ChronoUnit.DAYS);

        // Cập nhật số lượng sách và số lượng sách đã mượn của thành viên
        book.setQuantity(book.getQuantity() - 1);
        member.setBooksBorrowed(member.getBooksBorrowed() + 1);

        if (book.getQuantity() == 0) {
            book.setAvailability(false);
        }

        // Thêm một chuỗi mô tả giao dịch vào danh sách các giao dịch của thành viên
        String transactionDescription = "Mượn sách: " + book.getName() + ", Hạn trả: " + dueDate;
        member.getTransactions().add(transactionDescription);  // Thêm vào List<String>
        TransactionHistory history = new TransactionHistory();
        history.setMemberId(memberId);
        history.setMemberName(member.getName());  // Lưu tên người mượn
        history.setBookID(bookId);
        history.setTransactionType("mượn");
        history.setTransactionDate(borrowDate);
        history.setDueDate(dueDate);
        history.setDescription("Mượn sách: " + book.getName() + ", Hạn trả: " + dueDate);

        // Lưu giao dịch vào cơ sở dữ liệu
        transactionHistoryRepo.save(history);

        // Lưu thay đổi vào cơ sở dữ liệu
        bookRepo.save(book);
        memberRepo.save(member);

        return "Mượn sách thành công. Hạn trả là " + dueDate;
    }


    // Phương thức trả sách
    public String returnBook(String memberId, String bookId) {
        Optional<Member> memberOpt = memberRepo.findByMemberId(memberId);
        Optional<Book> bookOpt = bookRepo.findByBookId(bookId);

        if (memberOpt.isEmpty() || bookOpt.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy thành viên hoặc sách");
        }

        Member member = memberOpt.get();
        Book book = bookOpt.get();

        // Tìm và xoá giao dịch trả sách khỏi danh sách của thành viên
        boolean returned = member.getTransactions().removeIf(transaction -> transaction.contains(bookId));

        if (!returned) {
            return "Thành viên chưa mượn sách này";
        }

        // Tăng số lượng sách lên và cập nhật trạng thái availability
        book.setQuantity(book.getQuantity() + 1);
        if (book.getQuantity() > 0) {
            book.setAvailability(true);
        }

        // Tạo bản ghi giao dịch trả sách
        TransactionHistory history = new TransactionHistory();
        history.setMemberId(memberId);
        history.setMemberName(member.getName());
        history.setBookID(bookId);
        history.setTransactionType("trả");
        history.setTransactionDate(LocalDate.now());  // Ngày hiện tại
        history.setDueDate(null);  // Không có ngày hạn khi trả sách
        history.setDescription("Trả sách: " + book.getName());

        // Lưu giao dịch vào cơ sở dữ liệu
        transactionHistoryRepo.save(history);  // Lưu TransactionHistory vào DB
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

        // Kiểm tra xem thành viên đã mượn cuốn sách này chưa
        if (member.getTransactions().contains(bookId)) {
            // Tìm sách từ bookRepo
            Optional<Book> bookOpt = bookRepo.findById(bookId);
            if (bookOpt.isEmpty()) {
                return "Không tìm thấy sách này";
            }

            Book book = bookOpt.get();

            // Gia hạn ngày trả sách
            LocalDate newDueDate = LocalDate.now().plus(7, ChronoUnit.DAYS);

            // Tạo lịch sử gia hạn
            TransactionHistory history = new TransactionHistory();
            history.setMemberId(memberId);
            history.setMemberName(member.getName());
            history.setBookID(bookId);
            history.setTransactionType("gia hạn");  // Thay đổi thành "gia hạn"
            history.setDueDate(newDueDate);  // Hạn mới
            history.setDescription("Gia hạn sách: " + book.getName() + ", Hạn mới: " + newDueDate);

            // Lưu giao dịch vào cơ sở dữ liệu
            transactionHistoryRepo.save(history);

            // Trả về thông báo thành công
            return "Gia hạn sách thành công. Hạn mới là " + newDueDate;
        } else {
            return "Thành viên chưa mượn sách này";
        }
    }


}