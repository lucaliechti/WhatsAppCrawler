package driver;

import java.util.ArrayList;

import datastructures.History;

import parsers.MessageParser;

public class CrawlerDriver {
	public static void main(String[] args){
		String file = "C:\\Users\\Luca Liechti\\Dropbox\\Zeugs\\Chats\\lena.txt";
		MessageParser parser = new MessageParser();
		History history = new History();
	
		ArrayList<String> parsedMessages = parser.splitMessages(file);
		for(String message : parsedMessages)
			history.addMessage(parser.parseMessage(message));
		history.calculateValues();
		history.printUserStatistics();
	}
}
