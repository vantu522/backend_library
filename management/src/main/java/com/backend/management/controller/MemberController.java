package com.backend.management.controller;

import com.backend.management.model.Member;
import com.backend.management.service.MemberService;
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

    //them thanh vien
    @PostMapping
    public ResponseEntity<Member> addMember(@RequestBody Member member){
      Member savedMember = memberService.addMember(member);
      return new ResponseEntity<>(savedMember, HttpStatus.CREATED);
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



}
