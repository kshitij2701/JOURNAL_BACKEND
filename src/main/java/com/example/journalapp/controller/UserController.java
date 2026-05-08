package com.example.journalapp.controller;

import com.example.journalapp.entity.JournalEntry;
import com.example.journalapp.entity.User;
import com.example.journalapp.service.JournalEntryService;
import com.example.journalapp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers(){
        List<User> users = userService.getAll();

        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
        }

        return new ResponseEntity<>(users, HttpStatus.OK); // 200
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {

        if (user.getUserName() == null || user.getUserName().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400
        }

        User existingUser = userService.findByUserName(user.getUserName());
        if (existingUser != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 (user exists)
        }

        User savedUser = userService.saveEntry(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED); // 201
    }

    // ✅ UPDATE USER
    @PutMapping("/{userName}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable String userName) {

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

        User updatedUser = userService.saveEntry(userInDb);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK); // 200
    }


}

