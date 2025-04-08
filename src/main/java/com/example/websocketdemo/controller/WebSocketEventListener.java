package com.example.websocketdemo.controller;

import com.example.websocketdemo.config.CustomUserRegistry;
import com.example.websocketdemo.model.ChatMessage;
import com.example.websocketdemo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private SimpMessageSendingOperations messagingTemplate;
    private CustomUserRegistry userRegistry;

    public WebSocketEventListener(CustomUserRegistry userRegistry, SimpMessageSendingOperations messagingTemplate) {
        this.userRegistry = userRegistry;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("A new WebSocket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        User user = userRegistry.getUserBySessionId(sessionId);
        if (user != null) {
            logger.info("The user has disconnected : ", user.getUsername());

            userRegistry.removeBySessionId(sessionId);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(user.getUsername());
            chatMessage.setContent(user.getUsername() +"The user has left" );

            Map<String, String> onlineUsers = new HashMap<>();
            userRegistry.getAllUsers().forEach((key, value) -> onlineUsers.put(key, value.getUsername()));

            messagingTemplate.convertAndSend("/topic/public", chatMessage);
            messagingTemplate.convertAndSend("/topic/users", onlineUsers);
        }
    }
}