package datastructures;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class History {
	private ArrayList<Message> messages;
	private HashSet<String> participants;
	private HashMap<String, UserStatistic> userStats;
	private HashMap<Integer, HashMap<Integer, Month>> months;
	private Calendar now;
	private final int NUMBER_OF_MONTHS = 12;
	private HashMap<String, Long> usersActiveDays;
	private final String you = "Luca";
	
	public History(){
		this.messages = new ArrayList<Message>();
		this.participants = new HashSet<String>();
		this.usersActiveDays = new HashMap<String, Long>();
		this.userStats = new HashMap<String, UserStatistic>();
		this.months = new HashMap<Integer, HashMap<Integer, Month>>();
		this.now = Calendar.getInstance();
		now.setTime(new Date());
	}

	public void addMessage(Message _message){
		messages.add(_message);
		participants.add(_message.getSender());
		updateStats(_message.getSender(), _message);
	}
	
	private void updateStats(String sender, Message message){
		if(!userStats.containsKey(sender))
			userStats.put(sender, new UserStatistic());
		UserStatistic stat = userStats.get(sender);
		
		int messageMonth = message.getDate().get(Calendar.MONTH);
		int messageYear = message.getDate().get(Calendar.YEAR);
		checkAndUpdateMonths(messageMonth, messageYear);
		Month month = months.get(messageYear).get(messageMonth);
		
		switch(message.getType()) {
			case SYSTEM_MESSAGE:
				stat.increaseNumberOfMessages();
				month.increaseNumberOfMessages();
				if(message.getContent().contains("added"))
					calculateActiveDays(message.getContent(), message.getDate());	
				break;
			case USER_MESSAGE:
				stat.increaseNumberOfMessages();
				month.increaseNumberOfMessages();
				switch(message.getUserMessageType()) {
					case TEXT_MESSAGE:
						stat.increaseLengthOfTextMessages(message.getContent().length());
						stat.increaseNumberOfEmojis(message.getNumberOfEmojis());
						month.increaseLengthOfTextMessages(message.getContent().length());
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
		months.get(messageYear).put(messageMonth, month);
	}

	private void checkAndUpdateMonths(int messageMonth, int messageYear) {
		if(!months.containsKey(messageYear))
			months.put(messageYear, new HashMap<Integer, Month>());
		if(!months.get(messageYear).containsKey(messageMonth))
			months.get(messageYear).put(messageMonth, new Month(messageYear, messageMonth));
	}

	private void calculateActiveDays(String message, Calendar date) {
		String newParticipant = "";
		if(message.matches(".*was\\sadded"))
			newParticipant = message.substring(1, message.indexOf("was added")-1); //super weird encoding problem, have to start at index 1
		else if(message.matches("‎You\\swere\\sadded")) //why doesn't it work with "message.matches("You were added")"?
			newParticipant = you;
		else if(message.matches(".*\\sadded\\s.*")){
			newParticipant = message.substring(message.indexOf(" added ")+7);
			if(newParticipant.equals("you"))
				newParticipant = you;
		}
		Instant join = date.toInstant();
		Instant nowInstant = now.toInstant();
		usersActiveDays.put(newParticipant, ChronoUnit.DAYS.between(join, nowInstant));
	}

	public void printUserStatistics(){
		System.out.println("Total messages: " + messages.size());
		System.out.println("Total participants (past and present): " + participants.size() + "\n");
		System.out.println(formattedStats());
		System.out.println(formattedActivityStats());
	}

	private String formattedStats() {
		String stats = "Participant\tdays\tavg.#Msg\tavg.l/day\t#Msg\tl.Msg\tavg.l.Msg\t#img\t#vid\t#loc\t#emoji\t%emoji\n";
		for(String participant : participants){
			UserStatistic stat = userStats.get(participant);
			stat = stat.calculateAverageLengthOfTextMessages();
			stat = stat.calculateEmojiRatio();
			recalculateActiveDays(participant);
			stats += participant +  "\t" + usersActiveDays.get(participant) + "\t" + String.format("%.2f", (float)stat.getNumberOfMessages()/(float)usersActiveDays.get(participant)) + 
					 "\t" + String.format("%.2f", (float)stat.getLengthOfTextMessages()/(float)usersActiveDays.get(participant)) + "\t" + stat.getNumberOfMessages() + 
					 "\t" + stat.getLengthOfTextMessages() + "\t" + String.format("%.2f", stat.getAverageLengthOfTextMessages()) + 
					 "\t" + stat.getNumberOfImages() + "\t" + stat.getNumberOfVideos() + "\t" + stat.getNumberOfLocations() + 
					 "\t" + stat.getNumberOfEmojis() + "\t" + String.format("%.2f", 100*stat.getEmojiRatio()) + "\n";
			userStats.put(participant, stat);
		}
		return stats;
	}
	
	private void recalculateActiveDays(String participant) {
		if(!usersActiveDays.containsKey(participant))
			usersActiveDays.put(participant, ChronoUnit.DAYS.between(messages.get(0).getDate().toInstant(), now.toInstant()));
	}

	private String formattedActivityStats() {
		String activityStats = "Month\t#Msg\tl.Msg\n";
		//create months
		Calendar firstMessageDate = messages.get(0).getDate();
		int firstYear = firstMessageDate.get(Calendar.YEAR);
		int firstMonth = firstMessageDate.get(Calendar.MONTH);
		int currentYear = now.get(Calendar.YEAR);
		int currentMonth = now.get(Calendar.MONTH);
		
		for(int year = firstYear; year <= currentYear; year++){
			//first year
			if(year == firstYear && firstYear == currentYear) 
				activityStats += yearString(firstMonth, currentMonth, year);
			else if (year == firstYear && firstYear < currentYear) 
				activityStats += yearString(firstMonth, NUMBER_OF_MONTHS-1, year);
			//years between first and current
			else if(firstYear < year && year < currentYear) 
				activityStats += yearString(0, NUMBER_OF_MONTHS-1, year);
			//current year
			else 
				activityStats += yearString(0, currentMonth, year);
		}			
		return activityStats;
	}

	private String yearString(int beginMonth, int endMonth, int year) {
		String yearString = "";
		for(int month = beginMonth; month <= endMonth; month++){
			if(months.containsKey(year) && months.get(year).containsKey(month))
				yearString += months.get(year).get(month).printNicely();
			else
				yearString += Month.zeroMonth(month, year);
			yearString += "\n";
		}
		return yearString;
	}
}
