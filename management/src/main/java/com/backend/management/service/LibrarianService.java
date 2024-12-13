package com.backend.management.service;

import com.backend.management.exception.InvalidCredentialsException;
import com.backend.management.exception.InvalidOtpException;
import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Librarian;
import com.backend.management.repository.LibrarianRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class LibrarianService {
    @Autowired
    private LibrarianRepo librarianRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private ValidationService validationService;

    // Lưu trữ OTP
    private Map<String, OtpData> otpStorage = new HashMap<>();

    // Class để lưu thông tin OTP
    private static class OtpData {
        String otp;
        long timestamp;

        OtpData(String otp) {
            this.otp = otp;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public Librarian getLibrarianByUsername(String username) {
        Optional<Librarian> librarian = librarianRepo.findByUsername(username);
        if (!librarian.isPresent()) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng với username: " + username);
        }
        return librarian.get();
    }

    public Librarian authenticateLibrarian(String username, String password) {
        Librarian librarian = getLibrarianByUsername(username);
        if (passwordEncoder.matches(password, librarian.getPassword())) {
            return librarian;
        } else {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    public Librarian addLibrarian(Librarian librarian) {
        String encodedPassword = passwordEncoder.encode(librarian.getPassword());
        librarian.setPassword(encodedPassword);
        return librarianRepo.save(librarian);
    }

    public void sendPasswordResetOtp(String username) throws MessagingException {
        try {
            Librarian librarian = getLibrarianByUsername(username);
            String email = librarian.getEmail();
            
            // Validate email trước khi gửi
            if (email == null || !validationService.isValidEmail(email)) {
                throw new IllegalArgumentException("Email không hợp lệ: " + email);
            }

            String otp = generateOtp();
            otpStorage.put(username, new OtpData(otp));

            //render noi dung tu template
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("otp",otp);
            String emailContent = templateEngine.process("email/reset", context);


            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Đặt lại mật khẩu - Mã OTP");
            helper.setText(emailContent, true);
            mailSender.send(mimeMessage);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (MessagingException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gửi OTP: " + e.getMessage());
        }
    }

    // 2. Xác thực OTP và đổi mật khẩu
    public void resetPasswordWithOtp(String username, String otp, String newPassword) {
        // Kiểm tra OTP
        OtpData otpData = otpStorage.get(username);
        if (otpData == null || !isOtpValid(otpData, otp)) {
            throw new InvalidOtpException("OTP không hợp lệ hoặc đã hết hạn");
        }

        // Đổi mật khẩu
        Librarian librarian = getLibrarianByUsername(username);
        librarian.setPassword(passwordEncoder.encode(newPassword));
        librarianRepo.save(librarian);

        // Xóa OTP đã sử dụng
        otpStorage.remove(username);
    }

    // 3. Đổi mật khẩu (khi đã đăng nhập)
    public void changePassword(String username, String oldPassword, String newPassword) {
        // Xác thực mật khẩu cũ
        Librarian librarian = authenticateLibrarian(username, oldPassword);

        // Đổi mật khẩu mới
        librarian.setPassword(passwordEncoder.encode(newPassword));
        librarianRepo.save(librarian);
    }

    // Helper methods
    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private boolean isOtpValid(OtpData otpData, String inputOtp) {
        long currentTime = System.currentTimeMillis();
        return otpData.otp.equals(inputOtp) &&
                (currentTime - otpData.timestamp) <= 5 * 60 * 1000; // 5 phút
    }

}