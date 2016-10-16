package tests;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import parsers.MessageParser;

public class EmojiTest {
	
	MessageParser parser = new MessageParser();
	
	@Test
	public void countRegularEmojis(){
		String string_1_emoji = "🦄";
		String string_2_emoji = "🦄test🦄";
		String string_3_emoji = "test🦄test🦄test🦄test";
		int int_1_emoji = parser.countEmojis(string_1_emoji);
		int int_2_emoji = parser.countEmojis(string_2_emoji);
		int int_3_emoji = parser.countEmojis(string_3_emoji);
		assertEquals(1, int_1_emoji);
		assertEquals(2, int_2_emoji);
		assertEquals(3, int_3_emoji);
	}
	
	@Test
	public void countFlagEmojis(){
		String flagPt1 = "🇧";
		String flagPt2 = "🇩";
		String flag = "🇧🇩";
		int flag1Emojis = parser.countEmojis(flagPt1);
		int flag2Emojis = parser.countEmojis(flagPt2);
		int flagEmojis = parser.countEmojis(flag);
		assertEquals(1, flag1Emojis);
		assertEquals(1, flag2Emojis);
		assertEquals(1, flagEmojis); //Composite of 2 must count as 1
	}
	
	@Test
	public void countFamilyEmojis(){
		String fam1 = "👩";
		String fam2 = "👩";
		String fam3 = "👧";
		String fam4 = "👧";
		String fam = "👩‍👩‍👧‍👧";
		int fam1Emojis = parser.countEmojis(fam1);
		int fam2Emojis = parser.countEmojis(fam2);
		int fam3Emojis = parser.countEmojis(fam3);
		int fam4Emojis = parser.countEmojis(fam4);
		int famEmojis = parser.countEmojis(fam);
		assertEquals(1, fam1Emojis);
		assertEquals(1, fam2Emojis);
		assertEquals(1, fam3Emojis);
		assertEquals(1, fam4Emojis);
		assertEquals(1, famEmojis); //Composite of 4 must count as 1
	}
}
