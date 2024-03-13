package com.com6103.email.controller;

import com.alibaba.fastjson.JSONObject;
import com.com6103.email.common.TaskConfig;
import com.com6103.email.entity.Mail;
import com.com6103.email.service.EmailService;
import com.com6103.email.service.UserService;
import com.com6103.email.utils.Utils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/inbox")
public class InboxController {

    EmailService emailService;
    UserService userService;
    TaskConfig taskConfig;

    @Autowired
    public InboxController(EmailService emailService, UserService userService,TaskConfig taskConfig) {
        this.emailService = emailService;
        this.userService = userService;
        this.taskConfig = taskConfig;
    }

    /**
     * Initialises an inbox page
     * @param model a model to be set up with attributes
     * @param httpSession http session
     * @return inbox page
     */
    @GetMapping("/email")
    public String inbox(Model model, HttpSession httpSession) {
        var userId = httpSession.getAttribute("userId").toString();
        var account = userService.getUserById(userId);
        var receiveList =emailService.listReceivedEmails(account.getAddress());

        taskConfig.startSyncMailTask(account);

        model.addAttribute("accountInfo", account);
        model.addAttribute("receiveList", receiveList);
        return "emailBox";
    }

    /**
     * Synchronises and update emails
     * @param model model
     * @param httpSession http session
     * @return the result whether the request has been passed
     */
    @GetMapping("/sync")
    public ResponseEntity<String> getNewEmails(Model model , HttpSession httpSession) {
        var userId = httpSession.getAttribute("userId").toString();
        var account = userService.getUserById(userId);
        emailService.syncEmails(account);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }


    /**
     * Performs a read function
     * @param model a model to be set up with an attribute
     * @param emailId email ID to be read
     * @return launch a read mail function
     */
    @GetMapping("/read")
    public String readEmail(Model model, @RequestParam(value = "emailId") String emailId) {
        var mail = emailService.browseEmail(emailId);
        model.addAttribute("mailInfo", mail);
        return "readMail";
    }

    /**
     * Change the read status
     */
    @PostMapping("/status")
    public void changeMailStatus(@RequestParam(value = "emailId") List<String> emailId) {
        emailService.changeStatus(emailId);
    }


    /**
     * Performs a function to show detail of an email
     * @param session http session
     * @param model a model to be set up with the attributes
     * @param emailId email ID to be shown details of
     * @return mail detail
     */
    @GetMapping(value = "/showDetail")
    public String showDetailLoad(HttpSession session, Model model, @RequestParam(value = "mailId") String emailId) {
        String userId = session.getAttribute("userId").toString();
        var account = userService.getUserById(userId);
        var mail = emailService.browseEmail(emailId);
        ArrayList mailList = new ArrayList();
        mailList.add(mail.getMail_id());
        emailService.changeStatus(mailList);
        String content = mail.getContent();
        model.addAttribute("accountInfo",account);
        model.addAttribute("emailId", emailId);
        model.addAttribute("mailContent", content);
        return "mailDetail";
    }

    /**
     * Performs a delete function
     * @param emailId email ID to be deleted
     * @param httpSession httpsession
     * @return read mail
     */
    @PostMapping(value = "/delete", produces = { "application/json;charset=UTF-8" })
    public String deleteEmail(@RequestBody List<String> emailId,HttpSession httpSession) {
        String userId = httpSession.getAttribute("userId").toString();

        JSONObject result = new JSONObject();
        try {
            emailService.deleteEmail(emailId, userId);
        } catch (Exception e) {
            result.put("success",false);
            throw new RuntimeException(e);
        }
        result.put("success",true);
        return "readMail";
    }

}
