package com.com6103.email.handler;


import com.com6103.email.entity.Mail;
import com.com6103.email.entity.TTSRequest;
import com.com6103.email.service.EmailService;
import com.com6103.email.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class TTSHandler {
    @Value("${tts.url}")
    String ttsUrl;

    EmailService emailService;

    @Autowired
    public TTSHandler(EmailService emailService){
        this.emailService = emailService;
    }

    public String sendMail(Mail mail) {
        var ttsRequest = new TTSRequest(mail.getMail_id(),mail.getContent(),"0");
        var httpEntity = Utils.setHttpEntity(ttsRequest);
        RestTemplate restTemplate = new RestTemplate();
        TTSRequest response = restTemplate.postForObject(ttsUrl, httpEntity, TTSRequest.class);
        return response.getContent();
    }


}
