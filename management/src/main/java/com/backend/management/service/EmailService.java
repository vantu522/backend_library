package com.backend.management.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.thymeleaf.TemplateEngine;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    // Gửi thông báo mượn sách thành công
    public void sendBorrowSuccessEmail(String name, String email, String title, LocalDateTime borrowDate, LocalDateTime dueDate) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("title", title);
        context.setVariable("borrowDate", borrowDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        context.setVariable("dueDate", dueDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        String emailContent = templateEngine.process("email/borrow_success", context);
        sendEmail(email, "Thông báo mượn sách thành công", emailContent);
    }

    // Gửi thông báo trả sách thành công
    public void sendReturnSuccessEmail(String name, String email, String title, LocalDateTime returnDate) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("title", title);
        context.setVariable("returnDate", returnDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        String emailContent = templateEngine.process("email/return_success", context);
        sendEmail(email, "Thông báo trả sách thành công", emailContent);
    }

    // Gửi thông báo gia hạn sách thành công
    public void sendRenewalSuccessEmail(String name, String email, String title, LocalDateTime newDueDate) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("title", title);
        context.setVariable("newDueDate", newDueDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        String emailContent = templateEngine.process("email/renewal_success", context);
        sendEmail(email, "Thông báo gia hạn sách thành công", emailContent);
    }

    // Gửi email thông báo sắp hết hạn (1 ngày trước khi hết hạn)
    public void sendDueDateReminderEmail(String name, String email, String title, LocalDateTime dueDate) throws MessagingException {
        // Tính toán thời gian gửi email là dueDate - 1 ngày
        LocalDateTime sendDate = dueDate.minusDays(1); // Trừ 1 ngày để gửi email

        // Nếu ngày gửi email là hôm nay hoặc trong tương lai, tiến hành gửi email
        if (sendDate.isBefore(LocalDateTime.now())) {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("title", title);
            context.setVariable("dueDate", dueDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

            String emailContent = templateEngine.process("email/due_date_reminder", context);
            sendEmail(email, "Thông báo sách sắp hết hạn", emailContent);
        } else {
            // Bạn có thể thêm logic để lên lịch gửi email vào đúng thời gian tính toán ở đây nếu cần
            // Ví dụ, bạn có thể lưu lại thông tin và sử dụng một công cụ như @Scheduled để gửi email đúng lúc
            System.out.println("Email sẽ được gửi vào " + sendDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        }
    }

    // Phương thức gửi email chung
    private void sendEmail(String email, String subject, String content) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(mimeMessage);
    }
}
