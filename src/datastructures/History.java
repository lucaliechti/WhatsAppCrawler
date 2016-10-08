package datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class History {
	private ArrayList<Message> messages;
	private HashSet<String> participants;
	private HashMap<String, UserStatistic> userStats;
	
	public History(){
		this.messages = new ArrayList<Message>();
		this.participants = new HashSet<String>();
		this.userStats = new HashMap<String, UserStatistic>();
	}

	public void addMessage(Message _message){
		messages.add(_message);
		participants.add(_message.getSender());
		updateStats(_message.getSender(), _message);
	}
	
	private void updateStats(String sender, Message message){
		if(!userStats.containsKey(sender))
			userStats.put(sender, new UserStatistic());
		//update user statistics
		UserStatistic stat = userStats.get(sender);
		switch(message.getType()) {
			case SYSTEM_MESSAGE:
				stat.increaseNumberOfMessages();
				break;
			case USER_MESSAGE:
				stat.increaseNumberOfMessages();
				switch(message.getUserMessageType()) {
					case TEXT_MESSAGE:
						stat.increaseLengthOfTextMessages(message.getContent().length());
						stat.increaseNumberOfEmojis(message.getNumberOfEmojis());
						break;
					case IMAGE_MESSAGE:
						stat.increaseNumberOfImages();
						break;
					case VIDEO_MESSAGE:
						stat.increaseNumberOfVideos();
						break;
					case LOCATION_MESSAGE:
						stat.increaseNumberOfLocations();
						break;
				}
		}
		userStats.put(sender, stat);
	}

	public void printUserStatistics(){
		System.out.println("Total messages: " + messages.size());
		System.out.println("Total participants (past and present): " + participants.size());
		System.out.println(formattedStats());
	}

	private String formattedStats() {
		String stats = "Participant\t#Msg\tl.Msg\tavg.l.Msg\t#img\t#vid\t#loc\t#emoji\t%emoji\n";
		for(String participant : participants){
			UserStatistic stat = userStats.get(participant);
			stat = stat.calculateAverageLengthOfTextMessages();
			stat = stat.calculateEmojiRatio();
			stats += participant + "\t" + stat.getNumberOfMessages() + "\t" + stat.getLengthOfTextMessages() + "\t" + String.format("%.2f", stat.getAverageLengthOfTextMessages()) + 
					 "\t" + stat.getNumberOfImages() + "\t" + stat.getNumberOfVideos() + "\t" + stat.getNumberOfLocations() + 
					 "\t" + stat.getNumberOfEmojis() + "\t" + String.format("%.2f", 100*stat.getEmojiRatio()) + "\n";
			userStats.put(participant, stat);
		}
		return stats;
	}
}
