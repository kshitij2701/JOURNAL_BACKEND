package com.example.journalapp.cache;

import com.example.journalapp.entity.ConfigJournalAppEntity;
import com.example.journalapp.repository.ConfigJournalAppRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    public enum keys{
        WEATHER_API
    }

    public Map<String, String> cache = new HashMap<>();

    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;



    @PostConstruct
    public void init() {

        List<ConfigJournalAppEntity> all = configJournalAppRepository.findAll();
        for (ConfigJournalAppEntity configJournalAppEntity : all) {
            cache.put(configJournalAppEntity.getKey(), configJournalAppEntity.getValue());
        }

    }
}
