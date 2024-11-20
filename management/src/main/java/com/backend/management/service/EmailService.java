package com.backend.management.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;

import javax.naming.Context;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.thymeleaf.TemplateEngine;
public class EmailService {

    public void sendBorrowSuccessEmail(String memberName, String memberEmail, String bookTitle, LocalDateTime dueDate) throws MessagingException {
        // Tạo OTP nếu cần, trong trường hợp không cần thì bỏ qua bước này

        // Tạo nội dung email với Thymeleaf
        Context context = new Context();
        context.setVariable("memberName", memberName);
        context.setVariable("bookTitle", bookTitle);
        context.setVariable("dueDate", dueDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        // Template Thymeleaf (email/borrow_success.html)
        String emailContent = templateEngine.process("email/borrow_success", context);

        // Tạo và gửi email
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(memberEmail);
        helper.setSubject("Thông báo mượn sách thành công");
        helper.setText(emailContent, true); // true để cho phép định dạng HTML

        mailSender.send(mimeMessage);
    }

}
