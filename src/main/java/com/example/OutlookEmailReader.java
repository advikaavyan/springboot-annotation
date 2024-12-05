package com.example;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class OutlookEmailReader {
    public static void main(String[] args) {
        // Outlook IMAP settings
        String host = "imap.secureserver.net";
        String port = "993";
        String username = "hr@spireintech.com"; // Replace with your Outlook email
        String password = "Vijayvilas23";         // Replace with your password (use app password if MFA is enabled)

        // Set up properties
        Properties properties = new Properties();
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", port);
        properties.put("mail.imap.starttls.enable", "true");
        properties.put("mail.imap.ssl.enable", "true");

        // Create session
        Session session = Session.getDefaultInstance(properties);
        try {
            // Connect to the store
            Store store = session.getStore("imap");
            store.connect(host, username, password);

            // Open the inbox folder
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // Fetch unread messages
           // Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
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
    } private static void saveAttachment(MimeBodyPart part, String fileName) throws IOException, MessagingException {
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA " +fileName);
        File file = new File("downloaded_" + fileName);
        part.saveFile(file);
    }
}
