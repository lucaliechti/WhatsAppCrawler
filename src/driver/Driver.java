package driver;

import java.util.ArrayList;

import datastructures.History;

import parsers.MessageParser;

public class Driver {
	public static void main(String[] args){
		String file = "/home/luca/Desktop/Chats/Jess.txt";
		MessageParser parser = new MessageParser();
		History history = new History();
	
		ArrayList<String> parsedMessages = parser.splitMessages(file);
		for(String message : parsedMessages)
			history.addMessage(parser.parseMessage(message));
		history.printUserStatistics();
	}
}
