package com.com6103.email.service;

import com.com6103.email.entity.Account;
import com.com6103.email.entity.Mail;

import java.util.List;

public interface EmailService {
    /**
     * Batch change mail status
     * @param mailIdList
     */
    void changeStatus(List<String> mailIdList);

    /**
     * Once read mail change status
     * @param emailId
     * @return
     */
    Mail browseEmail(String emailId);

    /**
     * get Recieved mail
     * @param receiverAddress
     * @return
     */
    List<Mail> listReceivedEmails(String receiverAddress);

    /**
     * sync mail by account
     * @param account
     */
    void syncEmails(Account account);

    /**
     * init mail info when login
     * @param account
     */
    void initEmails(Account account);

    /**
     * send eamil
     * @param account
     * @param mail
     */
    void sendEmail(Account account, Mail mail);

    /**
     * delete Email
     * @param mailIdList
     * @param userId
     * @return
     */
    int deleteEmail(List<String> mailIdList, String userId);

}
