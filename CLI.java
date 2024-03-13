package com.com6103.email.cli;

import com.com6103.email.dao.EmailDAO;
import com.com6103.email.entity.Account;
import com.com6103.email.entity.TTSRequest;
import com.com6103.email.service.impl.EmailServiceImpl;
import com.com6103.email.service.impl.UserServiceImpl;
import com.com6103.email.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.client.RestTemplate;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

@ShellComponent
@ShellCommandGroup("msg")
public class CLI {

    @Value("${mail.name}")
    private String name;
    @Value("${mail.sender}")
    private String senderAddress;
    @Value("${mail.receiver}")
    private String receiverAddress;
    @Value("${mail.password}")
    private String password;
    // Macos bbtzgijwwyrtfhkq
    // Windows zwxqqjkrakhfoucy
    @Value("${mail.smtp}")
    private String smtp;
    @Value("${mail.imap}")
    private String imap;

    @Autowired
    UserServiceImpl userService;
    @Autowired
    EmailDAO emailDAO;
    @Autowired
    EmailServiceImpl emailService;

    //TODO Send a JSON string
    @ShellMethod(key = "sd")
    public void sendMessage() {
        TempEmail mail = new TempEmail(name
                , senderAddress
                , receiverAddress
                , password
                , smtp
                , imap
        );
        var ttsRequest = new TTSRequest("123456789", "This is a TTS.test for connecting","0");
        var httpEntity = Utils.setHttpEntity(ttsRequest);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:8080/"
                , HttpMethod.POST
                , httpEntity
                , String.class
        );
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
            var path = jsonNode.get("content").toString();
            playAudio(path);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Send Successfully");
//        String path = responseEntity.getBody().getContent();


    }


    @ShellMethod(key = "TSUserservice")
    public void testUserService() {
        var accountList = userService.getAccountList("202303101557-mjkyh");
    }

    @ShellMethod(key = "ts login")
    public void testLoginController() {
        var account = new Account("Mingqing", "mzhang122@sheffield.ac.uk", "hookakrvavdfxmqo"
                , "0", "smtp.gmail.com", "imap.gmail.com", "202303101557-mjkyh");
        emailService.initEmails(account);
        userService.insertAccount(account);
    }

    @ShellMethod(key = "path")
    public void classPath() {
        String classpath = System.getProperty("java.class.path");
        System.out.println(classpath);
    }


    @ShellMethod(key = "unread")
    public void testUnread() {
        var list = emailDAO.getUnreadMail("crowds723@gmail.com");
        System.out.println(list.size());
    }

    /**
     * Test Audio
     *
     * @param path
     */
    @ShellMethod(key = "pa")
    private void playAudio(@ShellOption(defaultValue = "/Users/crowds/COM6103_Project/123456789.wav") String path) {
        try {
            File audioFile = new File(path);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            Thread.sleep(clip.getMicrosecondLength() / 1000);
            clip.close();
            audioInputStream.close();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
