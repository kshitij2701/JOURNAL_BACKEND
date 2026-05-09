package com.example.journalapp.controller;

import com.example.journalapp.entity.User;
import com.example.journalapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @GetMapping("/health-check")
    public String checkDb() {
        return "All good";
    }


    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody User user) {

        if (user.getUserName() == null || user.getUserName().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400
        }

        User existingUser = userService.findByUserName(user.getUserName());
        if (existingUser != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 (user exists)
        }

        User savedUser = userService.saveNewUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED); // 201
    }
}
