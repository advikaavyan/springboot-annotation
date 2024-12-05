package com.example;

public interface EmailService {
    public void send(String email, String subject, String content);

    public void downloadAttachment();
}
