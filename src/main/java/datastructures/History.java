package datastructures;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.vdurmont.emoji.EmojiParser;

import datastructures.Message.MessageType;

public class History {
	private ArrayList<Message> messages;
	private HashSet<String> participants;
	private HashMap<String, UserStatistic> userStats;
	private HashMap<Integer, HashMap<Integer, Month>> months;
	private JoinLeaveHistory joinLeaveHistory;
	private final int NUMBER_OF_MONTHS = 12;
	
	public History() {
		this.messages = new ArrayList<>();
		this.participants = new HashSet<>();
		this.userStats = new HashMap<>();
		this.months = new HashMap<>();
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		this.joinLeaveHistory = new JoinLeaveHistory(this);
	}

	public void addMessage(Message message) {
		messages.add(message);
		participants.add(message.getSender());
		updateStats(message.getSender(), message);
	}
	
	private void updateStats(String sender, Message message) {
		userStats.putIfAbsent(sender, new UserStatistic());
		UserStatistic stat = userStats.get(sender);
		
		int messageMonth = message.getDate().get(Calendar.MONTH);
		int messageYear = message.getDate().get(Calendar.YEAR);
		checkAndUpdateMonths(messageMonth, messageYear);
		Month month = months.get(messageYear).get(messageMonth);
		
		switch (message.getType()) {
			case SYSTEM_MESSAGE:
				String cont = message.getContent();
				if (cont.contains("added") || cont.contains("removed") || cont.contains("left")) {
					joinLeaveHistory.addMessage(message);
				}
				break;
			case USER_MESSAGE:
				stat.increaseNumberOfMessages();
				month.increaseNumberOfMessages();
				switch(message.getUserMessageType()) {
					case TEXT_MESSAGE:
						int numberOfEmojis = message.getNumberOfEmojis();
						int lengthWithoutEmojis = EmojiParser.removeAllEmojis(message.getContent()).length();
						stat.increaseLengthOfTextMessages(lengthWithoutEmojis + numberOfEmojis);
						stat.increaseNumberOfEmojis(numberOfEmojis);
						month.increaseLengthOfTextMessages(lengthWithoutEmojis + numberOfEmojis);
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
					case VCARD_MESSAGE:
						stat.increaseNumberOfVcards();
						break;
				}
		}
		userStats.put(sender, stat);
		months.get(messageYear).put(messageMonth, month);
	}

	private void checkAndUpdateMonths(int messageMonth, int messageYear) {
		months.putIfAbsent(messageYear, new HashMap<>());
		months.get(messageYear).putIfAbsent(messageMonth, new Month(messageYear, messageMonth));
	}

	public void printUserStatistics(){
		System.out.println("Total messages: " + messages.size());
		System.out.println("Total participants (past and present): " + participants.size() + "\n");
		System.out.println(formattedStats());
		System.out.println(formattedActivityStats());
	}

	private String formattedStats() {
		StringBuilder stats = new StringBuilder("Participant\tdays\tavg.#Msg\tavg.l/day\t#Msg\tl.Msg\tavg.l.Msg\t#img\t#vid\t#loc\t#vcard\t#emoji\t%emoji\n");
		for(String participant : participants){
			UserStatistic stat = userStats.get(participant);
			stats.append(participant).append("\t")
					.append(joinLeaveHistory.getMembershipDays(participant)).append("\t")
					.append(String.format("%.2f", (double) stat.getNumberOfMessages() / (double) joinLeaveHistory.getMembershipDays(participant))).append("\t")
					.append(String.format("%.2f", (double) stat.getLengthOfTextMessages() / (double) joinLeaveHistory.getMembershipDays(participant))).append("\t")
					.append(stat.getNumberOfMessages()).append("\t")
					.append(stat.getLengthOfTextMessages()).append("\t")
					.append(String.format("%.2f", stat.getAverageLengthOfTextMessages())).append("\t")
					.append(stat.getNumberOfImages()).append("\t")
					.append(stat.getNumberOfVideos()).append("\t")
					.append(stat.getNumberOfLocations()).append("\t")
					.append(stat.getNumberOfVcards()).append("\t")
					.append(stat.getNumberOfEmojis()).append("\t")
					.append(String.format("%.2f", 100 * stat.getEmojiRatio())).append("\n");
		}
		return stats.toString();
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
		Calendar firstMessageDate = messages.get(0).getDate();
		Calendar lastMessageDate = messages.get(messages.size()-1).getDate();
		int firstYear = firstMessageDate.get(Calendar.YEAR);
		int firstMonth = firstMessageDate.get(Calendar.MONTH);
		int lastYear = lastMessageDate.get(Calendar.YEAR);
		int lastMonth = lastMessageDate.get(Calendar.MONTH);

		StringBuilder activityStats = new StringBuilder("Month\t#Msg\tl.Msg\n");
		for(int year = firstYear; year <= lastYear; year++) {
			if(firstYear == lastYear) {
				activityStats.append(yearString(firstMonth, lastMonth, year));
			}
			else if (year == firstYear) {
				activityStats.append(yearString(firstMonth, NUMBER_OF_MONTHS - 1, year));
			}
			else if (firstYear < year && year < lastYear) {
				activityStats.append(yearString(0, NUMBER_OF_MONTHS - 1, year));
			}
			else {
				activityStats.append(yearString(0, lastMonth, year));
			}
		}			
		return activityStats.toString();
	}

	private String yearString(int beginMonth, int endMonth, int year) {
		StringBuilder yearString = new StringBuilder();
		for (int month = beginMonth; month <= endMonth; month++){
			if (months.containsKey(year) && months.get(year).containsKey(month)) {
				yearString.append(months.get(year).get(month).printNicely());
			}
			else {
				yearString.append(Month.zeroMonth(month, year));
			}
			yearString.append("\n");
		}
		return yearString.toString();
	}
	
	protected Calendar getStartDate(){
		if(messages.get(0).getType() == MessageType.SYSTEM_MESSAGE && !messages.get(0).getContent().contains("You created"))
			return messages.get(1).getDate();
		else
			return messages.get(0).getDate();
	}
}
