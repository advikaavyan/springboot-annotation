package  com.example;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class GmailAttachmentDownloader {

    private static final String HOST = "imap.gmail.com";
    private static final String USERNAME = "avyanadvika";
    /*private static final String PASSWORD = "mlck vvts cygx lylv";*/

  //  private static final String PASSWORD = "Mordhwaj4##"; //javax.mail.AuthenticationFailedException: [ALERT] Application-specific password required: https://support.google.com/accounts/answer/185833 (Failure)
    private static final String PASSWORD = "oumt pgzl dily rxju";// generate from here https://support.google.com/accounts/answer/185833#zippy=%2Cforgot-your-app-password%2Cwhy-you-may-need-an-app-password
    //https://myaccount.google.com/apppasswords?pli=1&rapt=AEjHL4PlJynoeFVzHIa1PuNs0w4lPjSeuF_XP0lO-alfUQrGVI_U08HNx7a1FLogX1Sqes7rxpQ_XYYCGp8kuerL0Th6OQhbmxzvCb3Q3HDTrNEE5mnLMtw


 /*   public static void main(String[] args) {
        EmailServiceImpl emailService = new EmailServiceImpl(USERNAME, PASSWORD);
        emailService.send("avyanadvika@gmail.com", "First Sub", "First Email");

    }*/

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");

        try {
            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore("imaps");
            store.connect(USERNAME, PASSWORD);

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
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA " +fileName);
        File file = new File("downloaded_" + fileName);
        part.saveFile(file);
    }
}
