package com.com6103.email.handler;

import org.springframework.stereotype.Component;

import com.com6103.email.entity.Account;
import com.com6103.email.entity.Mail;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Component
public class SmtpHandler {


    public Session session;
    public Store store;
    public Folder inbox;
    final static String HTML = "text/html";
    final static String TEXT = "text/plain";

    /**
     * Connects with a given SMTP
     * @param account account information
     */
    public void smtpConnect(Account account) {
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.store.protocol", "smtp");
        props.setProperty("mail.smtp.host", account.getSmtp());
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account.getAddress(), account.getPassword());
            }
        });
//        try {
//            store = session.getStore();
//            store.connect(account.getAddress(), account.getPassword());
//            inbox = store.getFolder("INBOX");
//            inbox.open(Folder.READ_WRITE);
//        } catch (NoSuchProviderException e) {
//            throw new RuntimeException(e);
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
    }

    /**
     * Sends an mail
     * @param mail a mail to send
     * @throws MessagingException
     */
    public void sendMail(Mail mail) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mail.getFrom_user()));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(mail.getTo_user()));
        message.setSubject(mail.getSubject());
        var multipart = new MimeMultipart();
        multipart = setContent(mail.getContent(), TEXT, multipart);
        message.setContent(multipart);
        Transport.send(message);

    }

    /**
     * Breaks the connection
     * @throws MessagingException
     */
    public void disconnect() throws MessagingException {
        inbox.close(false);
        store.close();
    }

    /**
     * Sets given contents to a MimeMultipart object
     * @param plainText plain texts
     * @param contentType content type
     * @param multipart object to be set with given contents
     * @return MimeMultipart object set with given contents
     * @throws MessagingException
     */
    private MimeMultipart setContent(String plainText, String contentType, MimeMultipart multipart) throws MessagingException {
        var emailContent = new MimeBodyPart();
        emailContent.setContent(plainText, contentType);
        emailContent.setDisposition(MimeBodyPart.INLINE);
        multipart.addBodyPart(emailContent);
        return multipart;
    }

    /**
     * Sets attachment to a MimeMultiple object
     * @param filePath file path where the attachment is stored
     * @param multipart an object to be set with an attachment
     * @return a MimeMultiple object set with an attachment
     * @throws MessagingException
     */
    private MimeMultipart setAttachment(String filePath, MimeMultipart multipart) throws MessagingException {
        var emailAttachment = new MimeBodyPart();
        DataSource source = new FileDataSource(filePath);
        emailAttachment.setDataHandler(new DataHandler(source));
        // Even if this parameter is not set, the attachment will be sent by default
        emailAttachment.setDisposition(MimeBodyPart.ATTACHMENT);
        emailAttachment.setFileName(filePath);
        multipart.addBodyPart(emailAttachment);
        return multipart;
    }
}
