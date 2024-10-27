package com.backend.management.repository;

import com.backend.management.model.Member;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberRepo extends MongoRepository<Member, String> {

}
