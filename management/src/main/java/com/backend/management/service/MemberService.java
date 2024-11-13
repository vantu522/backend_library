package com.backend.management.service;

import com.backend.management.exception.ResourceNotFoundException;
import com.backend.management.model.Member;
import com.backend.management.repository.BookRepo;
import com.backend.management.repository.MemberRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {
    @Autowired
    private MemberRepo memberRepo;

    private BookRepo bookRepo ;

    //lay tat ca cac member
    public List<Member> getAllMembers(){
        return memberRepo.findAll();
    }


    // lay thanh vien theo ten hoac so dien thoai
    public List<Member> getMemberByNameAndPhoneNumber(String name,String phoneNumber){
        String nameSlug = name != null ? toSlug(name) : null;

        if(nameSlug != null && phoneNumber != null ){
            return memberRepo.findAll().stream()
                    .filter(member ->
                            (nameSlug != null && toSlug(member.getName()).equals(nameSlug)) ||
                                    (phoneNumber != null && member.getPhoneNumber().equals(phoneNumber)))
                    .collect(Collectors.toList());
        } else if (nameSlug != null) {
            return memberRepo.findAll().stream()
                    .filter(member->toSlug(member.getName()).equals(nameSlug))
                    .collect(Collectors.toList());

        } else if(phoneNumber != null){
            return memberRepo.findAll().stream()
                    .filter(member -> member.getPhoneNumber().equals(phoneNumber))
                    .collect(Collectors.toList());
        }

        return getAllMembers();

    }

    //them thanh vien
    public Member addMember(Member member){
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
        if(updatedMember.getBooksBorrowed() != 0){
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




    //cau hinh Slug
    private String toSlug(String input) {
        if (input == null) return "";

        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll("/"," ")
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

}
