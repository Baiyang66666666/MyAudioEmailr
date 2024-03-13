package com.com6103.email.service.impl;


import com.com6103.email.dao.EmailDAO;
import com.com6103.email.dao.UserDAO;
import com.com6103.email.dao.VoiceDAO;
import com.com6103.email.entity.*;
import com.com6103.email.service.VoiceService;
import com.com6103.email.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class VoiceServiceImpl implements VoiceService {
    Logger logger = LoggerFactory.getLogger(VoiceServiceImpl.class);
    RestTemplate restTemplate;
    VoiceDAO voiceDAO;
    EmailDAO emailDAO;


    UserDAO userDAO;

    @Value("${tts.url}")
    String TTS_URL;

    @Autowired
    public VoiceServiceImpl(VoiceDAO voiceDAO, UserDAO userDAO, EmailDAO emailDAO, RestTemplate restTemplate) {
        this.voiceDAO = voiceDAO;
        this.emailDAO = emailDAO;
        this.userDAO = userDAO;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional
    public void getVoiceFromTTS(String mailId, String mailContent, String voiceType) {
        String path;
        var ttsRequest = new TTSRequest(mailId, mailContent, voiceType);
        var httpEntity = Utils.setHttpEntity(ttsRequest);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                TTS_URL
                , HttpMethod.POST
                , httpEntity
                , String.class
        );
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
            path = jsonNode.get("content").toString();
            voiceDAO.insertVoice(path, mailId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public List<String> playUnreadEmails(Account account, String voiceType) {
        var pathList = new ArrayList<String>();
        try {
            var mailList = emailDAO.getUnreadMail(account.getAddress());
            for (Mail mail : mailList) {
                var voice = voiceDAO.getVoiceByMailId(mail.getMail_id(),voiceType);
                if (!isVoiceExist(voice, voiceType)) {
                    emailDAO.getMailById(mail.getMail_id());
                    getVoiceFromTTS(mail.getMail_id(), mail.getContent(), voiceType);
                }
                pathList.add("/audioEmail/audio/" + mail.getMail_id() + voiceType + ".wav");
            }
            return pathList;
        } catch (NullPointerException e) {
            return pathList;
        }
    }

    private Boolean isVoiceExist(Voice voice, String voiceType) {
        if (voice == null) {
            return false;
        } else if (!voice.getContent().contains(voiceType)) {
            return false;
        }
        return true;
    }

    @Override
    public List<Mail> getUnreadEmails(Account account) {
        var mailList = emailDAO.getUnreadMail(account.getAddress());
        return mailList;
    }

    @Override
    @Transactional
    public String playMail(String mailId, String userId) {
        AccountSchedule accountSchedule = userDAO.getUserScheduleById(userId);
        var voice = voiceDAO.getVoiceByMailId(mailId,accountSchedule.getVoice_type());
        if (!isVoiceExist(voice,accountSchedule.getVoice_type())) {
            var mail = emailDAO.getMailById(mailId);
            getVoiceFromTTS(mail.getMail_id(), mail.getContent(), accountSchedule.getVoice_type());
        }
        return "/audioEmail/audio/" + mailId + accountSchedule.getVoice_type() + ".wav";
    }

    @Override
    @Transactional
    public void transferUnreadMail(Account account,String voiceType){
        AccountSchedule accountSchedule = userDAO.getUserScheduleById(account.getUserId());
        try {
            var mailList = emailDAO.getUnreadMail(account.getAddress());
            for (Mail mail : mailList) {
                var voice = voiceDAO.getVoiceByMailId(mail.getMail_id(),accountSchedule.getVoice_type());
                if (voice == null) {
                    emailDAO.getMailById(mail.getMail_id());
                    getVoiceFromTTS(mail.getMail_id(), mail.getContent(),voiceType);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
