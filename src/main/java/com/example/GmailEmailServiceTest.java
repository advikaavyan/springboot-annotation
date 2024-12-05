package com.example;

public class GmailEmailServiceTest {
    private static final String USERNAME = "avyanadvika";
    private static final String PASSWORD = "mlck vvts cygx lylv";

    public static void main(String[] args) {
        EmailService emailService = new GmailEmailService(USERNAME, PASSWORD);
        emailService.send("avyanadvika@gmail.com", "Sec Sub", "First Email");
        emailService. downloadAttachment();

    }
}