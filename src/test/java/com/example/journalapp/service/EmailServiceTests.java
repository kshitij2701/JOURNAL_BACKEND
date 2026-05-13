package com.example.journalapp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTests {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendMail() {
        emailService.sendEmail("development.nd9b01@bumpmail.io", "Testing Java Mail Sender service", "Hi aap kaise hai All Ok hai na ji...");
    }
}
