package com.example.journalapp.service;

import com.example.journalapp.entity.JournalEntry;
import com.example.journalapp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    public JournalEntry saveEntry(JournalEntry journalEntry){
        journalEntry.setDate(new java.util.Date()); // add date
        return journalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll() {
        // it will return the list of journal Entries present in the db
        System.out.println("GET /journal moved to service");
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id){
        // find by id
        return journalEntryRepository.findById(id);
    }

    public void deleteById(ObjectId id){
        // find by id
        journalEntryRepository.deleteById(id);
    }

//    public void updateJournal(ObjectId id, JournalEntry newEntry){
//    }




}

