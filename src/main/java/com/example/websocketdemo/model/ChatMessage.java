package com.example.websocketdemo.model;

public class ChatMessage {

	private String sender;
	private String recipient;
	private String content;
	private MessageType type;

	public enum MessageType {
		CHAT,
		JOIN,
		LEAVE
	}

	public ChatMessage() {
	}

	public ChatMessage(String sender, String recipient, String content, MessageType type) {
		this.sender = sender;
		this.recipient = recipient;
		this.content = content;
		this.type = type;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}
}