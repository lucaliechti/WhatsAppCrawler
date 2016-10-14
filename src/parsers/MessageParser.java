package parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiTrie;
import com.vdurmont.emoji.EmojiTrie.Matches;

import datastructures.Message;

public class MessageParser {
	private final int MAX_EMOJI_LENGTH = 2; //number of CHARs an emoji can take up
	private EmojiTrie et = new EmojiTrie(EmojiManager.getAll());
	
	public ArrayList<String> splitMessages(String file){
		ArrayList<String> parsedMessages = new ArrayList<String>();
		try{
			BufferedReader br = new BufferedReader (new FileReader(file));
			String line = br.readLine();
			String currentMessage = "";
			while (line != null){
				if(line.length() >= 20 && line.substring(0,20).matches("\\d{2}\\.\\d{2}.\\d{2},\\s\\d{2}:\\d{2}:\\d{2}:\\s") && !currentMessage.equals("")){
					parsedMessages.add(currentMessage);
					currentMessage = "";
				}
				else if (!currentMessage.equals("")) currentMessage += "\n";
				currentMessage += line;
				line = br.readLine();
			}
			parsedMessages.add(currentMessage);//the last message
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
			String sender = mes.substring(20).substring(0, mes.substring(20).indexOf(':'));
			message.setSender(sender);
			String content = mes.substring(20).substring(mes.substring(19).indexOf(':')+1);
			message.setContent(content);
			if(content.equals("<‎image omitted>")) message.setUserMessageType(Message.UserMessageType.IMAGE_MESSAGE);
			else if (content.equals("<‎video omitted>")) message.setUserMessageType(Message.UserMessageType.VIDEO_MESSAGE);
			else if(content.matches("(\\W|\\S)?location: https://maps.google.com/\\?q=-?\\d{1,2}.\\d{6},-?\\d{1,2}.\\d{6}")) message.setUserMessageType(Message.UserMessageType.LOCATION_MESSAGE);
			else {
				message.setUserMessageType(Message.UserMessageType.TEXT_MESSAGE);
				message.setNumberOfEmojis(countEmojis(content));
			}
		}
		else{
			message.setMessageType(Message.MessageType.SYSTEM_MESSAGE);
			message.setSender("System");
			message.setContent(mes.substring(20));
		}
		return message;
	}
	
	//counts the emojis in a string.
	//at the moment, composite emojis count between double and quadruple.
	//maybe fix in the future.
	private int countEmojis(String content) {
		int emojis = 0;
		char[] currentLetter;
		int i = 0;
		while(i < content.length()){
			for(int j = 1; j <= MAX_EMOJI_LENGTH; j++){
				if(i+j <= content.length())
					currentLetter = content.substring(i, i+j).toCharArray();
				else
					currentLetter = content.substring(i, content.length()-1).toCharArray();
				if(et.isEmoji(currentLetter) == Matches.EXACTLY){
					emojis++;
					if(i+j == content.length())
						return emojis;
					i+=j;
					j=0;
				}
				else if(j == MAX_EMOJI_LENGTH)
					i++;
			}
		}
		return emojis;
	}

	private Calendar parseDate(String _date) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("CET"));
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy, hh:mm:ss");
		Date date = new Date();
		try {
			date = format.parse(_date.substring(0,6) + "20" + _date.substring(6));
			cal.setTime(date);
		} catch (ParseException e) { e.printStackTrace(); }
		return cal;
	}
}
