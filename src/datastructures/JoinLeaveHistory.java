package datastructures;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class JoinLeaveHistory {

	private History history;
	private ArrayList<Message> messages;
	private HashMap<String, ArrayList<JoinLeaveAction>> actions;
	private HashMap<String, Long> memberships;
	private final String you = "Luca";
	
	private enum ActionType {
		JOIN_ACTION,
		LEAVE_ACTION
	}
	
	public JoinLeaveHistory(History _his) {
		this.history = _his;
		this.messages = new ArrayList<Message>();
		this.actions = new HashMap<String, ArrayList<JoinLeaveAction>>();
		this.memberships = new HashMap<String, Long>();
	}
	
	public void addMessage(Message mes) {
		messages.add(mes);
	}
	
	private String getParticipantFromMessageContent(String content, ActionType type){
		String participant = "";
			switch(type) {
				case JOIN_ACTION:
					if(content.matches(".*was\\sadded"))
						participant = content.substring(0, content.indexOf("was added")-1);
					else if(content.length() == 14 && content.contains("You") && content.contains("were")) //WTF?
						participant = you;
					else if(content.matches(".*\\sadded\\s.*")){
						participant = content.substring(content.indexOf(" added ")+7);
						if(participant.equals("you"))
							participant = you;
					}
					break;
				case LEAVE_ACTION:
					if(content.contains("left"))
						participant = content.substring(0, content.indexOf(" left"));
					else if(content.contains("removed"))
						participant = content.substring(content.indexOf(" removed ")+9);
					break;
			}
		return participant;
	}
	
	private ActionType getActionType(String content) {
		if(content.contains("added"))
			return ActionType.JOIN_ACTION;
		else
			return ActionType.LEAVE_ACTION;
	}
	
	public void parseMessagesToActions() {
		for(Message message : messages){
			ActionType type = getActionType(message.getContent());
			String participant = getParticipantFromMessageContent(message.getContent(), getActionType(message.getContent()));
			if(!actions.containsKey(participant))
				actions.put(participant, new ArrayList<JoinLeaveAction>());
			ArrayList<JoinLeaveAction> list = actions.get(participant);
			list.add(new JoinLeaveAction(message.getDate(), type));
			actions.put(participant, list);
		}
	}
	
	public void calculateDaysOfMembership() {
		Calendar start = history.getStartDate();
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		Set<String> participants = actions.keySet();
		for(String participant : participants){
			long days = 0l;
			ArrayList<JoinLeaveAction> act = actions.get(participant);
			JoinLeaveAction lastAction = new JoinLeaveAction(start, ActionType.JOIN_ACTION);
			Iterator<JoinLeaveAction> iter = act.iterator();
			while(iter.hasNext()){
				JoinLeaveAction currentAction = iter.next();
				if(currentAction.getAction() == ActionType.LEAVE_ACTION){
					days += ChronoUnit.DAYS.between(lastAction.getDate().toInstant(), currentAction.getDate().toInstant());
				}
				lastAction = currentAction;
			}
			if(lastAction.getAction() == ActionType.JOIN_ACTION){ //if user is still in chat
				System.out.println("Added " + ChronoUnit.DAYS.between(lastAction.getDate().toInstant(), now.toInstant()) + " days for " + participant);
				days += ChronoUnit.DAYS.between(lastAction.getDate().toInstant(), now.toInstant());
			}
			if(days == 0l) days = 1l; //prevents division by zero
			memberships.put(participant, days);
		}
	}

//	@Override
//	public String toString(){
//		SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
//		String string = "";
//		Set<String> participants = actions.keySet();
//		for(String part : participants){
//			string += part + ": ";
//			ArrayList<JoinLeaveAction> set = actions.get(part);
//			for(JoinLeaveAction action : set){
//				string += format1.format(action.getDate().getTime()) + ": " + action.getAction() + " - ";
//			}
//			string += "\n";
//		}
//		return string;
//	}
		
	protected long getMembershipDays(String participant) {
		return memberships.get(participant);
	}
		
	public class JoinLeaveAction {
		private Calendar date;
		private ActionType action;
		
		public JoinLeaveAction(Calendar _cal, ActionType _type) {
			this.date = _cal;
			this.action = _type;
		}
		
		protected Calendar getDate() { return date; }
		protected ActionType getAction() { return action; }
	}

	public void addParticipantFromStart(String participant) {
		if(!actions.containsKey(participant))
			actions.put(participant, new ArrayList<JoinLeaveAction>());
	}


}
