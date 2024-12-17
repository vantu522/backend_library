package com.backend.management.repository;

import com.backend.management.model.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepo extends MongoRepository<Member, String> {

    Optional<Member> findByMemberId(String memberId);



    Optional<Member> findByEmail(String email);

    Member findByPhoneNumber(String phoneNumber);

    List<Member> findByNameAndPhoneNumber(String name, String phoneNumber);

}
