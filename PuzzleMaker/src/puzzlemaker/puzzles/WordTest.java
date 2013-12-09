package puzzlemaker.puzzles;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import puzzlemaker.Constants;

public class WordTest {

	@Test
	public void testIntersectionsWithValid() {
		Word word=new Word("everyone");
		Word word2=new Word("veered");
		int intersections=word.intersectionsWith(word2);
		assertTrue("Incorrect number of word intersections", intersections==5);
	}
	@Test
	public void testIntersectionsWithInvalid() {
		Word word=new Word("everyone");
		Word word2=new Word("mask");
		int intersections=word.intersectionsWith(word2);
		assertTrue("Found intersections between non-intersecting words", intersections==0);
	}

	@Test
	public void testContainsCharPresent() {
		Word word=new Word("everyone");
		assertTrue("Character not found in containing word", word.containsChar('v'));
	}
	@Test
	public void testContainsCharAbsent() {
		Word word=new Word("everyone");
		assertTrue("Character found in word that does not contain it", !word.containsChar('q'));
	}

	@Test
	public void testGetIntersectionIndicesValid() {
		Word word=new Word("everyone");
		ArrayList<Integer> intersections=word.getIntersectionIndices('e');
		assertTrue("Incorrect number of word intersections", intersections.size()==3);
		assertTrue("Valid intersection not found.",intersections.contains(0));
		assertTrue("Valid intersection not found.",intersections.contains(2));
		assertTrue("Valid intersection not found.",intersections.contains(7));
	}

	@Test
	public void testToString() {
		Word word=new Word("everyone");
		assertTrue("ToString returned incorrect string",word.toString()=="everyone");
	}

	@Test
	public void testToStringDetailed() {
		Word word=new Word("everyone",7,9,Constants.TOP_TO_BOTTOM);
		String text=word.toStringDetailed();
		assertTrue("ToStringDetailed returned incorrect string",text.equals("everyone x: 7 y: 9 dir: 2"));
	}

	@Test
	public void testToStringPretty() {
		Word word=new Word("everyone",7,9,Constants.TOP_TO_BOTTOM);
		assertTrue("ToStringDetailed returned incorrect string",word.toStringPretty().equals("everyone (7, 9)  (Top to Bottom)"));
	}

}
