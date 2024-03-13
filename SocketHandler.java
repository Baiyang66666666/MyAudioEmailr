package com.com6103.email.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SocketHandler extends TextWebSocketHandler {

    Logger logger = LoggerFactory.getLogger(SocketHandler.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * sets up logger with a message
     * @param session session
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        var url = session.getUri().getPath();
        try {
            var userId = extractNumberFromPath(url);
            sessions.put(userId, session);
            logger.info("Connection established with user: " + userId);
        }catch (Exception e){
            logger.error("Connection established with user: " );
        }
    }

    /**
     * Processes text messages sent by the client
     * @param session session
     * @param message message from the client
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("Received message");
    }

    /**
     * sets up a logger with a message when connection closed
     * @param session session
     * @param status status that the connection closed with
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("Connection closed with status: " + status);
    }

    /**
     * sends a given message to a user
     * @param messages a message to be sent
     * @param userId a user ID of a receiver
     */
    public void sendMessageToUser(List<String> messages, String userId) {
        var session = sessions.get(userId);
        try {
            Map<String, List<String>> pathMap = new HashMap<>();
            pathMap.put("path", messages);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = null;
            jsonString = objectMapper.writeValueAsString(pathMap);
            session.sendMessage(new TextMessage(jsonString));
        } catch (Exception e) {
            logger.error("Failed to send message to {}: {}", session, e.getMessage());
        }
    }

    /**
     * Extracts an email ID from a given path
     * @param path a path to be extracted
     * @return email ID as a number
     * @throws Exception
     */
    private String extractNumberFromPath(String path) throws Exception {
        Pattern pattern = Pattern.compile(".*/(\\d+)");
        Matcher matcher = pattern.matcher(path);
        if (matcher.matches()){
            var number = matcher.group(1);
            return number;
        }
        else throw new Exception();
    }
}
