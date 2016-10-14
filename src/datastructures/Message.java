package datastructures;

import java.util.Calendar;

public class Message {
	private Calendar messageDate;
	private String messageSender;
	private String messageContent;
	private int numberOfEmojis;
	public enum MessageType {
		SYSTEM_MESSAGE,
		USER_MESSAGE
	}
	public enum UserMessageType {
		LOCATION_MESSAGE,
		IMAGE_MESSAGE,
		VIDEO_MESSAGE,
		TEXT_MESSAGE
	}
	private MessageType messageType;
	private UserMessageType userMessageType;
	
	public Message(){}
	
	public Calendar getDate() {return messageDate;}
	public String getSender() {return messageSender;}
	public String getContent() {return messageContent;}
	public int getNumberOfEmojis() {return numberOfEmojis;}
	public MessageType getType() {return messageType;}
	public UserMessageType getUserMessageType() {return userMessageType;}
	
	public void setDate(Calendar _cal) {this.messageDate = _cal;}
	public void setSender(String _sender) {this.messageSender = _sender;}
	public void setContent(String _content) {this.messageContent = _content;}
	public void setNumberOfEmojis(int _num) {this.numberOfEmojis = _num;}
	public void setMessageType(MessageType _type) {this.messageType = _type;}
	public void setUserMessageType(UserMessageType _type) {this.userMessageType = _type;}
}