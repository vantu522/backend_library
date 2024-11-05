package com.backend.management.service;

import com.backend.management.model.Member;
import com.backend.management.repository.MemberRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberService {
    @Autowired
    private MemberRepo memberRepo;

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
