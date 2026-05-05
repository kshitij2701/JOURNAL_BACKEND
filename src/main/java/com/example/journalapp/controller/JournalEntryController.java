package com.example.journalapp.controller;

import com.example.journalapp.entity.JournalEntry;
import com.example.journalapp.service.JournalEntryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Autowired
    private JournalEntryService journalEntryService;

    @GetMapping("/check-db")
    public String checkDb() {
        return mongoUri;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<JournalEntry> entries = journalEntryService.getAll();

        if (entries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
        }
        return new ResponseEntity<>(entries, HttpStatus.OK); // 200
    }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry) {
        try {
            JournalEntry savedEntry = journalEntryService.saveEntry(myEntry);
            return new ResponseEntity<>(savedEntry, HttpStatus.CREATED); // 201
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400
        }
    }

    @GetMapping("/id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId) {
        Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);
        if (journalEntry.isPresent()) {
            return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable String myId) {
        try {
            journalEntryService.deleteById(new ObjectId(myId));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        }
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateJournalById(
            @PathVariable ObjectId id,
            @RequestBody JournalEntry newEntry) {

        JournalEntry journalEntryOld = journalEntryService.findById(id).orElse(null);

        if (journalEntryOld != null) {
            if (newEntry.getTitle() != null && !newEntry.getTitle().isEmpty()) {
                journalEntryOld.setTitle(newEntry.getTitle());
            }

            if (newEntry.getContent() != null && !newEntry.getContent().isEmpty()) {
                journalEntryOld.setContent(newEntry.getContent());
            }

            journalEntryService.saveEntry(journalEntryOld);

            return new ResponseEntity<>(journalEntryOld, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

