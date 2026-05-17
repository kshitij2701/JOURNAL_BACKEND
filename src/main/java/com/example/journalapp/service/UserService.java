package com.example.journalapp.service;

import com.example.journalapp.entity.User;
import com.example.journalapp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class UserService {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User saveNewUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER"));
        return userRepository.save(user);
    }

//    public User saveNewUser(User user) {
//        user.setPassword(
//                passwordEncoder.encode(user.getPassword())
//        );
//        if (user.getRoles() == null || user.getRoles().isEmpty()) {
//            user.setRoles(List.of("USER"));
//        } else {
//            List<String> roles = user.getRoles()
//                    .stream()
//                    .map(String::toUpperCase)
//                    .distinct()
//                    .toList();
//
//            user.setRoles(roles);
//        }
//        return userRepository.save(user);
//    }


    public User saveAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER", "ADMIN"));
        return userRepository.save(user);
    }

    public User SaveUser(User user){
        return userRepository.save(user);
    }

    public User updateUser(User updatedUser, String userName) {

        User userInDb = userRepository.findByUserName(userName);
        if (userInDb == null) {
            throw new RuntimeException("User not found");
        }

        // Update username
        if (updatedUser.getUserName() != null &&
                !updatedUser.getUserName().trim().isEmpty()) {
            userInDb.setUserName(updatedUser.getUserName());
        }

        // Update password
        if (updatedUser.getPassword() != null &&
                !updatedUser.getPassword().trim().isEmpty()) {
            userInDb.setPassword(
                    passwordEncoder.encode(updatedUser.getPassword())
            );
        }

        // Update email
        if (updatedUser.getEmail() != null &&
                !updatedUser.getEmail().trim().isEmpty()) {

            userInDb.setEmail(updatedUser.getEmail());
        }

        // Update sentiment analysis preference
        userInDb.setSentimentAnalysis(
                updatedUser.isSentimentAnalysis()
        );
        return userRepository.save(userInDb);
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

