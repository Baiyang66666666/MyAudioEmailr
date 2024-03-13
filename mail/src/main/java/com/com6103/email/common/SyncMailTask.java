package com.com6103.email.common;


import com.com6103.email.entity.Account;
import com.com6103.email.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SyncMailTask implements Runnable {
    private EmailService emailService;
    private Account account;
    Logger logger = LoggerFactory.getLogger(SyncMailTask.class);

    @Autowired
    public SyncMailTask(EmailService emailService) {
        this.emailService = emailService;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public void run() {
        emailService.syncEmails(account);
        logger.info("SyncMailTask Finished");
    }
}
