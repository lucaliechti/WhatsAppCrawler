package datastructures;

public class UserStatistic {
	
	private int numberOfMessages;
	private int lengthOfTextMessages;
	private int numberOfImages;
	private int numberOfVideos;
	private int numberOfLocations;
	private double averageLengthOfMessage;
	
	public UserStatistic(){
		this.numberOfMessages = 0;
		this.numberOfImages = 0;
		this.numberOfVideos = 0;
		this.numberOfLocations = 0;
		this.averageLengthOfMessage = 0.0;
	}
	
	public UserStatistic calculateAverageLengthOfTextMessages() {
		if(!(numberOfMessages == 0))
			averageLengthOfMessage = (double)lengthOfTextMessages/(double)numberOfMessages;
		return this;
	}
	
	public UserStatistic increaseNumberOfMessages() {numberOfMessages++; return this;}
	public UserStatistic increaseLengthOfTextMessages(int length) {lengthOfTextMessages += length; return this;}
	public UserStatistic increaseNumberOfImages() {numberOfImages++; return this;}
	public UserStatistic increaseNumberOfVideos() {numberOfVideos++; return this;}
	public UserStatistic increaseNumberOfLocations() {numberOfLocations++; return this;}

	public int getNumberOfMessages() {return numberOfMessages;}
	public int getLengthOfTextMessages() {return lengthOfTextMessages;}
	public int getNumberOfImages() {return numberOfImages;}
	public int getNumberOfVideos() {return numberOfVideos;}
	public int getNumberOfLocations() {return numberOfLocations;}
	public double getAverageLengthOfTextMessages() {return averageLengthOfMessage;}
}
