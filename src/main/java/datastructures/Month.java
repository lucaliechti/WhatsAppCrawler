package datastructures;

import java.text.DateFormatSymbols;

public class Month {
	private int year;
	private int month;
	private int numberOfMessages;
	private int lengthOfTextMessages;
	private static DateFormatSymbols dateformat;
	
	public Month (int _year, int _month) {
		this.year = _year;
		this.month = _month;
		Month.dateformat = new DateFormatSymbols();
		String[] shortMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		Month.dateformat.setShortMonths(shortMonths);
	}
	
	protected int getYear() { return year; }
	protected int getMonth() { return month; }
	protected int getNumberOfMessages() { return numberOfMessages; }
	protected int getLengthOfTextMessages() { return lengthOfTextMessages; }
	
	protected void setYear(int _year) { this.year = _year; }
	protected void setMonth(int _month) { this.month = _month; }
	protected void increaseNumberOfMessages() { this.numberOfMessages++; }
	protected void increaseLengthOfTextMessages(int length) { this.lengthOfTextMessages += length; }
	
	public String printNicely() {
		return dateformat.getShortMonths()[month] + " " + year + "\t" + numberOfMessages + "\t" + lengthOfTextMessages;
	}
	
	public static String zeroMonth(int month, int year) {
		return dateformat.getShortMonths()[month] + " " + year + "\t0\t0";
	}
}