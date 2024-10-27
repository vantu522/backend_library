package com.backend.management.controller;

import com.backend.management.model.Member;
import com.backend.management.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public List<Member> getAllMembers(){
        return memberService.getAllMembers();
    }

    @GetMapping("/{id}")
    public Member getMemberById(@PathVariable("id") String memberId) {
        return memberService.getMemberById(memberId);
    }

    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberService.createMember(member);
    }

    @PutMapping("/{id}")
    public Member updateMember(@PathVariable("id") String memberId, @RequestBody Member memberDetails) {
        return memberService.updateMember(memberId, memberDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable("id") String memberId) {
        memberService.deleteMember(memberId);
    }


}
