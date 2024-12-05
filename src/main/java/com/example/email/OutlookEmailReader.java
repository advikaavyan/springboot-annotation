package com.example.email;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import java.io.*;
import java.util.Properties;

public class OutlookEmailReader {
    private static final String HOST = "imap.secureserver.net";
    private static final String PORT = "993";
    private static final String USERNAME = "hr@spireintech.com";  // Replace with your Outlook email
    private static final String PASSWORD = "Vijayvilas23";          // Replace with your password or app password
    private static final String LAST_PROCESSED_FILE = "lastProcessedEmail.txt"; // File to store last processed UID

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("mail.imap.host", HOST);
        properties.put("mail.imap.port", PORT);
        properties.put("mail.imap.starttls.enable", "true");
        properties.put("mail.imap.ssl.enable", "true");

        try {
            Session session = Session.getDefaultInstance(properties);
            Store store = session.getStore("imap");
            store.connect(HOST, USERNAME, PASSWORD);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // Get the last processed UID
            long lastProcessedUID = getLastProcessedUID();
            System.out.println("Last Processed UID: " + lastProcessedUID);

            // Use UIDFolder for fetching messages by UID
            UIDFolder uidFolder = (UIDFolder) inbox;
            Message[] messages = inbox.getMessages();
            for (Message message : messages) {
                long uid = uidFolder.getUID(message);

                // Skip already processed messages
                if (uid <= lastProcessedUID) {
                    continue;
                }

                System.out.println("Processing email with UID: " + uid);
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);

                // Process attachments if any
                if (message.isMimeType("multipart/*")) {
                    Multipart multipart = (Multipart) message.getContent();
                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart bodyPart = multipart.getBodyPart(i);


                        if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                            MimeBodyPart mimeBodyPart = (MimeBodyPart) bodyPart;
                            String fileName = mimeBodyPart.getFileName();
                            File file = new File("downloads/" + fileName); // Define the file where the attachment will be saved
                            mimeBodyPart.saveFile(file); // Save the attachment directly using a File object
                            System.out.println("Attachment saved: " + file.getAbsolutePath());
                        }


                    }
                }

                // Update the last processed UID
                saveLastProcessedUID(uid);
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Read the last processed UID from the file
    private static long getLastProcessedUID() {
        File file = new File(LAST_PROCESSED_FILE);
        if (!file.exists()) {
            return 0; // No previous UID
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return Long.parseLong(reader.readLine().trim());
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Save the last processed UID to the file
    private static void saveLastProcessedUID(long uid) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LAST_PROCESSED_FILE))) {
            writer.write(Long.toString(uid));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
