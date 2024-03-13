package com.com6103.email.controller;

import com.com6103.email.common.TaskConfig;
import com.com6103.email.service.EmailService;
import com.com6103.email.service.UserService;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.List;


//@Component
//@ServerEndpoint(value = "/scheduled/{userid}")
@Controller
public class SocketController {

//    TaskConfig taskConfig;
//    UserService userService;
//    EmailService emailService;

    Logger logger = LoggerFactory.getLogger(SocketController.class);


}
