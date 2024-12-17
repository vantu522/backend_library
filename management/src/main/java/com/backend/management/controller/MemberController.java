package com.backend.management.controller;

import com.backend.management.exception.InvalidCredentialsException;
import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.LoginRequest;
import com.backend.management.model.LoginUser;
import com.backend.management.model.Member;
import com.backend.management.service.MemberService;
import com.backend.management.service.ValidationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/members")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private ValidationService validationService;

    // lay tat ca thanh vien
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers(){
        List<Member> members =  memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    //tim thanh vien theo ten hoac so dien thoai
    @GetMapping("/search")
    public List<Member> getMemberByNameAndPhoneNumber(@RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String phoneNumber){
        return memberService.getMemberByNameAndPhoneNumber(name,phoneNumber);
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerMember(@Valid @RequestBody Member member) {
        try {
            String emailType = validationService.getEmailType(member.getEmail());

            if (!"PTIT Student Email".equals(emailType) && !"General Email".equals(emailType)) {
                return ResponseEntity.badRequest().body("Email không hợp lệ");
            }

            Member savedMember = memberService.createMember(member);
            return ResponseEntity.ok(savedMember);
        } catch (Exception e) {
            // Log lỗi nếu cần
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi server: " + ex.getMessage());
    }


    // lay thanh vien theo id va sua
    @PutMapping("/update/{memberId}")
    public ResponseEntity<Member> updateMember(@PathVariable String memberId, @RequestBody Member updatedMember){
        Member members = memberService.updateMember(memberId,updatedMember);
        return ResponseEntity.ok(members);
    }


    // xoa thanh vien theo id
    @DeleteMapping("/delete/{memberId}")
    public void deleteMember(@PathVariable String memberId){
        memberService.deleteMemberById(memberId);
    }

    @GetMapping("/count")
    public ResponseEntity<Long > getMemberCount(){
        long countMember = memberService.countAllMembers();
        return ResponseEntity.ok(countMember);
    }

    @GetMapping("/{memberId}/borrowed-renewed-books")
    public ResponseEntity<Map<String, Object>> getBorrowedAndRenewedBooks(@PathVariable String memberId) {
        Map<String, Object> response = memberService.getMemberBorrowedAndRenewedBooks(memberId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/statistics")
    public ResponseEntity<?> getMemberStatistics(){
        try{
            return ResponseEntity.ok(memberService.getMemberStatistics());
        } catch(Exception e){
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUser loginUser){
        try{
            Member member = memberService.authenticateMember(loginUser.getEmail(), loginUser.getPassword());
            return ResponseEntity.ok(member);
        } catch(InvalidCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    // gui ma otp
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendPasswordResetOtp(@RequestBody LoginUser request) {
        try {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username không được để trống");
            }

            memberService.sendPasswordResetOtp(request.getEmail());
            return ResponseEntity.ok("Đã gửi OTP thành công");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy người dùng với email: " + request.getEmail());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi gửi email: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    // doi mat khau
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request){
        memberService.resetPasswordWithOtp(
                request.get("email"),
                request.get("otp"),
                request.get("newPassword")
        );
        return ResponseEntity.ok().body("mat khau doi thanh cong");
    }

    // Đổi mật khẩu (khi đã đăng nhập)
    @PostMapping("/change")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        memberService.changePassword(
                request.get("email"),
                request.get("oldPassword"),
                request.get("newPassword")
        );
        return ResponseEntity.ok().body("Mật khẩu đã được đổi thành công");
    }




}
