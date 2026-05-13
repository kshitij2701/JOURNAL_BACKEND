package com.example.journalapp.controller;

import com.example.journalapp.cache.AppCache;
import com.example.journalapp.entity.User;
import com.example.journalapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AppCache appCache;

    @Autowired
    private UserService userService;

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers() {

        try {
            List<User> users = userService.getAll();
            if (users.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body("No users found");
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch users");
        }
    }

    @PostMapping("/create-admin-user")
    public ResponseEntity<?> createAdminUser(@RequestBody User user) {

        try {
            if (user.getUserName() == null ||
                    user.getUserName().isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Username is required");
            }
            if (user.getPassword() == null ||
                    user.getPassword().isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Password is required");
            }
            userService.saveAdmin(user);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Admin user created successfully");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create admin user");
        }
    }

    @GetMapping("/clear-app-cache")
    public ResponseEntity<?> clearAppCache() {

        try {
            appCache.init();
            System.out.println("API HIT");
            return ResponseEntity.ok(
                    "Application cache refreshed successfully"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to refresh cache : " + e.getMessage());
        }
    }

}