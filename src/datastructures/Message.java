package datastructures;

import java.util.Date;

public class Message {
	private Date messageDate;
	private String messageSender;
	private String messageContent;
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
	
	public Date getDate() {return messageDate;}
	public String getSender() {return messageSender;}
	public String getContent() {return messageContent;}
	public MessageType getType() {return messageType;}
	public UserMessageType getUserMessageType() {return userMessageType;}
	public void setDate(Date _date) {this.messageDate = _date;}
	public void setSender(String _sender) {this.messageSender = _sender;}
	public void setContent(String _content) {this.messageContent = _content;}
	public void setMessageType(MessageType _type) {this.messageType = _type;}
	public void setUserMessageType(UserMessageType _type) {this.userMessageType = _type;}
}
