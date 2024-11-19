package com.backend.management.service;

import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Book;
import com.backend.management.model.Member;
//import com.backend.management.model.Transaction;
import com.backend.management.model.TransactionHistory;
import com.backend.management.repository.BookRepo;
import com.backend.management.repository.MemberRepo;
import com.backend.management.repository.TransactionHistoryRepo;
import com.mongodb.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.List;

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

    private String toSlug(String input) {
        if (input == null)
            return "";

        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll("/", " ")
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

    // Phương thức mượn sách
    public String borrowBook(String name, String title, @Nullable String memberId, @Nullable String bookId) {
        // Tạo slug cho name và title
        String nameSlug = toSlug(name);
        String titleSlug = toSlug(title);
       // Member member = null;

        // Nếu có memberId, tìm thành viên theo memberId
        if (memberId != null && !memberId.isEmpty()) {
            member = memberRepo.findByMemberId(memberId).orElse(null);
            if (member == null) {
                return "Không tìm thấy thành viên với memberId này";
            }
            //return member.getMemberId();
        } else {
            // Nếu không có memberId, tìm tất cả thành viên có tên trùng
            List<Member> members = memberRepo.findAll().stream()
                    .filter(m -> toSlug(m.getName()).equals(nameSlug))
                    .toList();

            if (members.isEmpty()) {
                return "Không tìm thấy thành viên nào có tên này";
            }

            // Nếu có nhiều thành viên trùng tên, yêu cầu người dùng chọn memberId
            if (members.size() > 1) {
                StringBuilder memberList = new StringBuilder("Có nhiều thành viên trùng tên, vui lòng chọn memberId:\n");
                for (Member m : members) {
                    memberList.append("ID: ").append(m.getMemberId())
                            .append(" - Tên: ").append(m.getName())
                            .append("\n");
                }
                return memberList.toString();
            }

            // Nếu chỉ có một thành viên, lấy thành viên đó
            member = members.get(0);
        }

        // Kiểm tra nếu bookId được cung cấp, lấy sách bằng bookId
        Optional<Book> bookOpt = Optional.empty();
        if (bookId != null && !bookId.isEmpty()) {
            bookOpt = bookRepo.findById(bookId);
        } else {
            // Tìm sách theo slug title nếu bookId không có
            bookOpt = bookRepo.findAll().stream()
                    .filter(book -> toSlug(book.getTitle()).equals(titleSlug))
                    .findFirst();
        }

        if (bookOpt.isEmpty()) {
            return "Không tìm thấy sách này";
        }

        Book book = bookOpt.get();

        // Kiểm tra các điều kiện mượn sách
        if (book.getQuantity() == 0) {
            return "Sách hiện không có sẵn";
        }

        if (member.getBooksBorrowed() == 5) {
            return "Không thể mượn quá năm quyển sách";
        }

        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime dueDate = borrowDate.plus(14, ChronoUnit.DAYS);

        // Cập nhật số lượng sách và số lượng sách đã mượn của thành viên
        book.setQuantity(book.getQuantity() - 1);
        member.setBooksBorrowed(member.getBooksBorrowed() + 1);

        if (book.getQuantity() == 0) {
            book.setAvailability(false);
        }

        // Thêm một chuỗi mô tả giao dịch vào danh sách các giao dịch của thành viên
        String transactionDescription = "Mượn sách: " + book.getTitle() + ", Hạn trả: " + dueDate;
        member.getTransactions().add(transactionDescription);

        // Tạo bản ghi lịch sử giao dịch
        TransactionHistory history = new TransactionHistory();
        history.setMemberId(member.getMemberId());
        history.setMemberName(member.getName());
        history.setBookId(book.getBookId());
        history.setTitle(book.getTitle());
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

        return "Mượn sách thành công. Hạn trả là " + dueDate;
    }


    public String returnBook(String name, String title, @Nullable String memberId, @Nullable String bookId) {
        // Tạo slug cho name và title
        String nameSlug = toSlug(name);
        String titleSlug = toSlug(title);

        // Tìm thành viên theo memberId nếu có
        Member member = null;
        if (memberId != null && !memberId.isEmpty()) {
            member = memberRepo.findByMemberId(memberId).orElse(null);
            if (member == null) {
                return "Không tìm thấy thành viên với memberId này";
            }
        } else {
            // Nếu không có memberId, tìm tất cả thành viên có tên trùng
            List<Member> members = memberRepo.findAll().stream()
                    .filter(m -> toSlug(m.getName()).equals(nameSlug))
                    .toList();

            if (members.isEmpty()) {
                return "Không tìm thấy thành viên nào có tên này";
            }

            if (members.size() > 1) {
                StringBuilder memberList = new StringBuilder("Có nhiều thành viên trùng tên, vui lòng chọn memberId:\n");
                for (Member m : members) {
                    memberList.append("ID: ").append(m.getMemberId())
                            .append(" - Tên: ").append(m.getName())
                            .append("\n");
                }
                return memberList.toString();
            }

            member = members.get(0);
        }

        List<TransactionHistory> overdueTransactions = transactionHistoryRepo.findByMemberIdAndStatusAndDueDateBefore(
                memberId, true, LocalDateTime.now());

        if (!overdueTransactions.isEmpty()) {
            return "Không thể mượn sách mới vì có sách đã quá hạn. Vui lòng trả hết sách đã quá hạn.";
        }


        // Kiểm tra nếu bookId được cung cấp, lấy sách bằng bookId
        Optional<Book> bookOpt = Optional.empty();
        if (bookId != null && !bookId.isEmpty()) {
            bookOpt = bookRepo.findById(bookId);
        } else {
            // Tìm sách theo slug title nếu bookId không có
            bookOpt = bookRepo.findAll().stream()
                    .filter(book -> toSlug(book.getTitle()).equals(titleSlug))
                    .findFirst();
        }

        if (bookOpt.isEmpty()) {
            return "Không tìm thấy sách này";
        }

        Book book = bookOpt.get();

        // Kiểm tra giao dịch mượn trong bảng transactionHistory với status = true
        List<TransactionHistory> borrowTransactions = transactionHistoryRepo.findByMemberIdAndBookIdAndTransactionTypeAndStatus(
                member.getMemberId(), book.getBookId(), "Mượn", true);

        if (borrowTransactions.isEmpty()) {
            return "Thành viên chưa mượn sách này hoặc sách đã được trả";
        }

        // Nếu giao dịch mượn tồn tại, cập nhật số lượng sách và trạng thái
        book.setQuantity(book.getQuantity() + 1);
        if (book.getQuantity() > 0) {
            book.setAvailability(true);
        }

        // Cập nhật trạng thái của giao dịch mượn (status = false)
        TransactionHistory borrowedTransaction = borrowTransactions.get(0);
        borrowedTransaction.setStatus(false);
        transactionHistoryRepo.save(borrowedTransaction);

        // Tạo bản ghi giao dịch trả sách mới
        TransactionHistory returnTransaction = new TransactionHistory();
        returnTransaction.setMemberId(member.getMemberId());
        returnTransaction.setMemberName(member.getName());
        returnTransaction.setBookId(book.getBookId());
        returnTransaction.setTitle(book.getTitle());
        returnTransaction.setTransactionType("Trả");
        returnTransaction.setTransactionDate(LocalDateTime.now());
        returnTransaction.setDueDate(null); // Không cần ngày hạn khi trả sách
        returnTransaction.setDescription("Trả sách: " + book.getTitle());
        returnTransaction.setStatus(false); // Đánh dấu là giao dịch "Trả"

        // Lưu giao dịch trả sách và cập nhật thông tin sách
        transactionHistoryRepo.save(returnTransaction);
        bookRepo.save(book);

        return "Trả sách thành công";
    }

    public String renewBook(String name, String title, @Nullable String memberId, @Nullable String bookId) {
        // Tạo slug cho name và title
        String nameSlug = toSlug(name);
        String titleSlug = toSlug(title);

        // Tìm thành viên theo memberId nếu có
        Member member = null;
        if (memberId != null && !memberId.isEmpty()) {
            member = memberRepo.findByMemberId(memberId).orElse(null);
            if (member == null) {
                return "Không tìm thấy thành viên với memberId này";
            }
        } else {
            // Nếu không có memberId, tìm tất cả thành viên có tên trùng
            List<Member> members = memberRepo.findAll().stream()
                    .filter(m -> toSlug(m.getName()).equals(nameSlug))
                    .toList();

            if (members.isEmpty()) {
                return "Không tìm thấy thành viên nào có tên này";
            }

            if (members.size() > 1) {
                StringBuilder memberList = new StringBuilder("Có nhiều thành viên trùng tên, vui lòng chọn memberId:\n");
                for (Member m : members) {
                    memberList.append("ID: ").append(m.getMemberId())
                            .append(" - Tên: ").append(m.getName())
                            .append("\n");
                }
                return memberList.toString();
            }

            member = members.get(0);
        }

        // Kiểm tra nếu bookId được cung cấp, lấy sách bằng bookId
        Optional<Book> bookOpt = Optional.empty();
        if (bookId != null && !bookId.isEmpty()) {
            bookOpt = bookRepo.findById(bookId);
        } else {
            // Tìm sách theo slug title nếu bookId không có
            bookOpt = bookRepo.findAll().stream()
                    .filter(book -> toSlug(book.getTitle()).equals(titleSlug))
                    .findFirst();
        }

        if (bookOpt.isEmpty()) {
            return "Không tìm thấy sách này";
        }

        Book book = bookOpt.get();

        // Kiểm tra giao dịch mượn trong bảng transactionHistory với status = true
        List<TransactionHistory> borrowTransactions = transactionHistoryRepo.findByMemberIdAndBookIdAndTransactionTypeAndStatus(
                member.getMemberId(), book.getBookId(), "Mượn", true);

        if (borrowTransactions.isEmpty()) {
            return "Thành viên chưa mượn sách này hoặc sách đã được trả";
        }

        // Lấy giao dịch mượn đầu tiên (có thể có nhiều giao dịch nhưng ta chỉ lấy giao dịch đầu tiên)
        TransactionHistory borrowTransaction = borrowTransactions.get(0);

        // Kiểm tra số lần gia hạn trước khi gia hạn
        List<TransactionHistory> renewTransactions = transactionHistoryRepo.findByMemberIdAndBookIdAndTransactionTypeAndStatus(
                member.getMemberId(), book.getBookId(), "Gia hạn", true);
        // giới hạn số lần gia hanj sách
        int maxRenewCount = 2;
        if (renewTransactions.size() >= maxRenewCount) {
            return "Sách này đã đạt giới hạn gia hạn tối đa.";
        }

        // Kiểm tra ngày hết hạn sách, nếu còn nhỏ hơn 7 ngày thì mới cần gia hạn
        LocalDateTime dueDate = borrowTransaction.getDueDate();
        LocalDateTime now = LocalDateTime.now();

        // Tính số ngày còn lại trước khi hết hạn
        long daysLeft = ChronoUnit.DAYS.between(now, dueDate);

        // Nếu thời gian còn lại ít hơn 7 ngày, thực hiện gia hạn
        if (daysLeft < 7) {
            // Gia hạn ngày trả sách
            LocalDateTime newDueDate = now.plus(7, ChronoUnit.DAYS);

            // Tạo lịch sử gia hạn
            TransactionHistory renewTransaction = new TransactionHistory();
            renewTransaction.setMemberId(member.getMemberId());
            renewTransaction.setMemberName(member.getName());
            renewTransaction.setBookId(book.getBookId());
            renewTransaction.setTitle(book.getTitle());
            renewTransaction.setTransactionType("Gia hạn");
            renewTransaction.setTransactionDate(now);  // Ngày gia hạn hiện tại
            renewTransaction.setDueDate(newDueDate);  // Hạn mới
            renewTransaction.setStatus(true);
            renewTransaction.setDescription("Gia hạn sách: " + book.getTitle() + ", Hạn mới: " + newDueDate);

            // Lưu giao dịch gia hạn vào cơ sở dữ liệu
            transactionHistoryRepo.save(renewTransaction);

            // Trả về thông báo thành công
            return "Gia hạn sách thành công. Hạn mới là " + newDueDate;
        } else {
            // Nếu còn nhiều hơn 7 ngày thì không cần gia hạn
            return "Bạn chưa cần gia hạn sách. Thời gian mượn còn lại là " + daysLeft + " ngày.";
        }
    }


}


