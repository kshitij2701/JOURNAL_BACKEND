package com.example.journalapp.controller;

import com.example.journalapp.api.response.WeatherResponse;
import com.example.journalapp.entity.User;
import com.example.journalapp.repository.UserRepository;
import com.example.journalapp.service.UserService;
import com.example.journalapp.service.WeatherService;
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

    @Autowired
    private WeatherService weatherService;

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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

    @GetMapping
    public ResponseEntity<?> greeting() {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // Edge Case 1 -> User not authenticated
            if (authentication == null ||
                    !authentication.isAuthenticated()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("User is not authenticated");
            }
            String userName = authentication.getName();
            // Edge Case 2 -> Invalid username
            if (userName == null || userName.isBlank()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Invalid username");
            }
            WeatherResponse weatherResponse =
                    weatherService.getWeather("Mumbai");
            StringBuilder message =
                    new StringBuilder("Hi, " + userName);

            // Edge Case 3 -> Weather service unavailable
            if (weatherResponse == null) {
                message.append(
                        ". Unable to fetch weather details currently."
                );

                return ResponseEntity
                        .status(HttpStatus.PARTIAL_CONTENT)
                        .body(message.toString());
            }
            // Edge Case 4 -> Weather current object null
            if (weatherResponse.getCurrent() == null) {
                message.append(
                        ". Weather data incomplete."
                );
                return ResponseEntity
                        .status(HttpStatus.PARTIAL_CONTENT)
                        .body(message.toString());
            }
            // Success Case
            message.append(", weather in Mumbai feels like ")
                    .append(weatherResponse.getCurrent().getFeelslike())
                    .append("°C");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(message.toString());

        } catch (Exception e) {
            // Edge Case 5 -> Unexpected server error
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong: " + e.getMessage());
        }

    }



}

