package com.example.journalapp.controller;

import com.example.journalapp.entity.User;
import com.example.journalapp.repository.UserRepository;
import com.example.journalapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // ✅ UPDATE USER
    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User userInDb = userService.findByUserName(userName);
        if (userInDb == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        }
        if (user.getUserName() != null && !user.getUserName().isEmpty()) {
            userInDb.setUserName(user.getUserName());
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            userInDb.setPassword(user.getPassword());
        }
        User updatedUser = userService.saveNewUser(userInDb);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK); // 200

    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserById() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUserName(username);
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
        userRepository.deleteByUserName(username);

        return ResponseEntity.ok("User deleted successfully");

    }



}

