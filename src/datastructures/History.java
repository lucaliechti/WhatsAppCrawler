package datastructures;

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
	private JoinLeaveHistory joinLeaveHistory;
	private Calendar now;
	private final int NUMBER_OF_MONTHS = 12;
	
	public History(){
		this.messages = new ArrayList<Message>();
		this.participants = new HashSet<String>();
		this.userStats = new HashMap<String, UserStatistic>();
		this.months = new HashMap<Integer, HashMap<Integer, Month>>();
		this.now = Calendar.getInstance();
		now.setTime(new Date());
		this.joinLeaveHistory = new JoinLeaveHistory(this);
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
				String cont = message.getContent();
				if(cont.contains("added") || cont.contains("removed") || cont.contains("left"))
					joinLeaveHistory.addMessage(message);//calculateActiveDays(message.getContent(), message.getDate());	
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

	public void printUserStatistics(){
		System.out.println("Total messages: " + messages.size());
		System.out.println("Total participants (past and present): " + participants.size() + "\n");
		System.out.println(formattedStats());
		System.out.println(formattedActivityStats());
		System.out.println(joinLeaveHistory);
	}

	private String formattedStats() {
		String stats = "Participant\tdays\tavg.#Msg\tavg.l/day\t#Msg\tl.Msg\tavg.l.Msg\t#img\t#vid\t#loc\t#emoji\t%emoji\n";
		for(String participant : participants){
			UserStatistic stat = userStats.get(participant);
			stats += participant + 
					 "\t" + joinLeaveHistory.getMembershipDays(participant) + 
					 "\t" + String.format("%.2f", (double)stat.getNumberOfMessages()/(double)joinLeaveHistory.getMembershipDays(participant)) +
					 "\t" + String.format("%.2f", (double)stat.getLengthOfTextMessages()/(double)joinLeaveHistory.getMembershipDays(participant)) + "\t" + stat.getNumberOfMessages() +
					 "\t" + stat.getLengthOfTextMessages() + "\t" + String.format("%.2f", stat.getAverageLengthOfTextMessages()) + 
					 "\t" + stat.getNumberOfImages() + "\t" + stat.getNumberOfVideos() + "\t" + stat.getNumberOfLocations() + 
					 "\t" + stat.getNumberOfEmojis() + "\t" + String.format("%.2f", 100*stat.getEmojiRatio()) + "\n";
		}
		return stats;
	}
	
	public void calculateValues() {
		for(String participant : participants){
			UserStatistic stat = userStats.get(participant);
			stat = stat.calculateAverageLengthOfTextMessages();
			stat = stat.calculateEmojiRatio();
			userStats.put(participant, stat);
			joinLeaveHistory.addParticipantFromStart(participant);
		}
		joinLeaveHistory.parseMessagesToActions();
		joinLeaveHistory.calculateDaysOfMembership();
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
	
	//that's not always the start date though
	protected Calendar getStartDate(){
		return messages.get(0).getDate();
	}
}
