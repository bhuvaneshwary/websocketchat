package edu.udacity.java.nano.WebSocketChat;

import java.util.ArrayList;

public class ChatMessage {
private String sender;
private String content;
private MessageType type;
private static ArrayList<String> onlineUsers = new ArrayList<String>();



public ArrayList<String> getOnlineUsers() {
	return onlineUsers;
}

public static void addUser(String user) {
	onlineUsers.add(user);
}

public static void removeUser(String user) {
	onlineUsers.remove(user);
}

public enum MessageType{CHAT,LEAVE,JOIN}

	public ChatMessage() {
		
	}


	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
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
