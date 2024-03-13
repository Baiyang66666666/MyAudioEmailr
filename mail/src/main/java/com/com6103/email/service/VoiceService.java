package com.com6103.email.service;

import com.com6103.email.entity.Account;
import com.com6103.email.entity.Mail;

import java.util.List;

public interface VoiceService {
    /**

     Retrieves a list of unread emails associated with the specified account.
     @param account the account to retrieve unread emails for
     @return a list of unread emails associated with the specified account
     */
    List<Mail> getUnreadEmails(Account account);

    /**

     Converts the specified email content to voice using text-to-speech technology, and saves the resulting voice file.
     @param mailId the ID of the email to convert to voice
     @param mailContent the content of the email to convert to voice
     @param voiceType the type of voice to use for the conversion
     */
    void getVoiceFromTTS(String mailId, String mailContent,String voiceType);

    /**

     Retrieves a list of unread emails associated with the specified account, and plays them back using the specified voice type.
     @param account the account to retrieve and play unread emails for
     @param voiceType the type of voice to use for the playback
     @return a list of strings representing the content of the emails that were played back
     */
    List<String> playUnreadEmails(Account account,String voiceType);
    /**

     Plays back the specified email using the specified user ID.
     @param mailId the ID of the email to play back
     @param userId the ID of the user to play the email back for
     @return a string representing the content of the email that was played back
     */
    String playMail(String mailId,String userId);

    /**

     Transfers unread emails associated with the specified account to voice messages, using the specified voice type.
     @param account the account to transfer unread emails for
     @param voiceType the type of voice to use for the transfer
     */
    void transferUnreadMail(Account account, String voiceType);
}
