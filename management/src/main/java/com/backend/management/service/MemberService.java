package com.backend.management.service;

import com.backend.management.model.Member;
import com.backend.management.repository.MemberRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    @Autowired
    private MemberRepo memberRepo;

    public Member createMember(Member member){
        return memberRepo.save(member);
    }

    public Member getMemberById(String memberId){
        return memberRepo.findById(memberId).orElse(null);
    }

    public List<Member> getAllMembers(){
        return memberRepo.findAll();
    }

    public Member updateMember(String memberId, Member memberDetails){
        Optional<Member> optionalMember = memberRepo.findById(memberId);
        if(optionalMember.isPresent()){
            Member member = optionalMember.get();
            member.setName(memberDetails.getName());
            member.setEmail(memberDetails.getEmail());
            member.setPhoneNumber(memberDetails.getPhoneNumber());
            member.setAddress(memberDetails.getAddress());
            member.setType(memberDetails.getType());
            member.setQuota(memberDetails.getQuota());

            return memberRepo.save(member);
        } else{
            return null;
        }
    }

    public void deleteMember(String memberId){
         memberRepo.deleteById(memberId);
    }

}
