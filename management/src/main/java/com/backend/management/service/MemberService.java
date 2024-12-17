package com.backend.management.service;

import com.backend.management.exception.InvalidCredentialsException;
import com.backend.management.exception.InvalidOtpException;
import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Librarian;
import com.backend.management.model.Member;
import com.backend.management.model.TransactionHistory;
import com.backend.management.repository.BookRepo;
import com.backend.management.repository.MemberRepo;
import com.backend.management.repository.TransactionHistoryRepo;
import com.backend.management.repository.TransactionRepo;
import com.backend.management.utils.SlugUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberService {
    @Autowired
    private MemberRepo memberRepo;

    @Autowired
    private BookRepo bookRepo ;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private TransactionHistoryRepo transactionHistoryRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender mailSender;

    //lay tat ca cac member
    public List<Member> getAllMembers(){
        return memberRepo.findAll();
    }


    // lay thanh vien theo ten hoac so dien thoai
    public List<Member> getMemberByNameAndPhoneNumber(String name,String phoneNumber){
        String nameSlug = name != null ? SlugUtil.toSlug(name) : null;

        if(nameSlug != null && phoneNumber != null ){
            return memberRepo.findAll().stream()
                    .filter(member ->
                            (nameSlug != null && SlugUtil.toSlug(member.getName()).equals(nameSlug)) ||
                                    (phoneNumber != null && member.getPhoneNumber().equals(phoneNumber)))
                    .collect(Collectors.toList());
        } else if (nameSlug != null) {
            return memberRepo.findAll().stream()
                    .filter(member->SlugUtil.toSlug(member.getName()).equals(nameSlug))
                    .collect(Collectors.toList());

        } else if(phoneNumber != null){
            return memberRepo.findAll().stream()
                    .filter(member -> member.getPhoneNumber().equals(phoneNumber))
                    .collect(Collectors.toList());
        }

        return getAllMembers();

    }

    //them thanh vien
    public Member createMember(Member member){
        // check email hop le chua
        validationService.isValidEmail(member.getEmail());
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);
        // kieam tra xem email ton tai chua
        Optional<Member> existingMember = memberRepo.findByEmail(member.getEmail());
        if(existingMember.isPresent()){
            throw new IllegalArgumentException("email da su dung");

        }


        return memberRepo.save(member);

    }

    // lay thanh vien theo id va sua
    public Member updateMember(String memberId, Member updatedMember){
        Member existingMember =  memberRepo.findByMemberId(memberId)
                .orElseThrow(()-> new ResourceNotFoundException(memberId));

        if(updatedMember.getMemberId() != null){
            existingMember.setMemberId(updatedMember.getMemberId());
        }
        if(updatedMember.getName() != null){
            existingMember.setName(updatedMember.getName());
        }
        if(updatedMember.getPhoneNumber() !=  null){
            existingMember.setPhoneNumber(updatedMember.getPhoneNumber());
        }
        if(updatedMember.getAddress() != null){
            existingMember.setAddress(updatedMember.getAddress());
        }
        if(updatedMember.getEmail() != null){
            existingMember.setEmail(updatedMember.getEmail());
        }
//        if(updatedMember.getTransactions() != null){
//            existingMember.setTransactions(updatedMember.getTransactions());
//        }
        if(updatedMember.getBooksBorrowed() != -1){
            existingMember.setBooksBorrowed(updatedMember.getBooksBorrowed());
        }

        return memberRepo.save(existingMember);
    }

    // xoa thanh vien theo id
    public void deleteMemberById(String idMember){
        if(memberRepo.existsById(idMember)){
            memberRepo.deleteById(idMember);
        } else{
            throw new ResourceNotFoundException(idMember);
        }
    }

    // dem so luong ban doc
    public long countAllMembers(){
        return memberRepo.count();
    }

    // xem ban doc dang muon hoac gia han nhung quyen sach nao
    public Map<String, Object> getMemberBorrowedAndRenewedBooks(String memberId) {
        Member member = memberRepo.findByMemberId(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên với ID: " + memberId));

        // Lấy các giao dịch mượn hoặc gia hạn đang hoạt động
        List<TransactionHistory> activeTransactions = transactionHistoryRepo.findByMemberIdAndStatus(memberId, "Đang mượn");

        // Chuyển đổi danh sách giao dịch thành danh sách thông tin sách
        List<Map<String, String>> borrowedBooks = activeTransactions.stream()
                .map(transaction -> {
                    Map<String, String> bookDetails = new HashMap<>();
                    bookDetails.put("bookId", transaction.getBookId());
                    bookDetails.put("bookTitle", transaction.getTitle());
                    bookDetails.put("author", transaction.getAuthor());
                    bookDetails.put("transactionType", transaction.getTransactionType());
                    bookDetails.put("transactionDate", transaction.getTransactionDate().toString());
                    bookDetails.put("dueDate", transaction.getDueDate() != null ? transaction.getDueDate().toString() : "Không có hạn trả");
                    return bookDetails;
                })
                .collect(Collectors.toList());

        // Kết hợp thông tin thành viên và danh sách sách
        Map<String, Object> result = new HashMap<>();
        result.put("memberId", member.getMemberId());
        result.put("memberName", member.getName());
        result.put("email", member.getEmail());
        result.put("phoneNumber", member.getPhoneNumber());
        result.put("borrowedAndRenewedBooks", borrowedBooks);

        return result;
    }

    //
    public List<Map<String , Object>> getMemberStatistics(){
        LocalDateTime now = LocalDateTime.now();
        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            YearMonth currentMonth = YearMonth.from(now.minusMonths(i));
            LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);

            // Đếm số member mới trong tháng
            long newMembers = mongoTemplate.count(
                    new org.springframework.data.mongodb.core.query.Query(
                            Criteria.where("createdDate").gte(startOfMonth).lt(endOfMonth.plusDays(1))
                    ),
                    Member.class
            );

            // Đếm tổng số member (không cần điều kiện createdDate)
            long activeUsers = memberRepo.count();

            Map<String, Object> monthStat = new HashMap<>();
            monthStat.put("name", "T" + currentMonth.getMonthValue());
            monthStat.put("new", newMembers);
            monthStat.put("active", activeUsers);

            result.add(monthStat);
        }
        
        Collections.reverse(result);
        return result;
    }

    // lam dnag nhap/dang ki cho member
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

    public Member getMemberByEmail(String email){
        Optional<Member> member = memberRepo.findByEmail(email);

        if(!member.isPresent()){
            throw new ResourceNotFoundException("Không tìm thấy người dùng với email: " + email);
        }
        return member.get();
    }

    // xac thuc password
    public Member authenticateMember(String email, String password){
        Member member = getMemberByEmail(email);

        if(passwordEncoder.matches(password, member.getPassword())){
            return member;
        } else{
            throw new InvalidCredentialsException("Invalid username or password");
        }

    }

    //
    public void sendPasswordResetOtp(String email) throws MessagingException{
        try{
            Member member = getMemberByEmail(email);

            // Validate email trước khi gửi
            if (email == null || !validationService.isValidEmail(email)) {
                throw new IllegalArgumentException("Email không hợp lệ: " + email);
            }

            String otp = generateOtp();
            otpStorage.put(email, new OtpData(otp));

            //render noi dung tu template
            Context context = new Context();
            context.setVariable("email", email);
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

    // xac thuc otp va doi mat khau
    public void resetPasswordWithOtp(String email, String otp, String newPassword){
        OtpData otpData = otpStorage.get(email);
        if(otpData == null || !isOtpValid(otpData,otp)){
            throw new InvalidOtpException("OTP không hợp lệ hoặc đã hết hạn");
        }
        Member member = getMemberByEmail(email);
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepo.save(member);

        otpStorage.remove(email);
    }

    // 3. Đổi mật khẩu (khi đã đăng nhập)
    public void changePassword(String email, String oldPassword, String newPassword) {
        // Xác thực mật khẩu cũ
        Member member = authenticateMember(email, oldPassword);

        // Đổi mật khẩu mới
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepo.save(member);
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
