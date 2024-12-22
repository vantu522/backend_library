package com.backend.management.service;

import com.backend.management.model.Member;
import com.backend.management.model.Transaction;
import com.backend.management.model.TransactionHistory;
import com.backend.management.repository.MemberRepo;
import com.backend.management.repository.TransactionHistoryRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.springframework.scheduling.annotation.Async;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.thymeleaf.TemplateEngine;

import static java.lang.System.in;

@Service
public class EmailService {
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private MemberRepo memberRepo;
    @Autowired
    private TransactionHistoryRepo transactionHistoryRepo;
    @Autowired
    private JavaMailSender mailSender;


    @Async
    public void sendBorrowSuccessEmail(String name, String email, String title,
                                       LocalDateTime borrowDate, LocalDateTime dueDate) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("title", title);
        context.setVariable("borrowDate", borrowDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        context.setVariable("dueDate", dueDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        String emailContent = templateEngine.process("email/borrow_success", context);
        sendEmail(email, "Thông báo mượn sách thành công", emailContent);
    }

    @Async
    public void sendReturnSuccessEmail(String name, String email, String title, LocalDateTime returnDate) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("title", title);
        context.setVariable("returnDate", returnDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        String emailContent = templateEngine.process("email/return_success", context);
        sendEmail(email, "Thông báo trả sách thành công", emailContent);
    }

    @Async
    public void sendRenewalSuccessEmail(String name, String email, String title, LocalDateTime newDueDate) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("title", title);
        context.setVariable("newDueDate", newDueDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        String emailContent = templateEngine.process("email/renewal_success", context);
        sendEmail(email, "Thông báo gia hạn sách thành công", emailContent);
    }

    @Async    // Gửi email thông báo sắp hết hạn (1 ngày trước khi hết hạn)
    public void sendDueDateReminderEmail(String name, String email, String title, LocalDateTime dueDate) throws MessagingException {
        // Tính toán thời gian gửi email là dueDate - 1 ngày

        // Nếu ngày gửi email là hôm nay hoặc trong tương lai, tiến hành gửi email
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("title", title);
        context.setVariable("dueDate", dueDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        String emailContent = templateEngine.process("email/due_date_reminder", context);
        sendEmail(email, "Thông báo sách sắp hết hạn", emailContent);
    }
    @Async
    public void sendOverdueNotificationEmail(String name, String email, String title, LocalDateTime dueDate) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("title", title);
        context.setVariable("dueDate", dueDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        String emailContent = templateEngine.process("email/overdue_notification", context);

        sendEmail(email, "Thông báo sách mượn quá hạn", emailContent);
    }

    @Async
    public void sendFeedBackResponseEmail(String name, String email, String userContent, String adminResponse) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("userContent", userContent);
        context.setVariable("adminResponse", adminResponse);
        String emailContent = templateEngine.process("email/feedback_response", context);
        sendEmail(email, "Phản hồi từ Admin", emailContent);


    }


//    @Async
    // Phương thức gửi email chung
    private void sendEmail(String email, String subject, String content) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(mimeMessage);
    }



    @Scheduled(cron = "0 52 15 * * ?", zone = "Asia/Ho_Chi_Minh")
    public void sendDueDateReminderBooks() {
        List<TransactionHistory> borrowedBooks = transactionHistoryRepo.findByTransactionTypeAndStatus("Mượn", "Đang mượn");

        for (TransactionHistory borrowedBook : borrowedBooks) {
//            if (borrowedBook.getDueDate() != null &&
//                    borrowedBook.getDueDate().minusDays(1).toLocalDate().isEqual(LocalDate.now())) {

                String memberId = borrowedBook.getMemberId();
                Member member = memberRepo.findByMemberId(memberId).orElse(null);
                if (member != null && member.getEmail() != null) {
                    try {
                        sendDueDateReminderEmail(member.getName(), member.getEmail(), borrowedBook.getTitle(), borrowedBook.getDueDate());
                        System.out.println("Gửi email nhắc nhở thành công đến: " + member.getEmail());
                    } catch (Exception e) {
                        System.err.println("Không thể gửi email nhắc nhở đến: " + member.getEmail());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Thành viên không có email hoặc email rỗng: " + borrowedBook.getMemberName());
                }
            }
        }


}




