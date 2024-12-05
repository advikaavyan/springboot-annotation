package com.example;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class GmailEmailService implements EmailService {

    private static final String HOST = "imap.gmail.com";
    private static final String AUTH = "true";
    private static final String PORT = "587";

    private static final String STORE_PORT = "993";
    private static final String STARTTLS_ENABLE = "true";
    private static final String STORE_PROTOCOL = "imaps";

    private static final String IMAPS = "imaps";


    private final String username;
    private final String password;
    private Session session;

    public GmailEmailService(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void send(String email, String subject, String content) {
        if (hasSession()) {
            try {
                Transport.send(createMimeMessage(email, session, subject, content));
                System.out.printf("Email sent to %s%n", email);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void downloadAttachment() {


        try {
            Session emailSession = Session.getDefaultInstance(getEmailStoreProps());
            Store store = emailSession.getStore("imaps");
            store.connect(username, password);

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            Message[] messages = emailFolder.getMessages();
            for (Message message : messages) {
                if (message.getContentType().contains("multipart")) {
                    Multipart multipart = (Multipart) message.getContent();

                    for (int i = 0; i < multipart.getCount(); i++) {
                        MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            String fileName = part.getFileName();
                            saveAttachment(part, fileName);
                        }
                    }
                }
            }

            emailFolder.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void saveAttachment(MimeBodyPart part, String fileName) throws IOException, MessagingException {

        File file = new File(fileName);
        part.saveFile(file);
        System.out.println("file saved " + fileName);
    }

    private boolean hasSession() {
        if (session == null) {
            session = startSessionTLS();
        }
        if (session == null) {
            System.out.printf("Cannot start email session%n");
            return false;
        }
        return true;
    }


    private Session startSessionTLS() {

        return Session.getInstance(getEmailProps(), new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private Properties getEmailProps() {
        Properties props = new Properties();
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.auth", AUTH);
        props.put("mail.smtp.starttls.enable", STARTTLS_ENABLE);
        props.put("mail.imaps.ssl.enable", STORE_PROTOCOL);
        return props;
    }

    private Properties getEmailStoreProps() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", STORE_PROTOCOL);
        properties.put("mail.imaps.host", HOST);
        properties.put("mail.imaps.port", STORE_PORT);
        properties.put("mail.imaps.ssl.enable", "true");

  /*      properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");*/
        return properties;
    }

    private MimeMessage createMimeMessage(String email, Session session, String subject, String body) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("noreply@email.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject(subject);
        message.setText(body);
        return message;
    }
}