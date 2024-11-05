package com.backend.management.controller;

import com.backend.management.model.Member;
import com.backend.management.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {
    @Autowired
    private MemberService memberService;

    // lay tat ca thanh vien
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers(){
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    //tim thanh vien theo ten hoac so dien thoai
    @GetMapping("/search")
    public List<Member> getMemberByNameAndPhoneNumber(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) String phoneNumber){
        return memberService.getMemberByNameAndPhoneNumber(name,phoneNumber);
    }


}
