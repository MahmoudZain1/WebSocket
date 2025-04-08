package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.ChatMessage;
import com.example.websocketdemo.config.CustomUserRegistry;
import com.example.websocketdemo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ChatController {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private CustomUserRegistry userRegistry;

	@MessageMapping("/chat.sendMessage")
	public void sendMessage(@Payload ChatMessage chatMessage) {
		String recipient = chatMessage.getRecipient();
		String sender = chatMessage.getSender();

		if (recipient != null && !recipient.isEmpty()) {
			User user = userRegistry.getUserByUsername(recipient);
			if (user != null) {
				messagingTemplate.convertAndSendToUser(
						recipient,
						"/queue/messages",
						chatMessage
				);
			}
		} else {
			messagingTemplate.convertAndSend("/topic/public", chatMessage);
		}
	}


	@MessageMapping("/chat.register")
	public void register(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		String username = chatMessage.getSender();
		String sessionId = headerAccessor.getSessionId();

		headerAccessor.getSessionAttributes().put("username", username);

		User user = new User(username, sessionId);
		userRegistry.register(user);

		chatMessage.setContent(username + " joined the chat");
		messagingTemplate.convertAndSend("/topic/public", chatMessage);

		broadcastUserList();
	}

	@MessageMapping("/chat.leave")
	public void leave(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		String username = chatMessage.getSender();
		String sessionId = headerAccessor.getSessionId();

		userRegistry.removeBySessionId(sessionId);

		chatMessage.setContent(username + " left the chat");
		messagingTemplate.convertAndSend("/topic/public", chatMessage);

		broadcastUserList();
	}

	@MessageMapping("/chat.userlist")
	public void getUserList() {
		broadcastUserList();
	}

	private void broadcastUserList() {
		Map<String, String> usernameMap = new HashMap<>();
		userRegistry.getAllUsers().forEach((username, user) -> {
			usernameMap.put(username, username);
		});
		messagingTemplate.convertAndSend("/topic/users", usernameMap);
	}
}