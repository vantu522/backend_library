package com.backend.management.service;

import com.backend.management.model.Book;
import com.backend.management.model.Member;
//import com.backend.management.model.Transaction;
import com.backend.management.model.TransactionHistory;
import com.backend.management.repository.BookRepo;
import com.backend.management.repository.MemberRepo;
import com.backend.management.repository.TransactionHistoryRepo;
import com.mongodb.lang.Nullable;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.html.HTMLImageElement;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class TransactionService {

    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private MemberRepo memberRepo;
    @Autowired
    private TransactionHistoryRepo transactionHistoryRepo;

    @Autowired
    private EmailService emailService;
    private Book book;

    //  private Transaction transaction;
    private Member member;


    // Phương thức mượn sách
    public String borrowBook(String name, String title, String phoneNumber) {
        Member member = null;

        // Tìm thành viên theo số điện thoại
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            member = memberRepo.findByPhoneNumber(phoneNumber);
            if (member == null) {
                return "Không tìm thấy thành viên với số điện thoại này";
            }
        } else {
            return "Số điện thoại không được để trống";
        }

        Book book = bookRepo.findByTitle(title);

//        if (book.isEmpty()) {
//            return "Không tìm thấy sách với tiêu đề này";
//        }


        // Kiểm tra các điều kiện mượn sách
        if (book.getQuantity() == 0) {
            return "Sách hiện không có sẵn";
        }

        if (member.getBooksBorrowed() == 5) {
            return "Không thể mượn quá năm quyển sách";
        }

        List<TransactionHistory> overdueTransactions = transactionHistoryRepo.findByPhoneNumberAndStatusAndDueDateBefore(
                phoneNumber, true, LocalDateTime.now());

        if (!overdueTransactions.isEmpty()) {
            return "Không thể mượn sách mới vì có sách đã quá hạn. Vui lòng trả hết sách đã quá hạn.";
        }

        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime dueDate = borrowDate.plus(14, ChronoUnit.DAYS);

        // Cập nhật số lượng sách và số lượng sách đã mượn của thành viên
        book.setQuantity(book.getQuantity() - 1);
        member.setBooksBorrowed(member.getBooksBorrowed() + 1);

        if (book.getQuantity() == 0) {
            book.setAvailability(false);
        }


        // Tạo bản ghi lịch sử giao dịch
        TransactionHistory history = new TransactionHistory();
        history.setMemberId(member.getMemberId());
        history.setMemberName(member.getName());
        history.setBookId(book.getBookId());
        history.setTitle(book.getTitle());
        history.setPhoneNumber(member.getPhoneNumber());
        history.setTransactionType("Mượn");
        history.setTransactionDate(borrowDate);
        history.setDueDate(dueDate);
        history.setStatus(true);
        history.setDescription("Mượn sách: " + book.getTitle() + ", Hạn trả: " + dueDate);

        // Lưu giao dịch vào cơ sở dữ liệu
        transactionHistoryRepo.save(history);

        // Lưu thay đổi vào cơ sở dữ liệu
        bookRepo.save(book);
        memberRepo.save(member);

        try {
            emailService.sendBorrowSuccessEmail(
                    member.getName(),
                    member.getEmail(),
                    book.getTitle(),
                    borrowDate,
                    dueDate
            );
        } catch (MessagingException e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
        }

        return "Mượn sách thành công. Hạn trả là " + dueDate;
    }




    public String returnBook(String name, String title, String phoneNumber) {
        // Tìm thành viên theo số điện thoại
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return "Số điện thoại không được để trống";
        }

        Member member = memberRepo.findByPhoneNumber(phoneNumber);
        if (member == null) {
            return "Không tìm thấy thành viên với số điện thoại này";
        }

        // Tìm sách theo tiêu đề
        Book book = bookRepo.findByTitle(title);
        if (book == null) {
            return "Không tìm thấy sách với tiêu đề này";
        }

        // Tìm các giao dịch mượn liên quan
        List<TransactionHistory> borrowTransactions = transactionHistoryRepo.findByMemberIdAndBookIdAndTransactionTypeAndStatus(
                member.getMemberId(), book.getBookId(), "Mượn", true);

        if (borrowTransactions.isEmpty()) {
            return "Thành viên chưa mượn sách này hoặc sách đã được trả";
        }

        LocalDateTime returnDate = LocalDateTime.now();

        // Cập nhật số lượng sách và số lượng sách đã mượn của thành viên
        book.setQuantity(book.getQuantity() + 1);
        member.setBooksBorrowed(member.getBooksBorrowed() - 1);

        if (book.getQuantity() > 0) {
            book.setAvailability(true);
        }

        // Đánh dấu tất cả các giao dịch mượn liên quan thành false
        for (TransactionHistory borrowTransaction : borrowTransactions) {
            borrowTransaction.setStatus(false);
            borrowTransaction.setDescription(borrowTransaction.getDescription() +
                    " (Đã trả vào ngày " + returnDate + ")");
        }
        transactionHistoryRepo.saveAll(borrowTransactions);

        // Tạo bản ghi lịch sử giao dịch mới cho việc trả sách
        TransactionHistory history = new TransactionHistory();
        history.setMemberId(member.getMemberId());
        history.setMemberName(member.getName());
        history.setBookId(book.getBookId());
        history.setTitle(book.getTitle());
        history.setPhoneNumber(member.getPhoneNumber());
        history.setTransactionType("Trả");
        history.setTransactionDate(returnDate);
        history.setDueDate(null); // Không cần hạn trả khi trả sách
        history.setStatus(false);
        history.setDescription("Trả sách: " + book.getTitle() + ", Ngày trả: " + returnDate);

        // Lưu giao dịch trả sách vào cơ sở dữ liệu
        transactionHistoryRepo.save(history);

        // Lưu thay đổi vào cơ sở dữ liệu
        bookRepo.save(book);
        memberRepo.save(member);

        // Gửi email thông báo thành công
        try {
            emailService.sendReturnSuccessEmail(
                    member.getName(),
                    member.getEmail(),
                    book.getTitle(),
                    returnDate
            );
        } catch (MessagingException e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
        }

        return "Trả sách thành công vào ngày " + returnDate;
    }


    public String renewBook(String name, String title, String phoneNumber) {
//        String nameSlug = toSlug(name);
//        String titleSlug = toSlug(title);

        Member member = null;
        member = memberRepo.findByPhoneNumber(phoneNumber);

        if (member == null) {
            return "Không tìm thấy thành viên với số điện thoại này";
        }

        Book book = bookRepo.findByTitle(title);

        if (book == null) {
            return "Không tìm thấy sách với title này";
        }

        // Kiểm tra giao dịch mượn trong bảng transactionHistory với status = true
        List<TransactionHistory> borrowTransactions = transactionHistoryRepo.findByMemberIdAndBookIdAndTransactionTypeAndStatus(
                member.getMemberId(), book.getBookId(), "Mượn", true);

        if (borrowTransactions.isEmpty()) {
            return "Thành viên chưa mượn sách này hoặc sách đã được trả";
        }

        TransactionHistory borrowTransaction = borrowTransactions.get(0);

        List<TransactionHistory> renewTransactions = transactionHistoryRepo.findByMemberIdAndBookIdAndTransactionTypeAndStatus(
                member.getMemberId(), book.getBookId(), "Gia hạn", true);
        int maxRenewCount = 2;
        if (renewTransactions.size() >= maxRenewCount) {
            return "Sách này đã đạt giới hạn gia hạn tối đa.";
        }

        LocalDateTime dueDate = borrowTransaction.getDueDate();
        LocalDateTime now = LocalDateTime.now();

        long daysLeft = ChronoUnit.DAYS.between(now, dueDate);

        if (daysLeft < 7) {
            LocalDateTime newDueDate = now.plus(7, ChronoUnit.DAYS);

            TransactionHistory renewTransaction = new TransactionHistory();
            renewTransaction.setMemberId(member.getMemberId());
            renewTransaction.setMemberName(member.getName());
            renewTransaction.setBookId(book.getBookId());
            renewTransaction.setTitle(book.getTitle());
            renewTransaction.setTransactionType("Gia hạn");
            renewTransaction.setPhoneNumber(member.getPhoneNumber());
            renewTransaction.setTransactionDate(now);  // Ngày gia hạn hiện tại
            renewTransaction.setDueDate(newDueDate);  // Hạn mới
            renewTransaction.setStatus(true);
            renewTransaction.setDescription("Gia hạn sách: " + book.getTitle() + ", Hạn mới: " + newDueDate);

            // Lưu giao dịch gia hạn vào cơ sở dữ liệu
            transactionHistoryRepo.save(renewTransaction);

            try {
                emailService.sendRenewalSuccessEmail(
                        member.getName(),
                        member.getEmail(),
                        book.getTitle(),
                        newDueDate
                );
            } catch (MessagingException e) {
                System.err.println("Gửi email thất bại: " + e.getMessage());

            }

            // Trả về thông báo thành công
            return "Gia hạn sách thành công. Hạn mới là " + newDueDate;
        } else {
            // Nếu còn nhiều hơn 7 ngày thì không cần gia hạn
            return "Bạn chưa cần gia hạn sách. Thời gian mượn còn lại là " + daysLeft + " ngày.";
        }
    }

    public List<Map<String, String>> getAllBorrowTransactions() {
        // Lấy tất cả các giao dịch có loại "Mượn"
        List<TransactionHistory> transactions = transactionHistoryRepo.findByTransactionTypeAndStatus("Mượn", true);

        // Chuyển đổi danh sách giao dịch thành danh sách kèm trạng thái
        List<Map<String, String>> result = new ArrayList<>();
        for (TransactionHistory transaction : transactions) {
            Map<String, String> transactionDetails = new HashMap<>();
            transactionDetails.put("memberId", transaction.getMemberId());
            transactionDetails.put("memberName", transaction.getMemberName());
            transactionDetails.put("bookId", transaction.getBookId());
            transactionDetails.put("bookTitle", transaction.getTitle());
            transactionDetails.put("phoneNumber", transaction.getPhoneNumber());
            transactionDetails.put("transactionDate", transaction.getTransactionDate().toString());
            transactionDetails.put("status", transaction.getStatus() ? "Đang mượn" : "Đã trả");
            transactionDetails.put("description", transaction.getDescription());

            result.add(transactionDetails);
        }

        return result;
    }

    public List<Map<String, String>> getAllReturnTransactions() {
        // Lấy tất cả giao dịch "Trả"
        List<TransactionHistory> transactions = transactionHistoryRepo.findByTransactionType("Trả");

        // Chuyển đổi sang định dạng mong muốn
        List<Map<String, String>> result = new ArrayList<>();
        for (TransactionHistory transaction : transactions) {
            Map<String, String> transactionDetails = new HashMap<>();
            transactionDetails.put("memberId", transaction.getMemberId());
            transactionDetails.put("memberName", transaction.getMemberName());
            transactionDetails.put("bookId", transaction.getBookId());
            transactionDetails.put("bookTitle", transaction.getTitle());
            transactionDetails.put("phoneNumber", transaction.getPhoneNumber());

            transactionDetails.put("transactionDate", transaction.getTransactionDate().toString());
            transactionDetails.put("status", transaction.getStatus() ? "Đang mượn" : "Đã trả");
            transactionDetails.put("description", transaction.getDescription());

            result.add(transactionDetails);
        }

        return result;
    }
    public List<Map<String, String>> getAllRenewTransactions() {
        // Lấy tất cả các giao dịch có loại "Gia hạn"
        List<TransactionHistory> transactions = transactionHistoryRepo.findByTransactionType("Gia hạn");

        // Chuyển đổi danh sách giao dịch thành danh sách kèm trạng thái
        List<Map<String, String>> result = new ArrayList<>();
        for (TransactionHistory transaction : transactions) {
            Map<String, String> transactionDetails = new HashMap<>();
            transactionDetails.put("memberId", transaction.getMemberId());
            transactionDetails.put("memberName", transaction.getMemberName());
            transactionDetails.put("bookId", transaction.getBookId());
            transactionDetails.put("bookTitle", transaction.getTitle());
            transactionDetails.put("phoneNumber", transaction.getPhoneNumber());

            transactionDetails.put("transactionDate", transaction.getTransactionDate().toString());
            transactionDetails.put("status", transaction.getStatus() ? "Đang mượn" : "Đã trả");
            transactionDetails.put("description", transaction.getDescription());

            result.add(transactionDetails);
        }

        return result;
    }

    //ddem so sach dang muon
    public long countBorrowedBooks(){
        return transactionHistoryRepo.countByTransactionTypeAndStatus("Mượn",true);
    }

}

