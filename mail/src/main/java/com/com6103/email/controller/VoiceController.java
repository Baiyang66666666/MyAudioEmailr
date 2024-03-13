package com.com6103.email.controller;

import com.alibaba.fastjson.JSONObject;
import com.com6103.email.entity.Mail;
import com.com6103.email.handler.SocketHandler;
import com.com6103.email.service.EmailService;
import com.com6103.email.service.UserService;
import com.com6103.email.service.VoiceService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/tts")
public class VoiceController {
    VoiceService voiceService;
    EmailService emailService;
    RestTemplate restTemplate;
    UserService userService;
    SocketHandler socketHandler;

    Logger logger = LoggerFactory.getLogger(VoiceController.class);

    @Autowired
    public VoiceController(SocketHandler socketHandler, VoiceService voiceService, EmailService emailService, RestTemplate restTemplate, UserService userService) {
        this.voiceService = voiceService;
        this.emailService = emailService;
        this.restTemplate = restTemplate;
        this.userService = userService;
        this.socketHandler = socketHandler;
    }

    /**
     * Returns a path of a content to be played, which is specified by a given email ID
     * @param emailId email ID of an email to be played
     * @param httpSession
     * @return a path of a content to be played
     */
    @PostMapping(value = "/getVoice", consumes = "application/x-www-form-urlencoded")
    public String playMail(@RequestParam("emailId") String emailId, HttpSession httpSession) {
        String userId = httpSession.getAttribute("userId").toString();
        var resourcePath = voiceService.playMail(emailId, userId);
        var mailIdList = new ArrayList<String>();
        mailIdList.add(emailId);
        emailService.changeStatus(mailIdList);
        return resourcePath;
    }

    /**
     * Sends a paths of unread emails as a message list to a user
     * @param httpSession
     * @return the result whether the request have been passed successfully
     */
    @PostMapping(value = "/unread")
    public JSONObject playUnreadMail(HttpSession httpSession) {
        String userId = httpSession.getAttribute("userId").toString();
        var account = userService.getUserById(userId);
        String voiceType = userService.getUserScheduleById(userId).getVoice_type();
        List<String> pathList = voiceService.playUnreadEmails(account, voiceType);
        var unreadList = voiceService.getUnreadEmails(account);
        // Send the email one by one
        var mailIdList = new ArrayList<String>();
        var msgList = new ArrayList<String>();
        for (Mail mail : unreadList) {
            var path = voiceService.playMail(mail.getMail_id(), userId);
            msgList.add(path);
            mailIdList.add(mail.getMail_id());
            socketHandler.sendMessageToUser(msgList, userId);
        }
        emailService.changeStatus(mailIdList);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("state", "success");
        return jsonObject;
    }

    /**
     * Sends a paths of specified emails as a messsage list to a user
     * @param httpSession
     * @param emailList an email list to be played
     * @return the result whether the request have been passed successfully
     */
    @PostMapping(value = "/read", produces = { "application/json;charset=UTF-8" })
    public JSONObject playSpecificMail(HttpSession httpSession, @RequestBody List<String> emailList) {
        String userId = httpSession.getAttribute("userId").toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("state", "success");
        var msgList = new ArrayList<String>();
        var mailIdList = new ArrayList<String>();
        emailService.changeStatus(emailList);
        for (String id : emailList) {
            var mail = emailService.browseEmail(id);
            var path = voiceService.playMail(mail.getMail_id(), userId);
            msgList.add(path);
            mailIdList.add(mail.getMail_id());
            socketHandler.sendMessageToUser(msgList, userId);
        }
        emailService.changeStatus(mailIdList);
        return jsonObject;
    }

    /**
     * Send email to SMTP server
     * @param subject a subject of an email to be sent
     * @param content a content of an email to be sent
     * @param to_user a receiver of an email
     * @param httpSession
     * @return a result whether an email has been sent successfully
     */
    @PostMapping(value = "/send" )
    public JSONObject sendEmail(@RequestParam("subject") String subject, @RequestParam("content") String content, @RequestParam("to_user") String to_user, HttpSession httpSession) {
        String userId = httpSession.getAttribute("userId").toString();
        var account = userService.getUserById(userId);
        var mail = new Mail();
        mail.setSubject(subject);
        mail.setContent(content);
        mail.setFrom_user(account.getAddress());
        mail.setTo_user(to_user);
        mail.setRead_flag("0");
//        mail.setCreate_time();
        mail.setDelete_flag("0");
        JSONObject result = new JSONObject();
        try {
            emailService.sendEmail(account, mail);
        } catch (Exception e) {
            result.put("success", false);
            throw new RuntimeException(e);
        }
        result.put("success", true);
        return result;
    }
}
