package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiTrie;
import com.vdurmont.emoji.EmojiTrie.Matches;

import parsers.MessageParser;

public class EmojiTest {
	
	MessageParser parser = new MessageParser();
	private EmojiTrie et = new EmojiTrie(EmojiManager.getAll());
	
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
	public void recognizeFlag(){
		String flagPt1 = "🇧"; char[] flag1char = flagPt1.toCharArray();
		String flagPt2 = "🇩"; char[] flag2char = flagPt2.toCharArray();
		String flag = flagPt1 + flagPt2; char[] flagChar = flag.toCharArray();
		assertEquals(et.isEmoji(flag1char), Matches.EXACTLY);
		assertEquals(et.isEmoji(flag2char), Matches.EXACTLY);
		assertEquals(et.isEmoji(flagChar), Matches.EXACTLY);
	}
	
	@Test
	public void countFlagEmojis(){
		String flagPt1 = "🇧";
		String flagPt2 = "🇩";
		String flag = flagPt1 + flagPt2;
		int flag1Emojis = parser.countEmojis(flagPt1);
		int flag2Emojis = parser.countEmojis(flagPt2);
		int flagEmojis = parser.countEmojis(flag);
		assertEquals(1, flag1Emojis);
		assertEquals(1, flag2Emojis);
		assertEquals(1, flagEmojis); //Composite of 2 must count as 1
	}
	
	@Test
	public void recognizeFamily(){
		String famPt1 = "👩"; char[] fam1char = famPt1.toCharArray();
		String famPt2 = "👩"; char[] fam2char = famPt2.toCharArray();
		String famPt3 = "👧"; char[] fam3char = famPt3.toCharArray();
		String famPt4 = "👧"; char[] fam4char = famPt4.toCharArray();
		String fam = famPt1 + famPt2 + famPt3 + famPt4; char[] famChar = fam.toCharArray();
		assertEquals(et.isEmoji(fam1char), Matches.EXACTLY);
		assertEquals(et.isEmoji(fam2char), Matches.EXACTLY);
		assertEquals(et.isEmoji(fam3char), Matches.EXACTLY);
		assertEquals(et.isEmoji(fam4char), Matches.EXACTLY);
		assertEquals(et.isEmoji(famChar), Matches.EXACTLY);
	}
	
	@Test
	public void countFamilyEmojis(){
		String fam1 = "👩";
		String fam2 = "👩";
		String fam3 = "👧";
		String fam4 = "👧";
		String fam = fam1 + fam2 + fam3 + fam4;
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
