package com.com6103.email.service.impl;

import com.com6103.email.dao.EmailDAO;
import com.com6103.email.dao.UserDAO;
import com.com6103.email.dao.VoiceDAO;
import com.com6103.email.entity.Account;
import com.com6103.email.entity.Mail;
import com.com6103.email.handler.ImapHandler;
import com.com6103.email.handler.SmtpHandler;
import com.com6103.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    EmailDAO emailDAO;
    UserDAO userDAO;
    VoiceDAO voiceDAO;

    SmtpHandler smtpHandler;
    ImapHandler imapHandler;

    @Autowired
    public EmailServiceImpl(EmailDAO emailDAO, UserDAO userDAO,VoiceDAO voiceDAO
            , SmtpHandler smtpHandler, ImapHandler imapHandler) {
        this.emailDAO = emailDAO;
        this.userDAO = userDAO;
        this.voiceDAO = voiceDAO;
        this.smtpHandler = smtpHandler;
        this.imapHandler = imapHandler;
    }

    @Override
    @Transactional
    public void initEmails(Account account) {
        var mailList = imapHandler.getCertainNumOfMail(account, 10);
        emailDAO.insertEmails(mailList);
    }

    @Override
    public Mail browseEmail(String emailId) {
        emailDAO.setRead(emailId);
        var mail = emailDAO.getMailById(emailId);
        return mail;
    }

    @Override
    public List<Mail> listReceivedEmails(String receiverAddress) {
        var mailList = emailDAO.getReceiveList(receiverAddress);
        return mailList;
    }

    @Override
    @Transactional
    public void syncEmails(Account account) {
        Mail mail = emailDAO.getLatestReceivedMail(account.getAddress());
        if(mail != null){
            String messageId = mail.getMessage_id();
            var mailList = imapHandler.syncEmails(account, messageId);
            if (mailList.size() != 0)
                emailDAO.insertEmails(mailList);
        }

    }

    @Override
    @Transactional
    public void changeStatus(List<String> mailIdList){
       for (String mailId : mailIdList){
           emailDAO.setRead(mailId);
       }
    }

    @Override
    @Transactional
    public void sendEmail(Account account, Mail mail) {
        smtpHandler.smtpConnect(account);
        try {
            smtpHandler.sendMail(mail);
//            emailDAO.insertEmail(mail);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public int deleteEmail(List<String> mailIdList, String userId) {
        int i = 0;
        for (String mailId : mailIdList){
                i += emailDAO.deleteEmail(mailId,userId);
        }
        return i;
    }


}
