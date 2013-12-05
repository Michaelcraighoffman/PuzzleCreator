package puzzlemaker.puzzles;

import java.util.ArrayList;

import puzzlemaker.Constants;

public class Word implements Comparable<Object> {

	private String m_word;	
	private int m_x;
	private int m_y;
	private int m_direction;
		
	public Word (String word) {
		m_word = word;
		m_x = -1;
		m_y = -1;
		m_direction = -1;
	}
	
	public Word (String word, int x, int y, int direction) {
		m_word = word;
		m_x = x;
		m_y = y;
		m_direction = direction;
	}
	
	public Word (Word word) {
		m_word = word.toString();
		m_x = word.getX();
		m_y = word.getY();
		m_direction = word.getDirection();
	}

	public int getX() {
		return m_x;
	}
	
	public int getY() {
		return m_y;
	}
	
	public int getDirection() {
		return m_direction;
	}
	
	public void setX(int x) {
		m_x = x;
	}
	
	public void setY(int y) {
		m_y = y;
	}
	
	public void setDirection(int direction) {
		m_direction = direction;
	}
	
	public int intersectionsWith(Word otherWord) {
		int intersections = 0;
		String otherString = otherWord.toString();
		
		for (char c : otherString.toCharArray()) {
			if (m_word.indexOf((int) c) > -1) {
				intersections++;
			}
		}
		
		return intersections;
	}
	
	public boolean containsChar(char c) {
		return (m_word.indexOf(c) >= 0);
	}
	
	public ArrayList<Integer> getIntersectionIndices(char c) {
		ArrayList<Integer> indices = new ArrayList<Integer>(0);
		char[] word = m_word.toCharArray();
		for (int i = 0; i < word.length; i++) {
			if (word[i] == c) {
				indices.ensureCapacity(indices.size() + 1);
				indices.add(i);
			}
		}
		
		return indices;
	}
	
	@Override
	public String toString() {
		return m_word;
	}
	
	public String toStringDetailed() {
		return m_word + " x: " + m_x + " y: " + m_y + " dir: " + m_direction;
	}
	
	public String toStringPretty() {
		String output = m_word + " (" + m_x + ", " + m_y + ")  ";
		
		String direction;
		switch (m_direction) {
			case Constants.LEFT_TO_RIGHT:
				direction = "(Left to Right)";
				break;
			case Constants.TOPLEFT_TO_BOTTOMRIGHT:
				direction = "(Top Left to Bottom Right)";
				break;
			case Constants.TOP_TO_BOTTOM:
				direction = "(Top to Bottom)";
				break;
			case Constants.TOPRIGHT_TO_BOTTOMLEFT:
				direction = "(Top Right to Bottom Left)";
				break;
			case Constants.RIGHT_TO_LEFT:
				direction = "(Right to Left)";
				break;
			case Constants.BOTTOMRIGHT_TO_TOPLEFT:
				direction = "(Bottom Right to Top Left)";
				break;
			case Constants.BOTTOM_TO_TOP:
				direction = "(Bottom to Top)";
				break;
			case Constants.BOTTOMLEFT_TO_TOPRIGHT:
				direction = "(Bottom Left to Top Right)";
				break;
			default:
				direction = ":(";
				break;
		}
		
		return output + direction;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof String) {
			return ((String) o).compareTo(m_word);
		}
		else if (o instanceof Word) {
			return ((Word) o).toString().compareTo(m_word);
		}
		else {
			System.err.println("Unrecognized compare type: " + o.getClass().getName());
			System.err.println(Thread.currentThread().getStackTrace());
		}
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Word) {
			Word w = (Word) o;
			if (w.toString().equals(m_word)) {
				if (w.getX() == m_x && w.getY() == m_y && w.getDirection() == m_direction) {
					return true;
				}
			}
		}
		return false;
	}
}
