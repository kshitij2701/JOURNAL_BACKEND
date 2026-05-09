package com.example.journalapp.repository;

import com.example.journalapp.entity.JournalEntry;
import com.example.journalapp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

    User findByUserName(String username);
    User deleteByUserName(String username);

}
