package com.com6103.email.handler;

import com.com6103.email.entity.Account;
import com.com6103.email.entity.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class ImapHandler {

    Logger logger = LoggerFactory.getLogger(ImapHandler.class);
    private Store store;
    private Folder inbox;

    /**
     * Connects with a given imap
     * @param account account information
     */
    private void imapConnect(Account account) {
        Properties props = new Properties();
        props.setProperty("mail.imap.auth", "true");
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.host", account.getImap());
        props.setProperty("mail.imap.port", "993");
        props.put("mail.imap.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(props);
        try {
            store = session.getStore();
            store.connect(account.getAddress(), account.getPassword());
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
        } catch (NoSuchProviderException e) {
            logger.error("NoSuchProvider");
        } catch (MessagingException e) {
            logger.error("Connect error");
            throw new RuntimeException(e);
        }


    }

    /**
     * Breaks the connection
     */
    private void disconnect() {
        try {
            inbox.close();
            store.close();
        } catch (Exception e) {
            logger.error("Exception");
        }
    }

    /**
     * Gets unread messages
     * @param account account information
     * @return unread message list
     */
    public Message[] getUnReadMessage(Account account) {
        imapConnect(account);
        FlagTerm recentFlagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        Message[] search = new Message[0];
        try {
            search = inbox.search(recentFlagTerm);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        disconnect();
        return search;
    }


    /**
     * Synchronizes with the email account and updates the emails
     * @param account account information
     * @param messageId message ID
     * @return new mail list
     */
    public ArrayList<Mail> syncEmails(Account account, String messageId) {
        imapConnect(account);
        var mimeList = new ArrayList<MimeMessage>();
        var mailList = new ArrayList<Mail>();
        int totalMessages = 0;
        try {
            totalMessages = inbox.getMessageCount();
            for (int i = totalMessages; i > 0; i--) {
                var message = (MimeMessage) inbox.getMessage(i);
                var serverMsgId = message.getMessageID();
                if (serverMsgId.equals(messageId)) {
                    break;
                }
                mimeList.add(message);
            }
            mailList = convertMimeToMail(mimeList, account);
        } catch (MessagingException e) {
            logger.error("syncEmails: totalMessages" + totalMessages);
            throw new RuntimeException(e);
        }
        disconnect();
        return mailList;
    }

    /**
     * Gets the given number of the mails and returns a list of them
     * @param account account information
     * @param num number of the mail to get
     * @return a mail list with a given number
     */
    public ArrayList<Mail> getCertainNumOfMail(Account account, int num) {
        imapConnect(account);
        var mailList = new ArrayList<Mail>();
        int totalMessages = 0;
        Message[] messages;
        try {
            totalMessages = inbox.getMessageCount();
            int startMessage = Math.max(1, totalMessages - num + 1);
            messages = inbox.getMessages(startMessage, totalMessages);
            for (int i = messages.length - 1; i >= 0; i--) {
                Message message = messages[i];
                setMailObject(mailList, (MimeMessage) message, account);
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        disconnect();
        return mailList;
    }

    /**
     * Converts a MimeMessage list to a mail list
     * @param messageList MimeMessage list to convert
     * @param account account information
     * @return a mail list converted from the given MimeMessage list
     */
    private ArrayList<Mail> convertMimeToMail(List<MimeMessage> messageList, Account account) {
        var mailList = new ArrayList<Mail>();
        for (MimeMessage message : messageList) {
            setMailObject(mailList, message, account);
        }
        return mailList;
    }

    /**
     * Sets values to the mail object according to given message and account
     * @param mailList a mail list to set the information
     * @param message a message that provides values
     * @param account an account that provides values
     */
    private void setMailObject(ArrayList<Mail> mailList, MimeMessage message, Account account) {
        var mail = new Mail();
        try {
            mail.setMessage_id(message.getMessageID());
            mail.setSubject(message.getSubject());
            mail.setContent(parseMailContent(message.getContent()));
            mail.setFrom_user(message.getSender().toString());
            mail.setTo_user(account.getAddress());
            mail.setRead_flag(getMailFlag(message));
            mail.setDelete_flag(getDeleteFlag(message));
            mail.setCreate_time(message.getSentDate());
            mailList.add(mail);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }
    }

    /**
     * Parses a given mail content to String
     * @param content mail content
     * @return mail content that have been parsed to String
     * @throws MessagingException
     * @throws IOException
     */
    private String parseMailContent(Object content) throws MessagingException, IOException {
        var contentStr = new StringBuilder();
        if (content instanceof String) {
            var emailContent = (String) content;
            contentStr.append(emailContent);
        } else if (content instanceof MimeMultipart) {
            var multipart = (MimeMultipart) content;
            int count = multipart.getCount();
            if (count >= 2) {
                var bodyPart = (MimeBodyPart) multipart.getBodyPart(1);
                String emailContent = bodyPart.getContent().toString();
                contentStr.append(emailContent);
            } else {
                var bodyPart = (MimeBodyPart) multipart.getBodyPart(0);
                String emailContent = bodyPart.getContent().toString();
                contentStr.append(emailContent);
            }
        }
        return contentStr.toString();
    }

    /**
     * Returns whether there is an attachment
     * @param bodyPart body part to check
     * @return true if there is an attachment, otherwise false
     * @throws MessagingException
     */
    private Boolean isAttachment(MimeBodyPart bodyPart) throws MessagingException {
        String disposition = bodyPart.getDisposition();
        if (disposition != null && disposition.equalsIgnoreCase(Part.ATTACHMENT)) {
            return true;
        }
        return false;
    }


    private String getMailFlag(MimeMessage message) throws MessagingException {
        return "0";
//        var msg = (Message)message ;
//        if (msg.isSet(Flags.Flag.SEEN)) {
//            return "1";
//        } else {
//            return "0";
//        }
    }

    /**
     * Gets a delete flag: 1 or 0
     * @param message a MimeMessage to check
     * @return a delete flag (1 or 0)
     * @throws MessagingException
     */
    private String getDeleteFlag(MimeMessage message) throws MessagingException {
        if (message.getFlags().contains(Flags.Flag.DELETED)) {
            return "1";
        } else {
            return "0";
        }
    }


}
