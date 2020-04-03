package parsers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import datastructures.Message;

public class MessageParser {

	private static final int DATE_START_POS = 1;
	private static final int DATE_END_POS = 19;

	private static final String DATE_REGEX = "\\d{2}\\.\\d{2}.\\d{2},\\s{1}\\d{2}:\\d{2}:\\d{2}";
	private static final String IMAGE_MESSAGE_TEXT = "image omitted";
	private static final String VIDEO_MESSAGE_TEXT = "‎video omitted";
	private static final String VCARD_MESSAGE_TEXT = "vCard omitted";
	private static final String LOCATION_MESSAGE_REGEX =
			"(\\W|\\S)?location: https://maps.google.com/\\?q=-?\\d{1,2}.\\d{6},-?\\d{1,2}.\\d{6}";

	private static final SimpleDateFormat WHATSAPP_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy, hh:mm:ss");

	public List<String> splitMessages(String file){
		ArrayList<String> parsedMessages = new ArrayList<>();
		try (BufferedReader br = new BufferedReader (new FileReader(file))) {
			String line = br.readLine();
			StringBuilder currentMessage = new StringBuilder();
			while (line != null) {
				line = clean(line);
				if (line.length() > DATE_END_POS && (line.substring(DATE_START_POS, DATE_END_POS).matches(DATE_REGEX))
						&& !currentMessage.toString().isBlank()) {
					parsedMessages.add(currentMessage.toString());
					currentMessage = new StringBuilder();
				}
				else if (!currentMessage.toString().isBlank()) {
					currentMessage.append("\n");
				}
				currentMessage.append(line);
				line = br.readLine();
			}
			parsedMessages.add(currentMessage.toString());
		} catch(IOException e) {
			e.printStackTrace();
		}
		return parsedMessages;
	}

	private String clean(String line) {
		return line == null ? null : line.replace("\u200E", "");
	}

	public Message parseMessage(String mes) {
		Message message = new Message();
		message.setDate(parseDate(mes.substring(DATE_START_POS, DATE_END_POS)));

		if (mes.substring(DATE_END_POS).contains(":")) {
			message.setMessageType(Message.MessageType.USER_MESSAGE);
			String sender = mes.substring(20).substring(0, mes.substring(20).indexOf(':'));
			message.setSender(sender);
			String content = mes.substring(20).substring(mes.substring(19).indexOf(':')+1);
			message.setContent(content);

			if (content.equals(IMAGE_MESSAGE_TEXT)) {
				message.setUserMessageType(Message.UserMessageType.IMAGE_MESSAGE);
			}
			else if (content.equals(VIDEO_MESSAGE_TEXT)) {
				message.setUserMessageType(Message.UserMessageType.VIDEO_MESSAGE);
			}
			else if (content.equals(VCARD_MESSAGE_TEXT)) {
				message.setUserMessageType(Message.UserMessageType.VCARD_MESSAGE);
			}
			else if (content.matches(LOCATION_MESSAGE_REGEX)) {
				message.setUserMessageType(Message.UserMessageType.LOCATION_MESSAGE);
				System.out.println(content);
			}
			else {
				message.setUserMessageType(Message.UserMessageType.TEXT_MESSAGE);
				message.setNumberOfEmojis(countEmojis(content));
			}
		}
		else{
			message.setMessageType(Message.MessageType.SYSTEM_MESSAGE);
			message.setSender("System");
			message.setContent(mes.substring(21));
		}
		return message;
	}

	public int countEmojis(String content) {
		int emojis = 0;
		content = EmojiParser.parseToAliases(content);
		String[] candidates = content.split(":");
		for (String candidate : candidates) {
			if (candidate.contains("|")) {
				candidate = candidate.split("\\|")[0];
			}
			if (!(EmojiManager.getForAlias(candidate) == null)) {
				emojis++;
			}
		}
		return emojis;
	}

	private Calendar parseDate(String date) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("CET"));
		Date parsedDate;
		try {
			parsedDate = WHATSAPP_DATE_FORMAT.parse(date.substring(0,6) + "20" + date.substring(6));
			cal.setTime(parsedDate);
		} catch (ParseException e) {
			System.out.println("Could not parse date \"" + date + "\"");
		}
		return cal;
	}
}
