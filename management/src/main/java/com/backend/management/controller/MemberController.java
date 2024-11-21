package com.backend.management.controller;

import com.backend.management.model.Member;
import com.backend.management.service.MemberService;
import com.backend.management.service.ValidationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public long countMembers(){
        return memberService.countMembers();
    }



}
