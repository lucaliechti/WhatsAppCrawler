package parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import datastructures.Message;

public class MessageParser {
	
	public ArrayList<String> splitMessages(String file){
		ArrayList<String> parsedMessages = new ArrayList<String>();
		try{
			BufferedReader br = new BufferedReader (new FileReader(file));
			String line = br.readLine();
			String currentMessage = line;
			while (line != null){
				if(line.length() >= 20 && line.substring(0,20).matches("\\d{2}\\.\\d{2}.\\d{2},\\s\\d{2}:\\d{2}:\\d{2}:\\s")){
					parsedMessages.add(currentMessage);
					currentMessage = "";
				}
				currentMessage += line;
				line = br.readLine();
			}
			br.close();
		}
		catch(FileNotFoundException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
		return parsedMessages;
	}
	
	public Message parseMessage(String mes){
		Message message = new Message();
		message.setDate(parseDate(mes.substring(0,17)));
		if(mes.substring(19).contains(":")){
			message.setMessageType(Message.MessageType.USER_MESSAGE);
			message.setSender(mes.substring(20).substring(0, mes.substring(20).indexOf(':')));
			String content = mes.substring(20).substring(mes.substring(19).indexOf(':')+1);
			message.setContent(content);
			if(content.equals("<‎image omitted>")) message.setUserMessageType(Message.UserMessageType.IMAGE_MESSAGE);
			else if (content.equals("<‎video omitted>")) message.setUserMessageType(Message.UserMessageType.VIDEO_MESSAGE);
			else if(content.matches("")) message.setUserMessageType(Message.UserMessageType.LOCATION_MESSAGE);
			else message.setUserMessageType(Message.UserMessageType.TEXT_MESSAGE);
		}
		else{
			message.setMessageType(Message.MessageType.SYSTEM_MESSAGE);
			message.setSender("System");
			message.setContent(mes.substring(20));
		}
		return message;
	}

	private Date parseDate(String _date) {
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy, hh:mm:ss");
		Date date = new Date();
		try {
			date = format.parse(_date.substring(0,6) + "20" + _date.substring(7));
		} catch (ParseException e) { e.printStackTrace(); }
		return date;
	}
}
