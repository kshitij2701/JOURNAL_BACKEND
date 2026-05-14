package com.example.journalapp.service;

import com.example.journalapp.entity.JournalEntry;
import com.example.journalapp.entity.User;
import com.example.journalapp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public JournalEntry saveEntry(JournalEntry journalEntry, String userName) {
        try {
            User user = userService.findByUserName(userName);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(saved);
            userService.SaveUser(user);
            return saved;

        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            throw new RuntimeException("Failed to save journal entry");
        }
    }

    public JournalEntry saveEntry(JournalEntry journalEntry) {
        return journalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll() {
        // it will return the list of journal Entries present in the db
        System.out.println("GET /journal moved to service");
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        // find by id
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public void deleteById(ObjectId id, String userName) {
        try {
            User user = userService.findByUserName(userName);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            boolean removed = user.getJournalEntries().removeIf(
                    x -> x.getId().equals(id)
            );
            if (!removed) {
                throw new RuntimeException("Journal entry not found in user's list");
            }
            userService.SaveUser(user);
            journalEntryRepository.deleteById(id);

        } catch (Exception e) {
            System.out.println("Delete transaction failed: " + e.getMessage());
            throw new RuntimeException("Failed to delete journal entry");
        }
    }

//    public void updateJournal(ObjectId id, JournalEntry newEntry){
//    }


}

