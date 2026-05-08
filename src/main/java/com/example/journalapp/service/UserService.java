package com.example.journalapp.service;

import com.example.journalapp.entity.JournalEntry;
import com.example.journalapp.entity.User;
import com.example.journalapp.repository.JournalEntryRepository;
import com.example.journalapp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveEntry(User user){
        return userRepository.save(user);
    }

    public List<User> getAll() {
        // it will return the list of user Entries present in the db
        System.out.println("GET /user moved to service");
        return userRepository.findAll();
    }

    public Optional<User> findById(ObjectId id){
        // find by id
        return userRepository.findById(id);
    }

    public void deleteById(ObjectId id){
        // find by id
        userRepository.deleteById(id);
    }

    public User findByUserName(String userName){
        return userRepository.findByUserName(userName);
    }


}

