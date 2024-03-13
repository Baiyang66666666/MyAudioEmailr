

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class JavaMailDemo {
    public static void main(String[] args) {
        String senderAddress = "xx@gmail.com"; // Username of your account
        String receiverAddress = "xx@gmail.com";
        String password = "";                   // app password
        String filePath = "";
        sendMail(senderAddress, receiverAddress, password , filePath);
        receiveMail(senderAddress,password);
    }

    public static void sendMail(String senderAddress
            , String receiverAddress
            , String password
            , String filePath
    ) {
        Properties properties = System.getProperties();
        // Is authorization required
        properties.setProperty("mail.smtp.auth", "true");
        // Set the smtp server
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        // Set the port
        properties.setProperty("mail.smtp.port", "465");
        // Set SSL
        properties.setProperty("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        // Get Session
        Session session = Session.getInstance(properties,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderAddress, password);
                    }
                });
        try {
            // Create a MimeMessage to send email
            MimeMessage message = new MimeMessage(session);
            // Set the email address of the sender
            message.setFrom(new InternetAddress(senderAddress));
            // Set the email address of the receiver
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiverAddress));
            // Set the title of the mail
            message.setSubject("This is the Subject Line");
            //Set the content
//            message.setText("This is the content");
            // Set the tml content and attachment
            var multipart = new MimeMultipart();
            multipart = setHtmlContent("<h1>jsoup: Java HTML Parser</h1>", multipart);
            multipart = setAttachment(filePath, multipart);
            message.setContent(multipart);
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public static void receiveMail(String senderAddress, String password) {

        Properties prop = new Properties();
        // Set the imap host
        prop.setProperty("mail.imap.host", "imap.gmail.com");
        // Set the port of the host
        prop.setProperty("mail.imap.port", "993");
        // Set the ssl
        prop.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // Create a session.
        // And when using imap, username and pwd
        // are not stored in the Authenticator
        Session session = Session.getInstance(prop);
        try {
            // Get the store object ,and set the protocol and username/pwd
            Store store = session.getStore("imap");
            store.connect(senderAddress, password);
            // Get the
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
//            System.out.println("Numbers of mail: " + folder.getMessageCount());
//            System.out.println("Numbers of mail(unread): " + folder.getUnreadMessageCount());
//            System.out.println("Numbers of mail(new mail): " + folder.getNewMessageCount());
//            System.out.println("Numbers of deleted mail: " + folder.getDeletedMessageCount());
            // mails in the folder
            Message[] messages = folder.getMessages();
            for (Message message : messages) {
                printMessage((MimeMessage) message);
            }
            folder.close(true);
            store.close();
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    private static void printMessage(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {
        System.out.println("Subject: " + MimeUtility.decodeText(msg.getSubject()));
        Address[] froms = msg.getFrom();
        // Get the sender's address
        InternetAddress address = (InternetAddress) froms[0];
        String personal = address.getPersonal();
        // Name of the sender, return null if we can not get the sender's name
        String from = personal == null ? address.getAddress() : (MimeUtility.decodeText(personal) + " <" + address.getAddress() + ">");
        System.out.println("From: " + from);
    }

    private static MimeMultipart setHtmlContent(String htmlContent, MimeMultipart multipart) throws MessagingException {
        var emailContent = new MimeBodyPart();
        emailContent.setContent(htmlContent, "text/html");
        emailContent.setDisposition(MimeBodyPart.INLINE);
        multipart.addBodyPart(emailContent);
        return multipart;
    }

    private static MimeMultipart setAttachment(String filePath, MimeMultipart multipart) throws MessagingException {
        var emailAttachment = new MimeBodyPart();
        DataSource source = new FileDataSource(filePath);
        emailAttachment.setDataHandler(new DataHandler(source));
        // Even if this parameter is not set, the attachment will be sent by default
        emailAttachment.setDisposition(MimeBodyPart.ATTACHMENT);
        //TODO Need to do some adjustments
        emailAttachment.setFileName(filePath);
        multipart.addBodyPart(emailAttachment);
        return multipart;
    }


}

