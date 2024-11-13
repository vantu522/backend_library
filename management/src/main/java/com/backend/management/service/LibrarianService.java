package com.backend.management.service;

import com.backend.management.exception.InvalidCredentialsException;
import com.backend.management.exception.InvalidOtpException;
import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Librarian;
import com.backend.management.repository.LibrarianRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class LibrarianService {
    @Autowired
    private LibrarianRepo librarianRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

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

    // Giữ nguyên các method cũ
    public Librarian getLibrarianByUsername(String username){
        return librarianRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(username));
    }

    public Librarian authenticateLibrarian(String username, String password) {
        Librarian librarian = getLibrarianByUsername(username);
        if (passwordEncoder.matches(password, librarian.getPassword())) {
            return librarian;
        } else {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    public Librarian addLibrarian(Librarian librarian){
        String encodedPassword = passwordEncoder.encode(librarian.getPassword());
        librarian.setPassword(encodedPassword);
        return librarianRepo.save(librarian);
    }



    // Thêm các method mới

    // 1. Gửi mã OTP qua email
    public void sendPasswordResetOtp(String username) {
        Librarian librarian = getLibrarianByUsername(username);
        String otp = generateOtp();

        // Lưu OTP vào storage
        otpStorage.put(username, new OtpData(otp));

        // Tạo và gửi email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(librarian.getEmail());
        message.setSubject("Tra sach de muon qua han roi ");
        message.setText("Mã OTP của bạn là: " + otp + "\nMã này sẽ hết hạn sau 5 phút.");
        mailSender.send(message);
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