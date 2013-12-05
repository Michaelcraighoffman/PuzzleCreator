package puzzlemaker.tools;

import puzzlemaker.Constants;

public class WordCluePair implements Comparable<String> {
	
	private String m_word;
	private String m_clue;
	
	public WordCluePair(String word) {
		this(word, "");
	}
	
	public WordCluePair(String word, String clue) {
		word = Constants.filterWord(word);
		m_word = word;
		m_clue = clue;
	}
	
	public String getWord() {
		return m_word;
	}
	
	public boolean setWord(String word) {
		word = Constants.filterWord(word);
		
		if (word == null) {
			return false;
		}
		else {
			m_word = word;
			return true;
		}
	}
	
	public String getClue() {
		return m_clue;
	}
	
	public void setClue(String clue) {
		m_clue = clue;
	}
	
	@Override
	public String toString() {
		// TODO: Alyssa, you choose what to print here. -Sam
		return m_word;
	}

	@Override
	public int compareTo(String o) {
		return (m_word.compareTo(o));
	}
}
