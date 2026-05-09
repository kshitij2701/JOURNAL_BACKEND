package com.example.journalapp.controller;

import com.example.journalapp.entity.JournalEntry;
import com.example.journalapp.entity.User;
import com.example.journalapp.repository.UserRepository;
import com.example.journalapp.service.JournalEntryService;
import com.example.journalapp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping("/check-db")
    public String checkDb() {
        return mongoUri;
    }

//    @GetMapping
//    public ResponseEntity<?> getAllJournalEntriesOfUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String userName = authentication.getName();
//        User user = userService.findByUserName(userName);
//        List<JournalEntry> entries = user.getJournalEntries();
//
//        if (entries.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
//        }
//        return new ResponseEntity<>(entries, HttpStatus.OK); // 200
//    }

    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
        List<JournalEntry> entries = user.getJournalEntries();
        if (entries == null || entries.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(entries);

    }

    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry myEntry) {

        try {

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            JournalEntry savedEntry =
                    journalEntryService.saveEntry(myEntry, userName);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedEntry);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Failed to create journal entry: " + e.getMessage());
        }

    }

//    @GetMapping("/id/{myId}")
//    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId) {
//
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//        String userName = authentication.getName();
//        User user = userService.findByUserName(userName);
//        List<JournalEntry> collect =  user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
//        if(!collect.isEmpty()){
//            Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);
//            if (journalEntry.isPresent()) {
//                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
//            }
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//
//    }

    @GetMapping("/id/{myId}")
    public ResponseEntity<?> getJournalEntryById(@PathVariable ObjectId myId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
        boolean entryExists = user.getJournalEntries()
                .stream()
                .anyMatch(entry -> entry.getId().equals(myId));

        if (!entryExists) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Journal entry not found");
        }
        Optional<JournalEntry> journalEntry =
                journalEntryService.findById(myId);

        return journalEntry
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Journal entry not found"));

    }

//    @DeleteMapping("/id/{myId}")
//    public ResponseEntity<?> deleteJournalEntryById(@PathVariable String myId) {
//        try {
//            Authentication authentication =
//                    SecurityContextHolder.getContext().getAuthentication();
//            String userName = authentication.getName();
//            journalEntryService.deleteById(new ObjectId(myId), userName);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
//        }
//    }

        @DeleteMapping("/id/{myId}")
        public ResponseEntity<?> deleteJournalEntryById( @PathVariable String myId) {

                try {

                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    String userName = authentication.getName();
                    journalEntryService.deleteById(new ObjectId(myId), userName);
                    return ResponseEntity
                            .noContent()
                            .build();

                } catch (IllegalArgumentException e) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("Invalid journal entry ID");

                } catch (RuntimeException e) {
                    return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .body(e.getMessage());

                } catch (Exception e) {
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Something went wrong");
                }

        }

        @PutMapping("/id/{id}")
        public ResponseEntity<?> updateJournalById(@PathVariable ObjectId id, @RequestBody JournalEntry newEntry) {

            try {

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String userName = authentication.getName();
                User user = userService.findByUserName(userName);

                if (user == null) {
                    return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .body("User not found");
                }

                boolean entryExists = user.getJournalEntries()
                        .stream()
                        .anyMatch(entry -> entry.getId().equals(id));

                if (!entryExists) {
                    return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .body("Journal entry not found");
                }

                Optional<JournalEntry> optionalEntry =
                        journalEntryService.findById(id);

                if (optionalEntry.isEmpty()) {
                    return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .body("Journal entry not found");
                }

                JournalEntry old = optionalEntry.get();

                old.setTitle(
                        newEntry.getTitle() != null &&
                                !newEntry.getTitle().isEmpty()
                                ? newEntry.getTitle()
                                : old.getTitle()
                );

                old.setContent(
                        newEntry.getContent() != null &&
                                !newEntry.getContent().isEmpty()
                                ? newEntry.getContent()
                                : old.getContent()
                );

                JournalEntry updated =
                        journalEntryService.saveEntry(old);

                return ResponseEntity.ok(updated);

            } catch (Exception e) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to update journal entry");
            }
        }
}

