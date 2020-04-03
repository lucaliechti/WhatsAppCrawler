package driver;

import java.util.List;

import datastructures.History;

import parsers.MessageParser;

public class CrawlerDriver {
	public static void main(String[] args){
		String file = args[0];
		MessageParser parser = new MessageParser();
		History history = new History();

		List<String> parsedMessages = parser.splitMessages(file);
		for(String message : parsedMessages)
			history.addMessage(parser.parseMessage(message));
		history.calculateValues();
		history.printUserStatistics();
	}
}
