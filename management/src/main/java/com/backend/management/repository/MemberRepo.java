package com.backend.management.repository;

import com.backend.management.model.Member;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepo extends MongoRepository<Member, String> {

    Optional<Member> findByMemberId(String memberId);

    List<Member> findByName(String name);

    List<Member> findByEmail(String email);

    List<Member> findByPhoneNumber(String phoneNumber);

    List<Member> findByNameAndPhoneNumber(String name, String phoneNumber);

}
