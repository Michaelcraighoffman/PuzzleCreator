package puzzlemaker.puzzles;

import java.util.ArrayList;

import puzzlemaker.tools.grid.Grid;
import puzzlemaker.tools.grid.GridIterator;

public class Word implements Comparable<Object> {

	private String m_word;	
	private int m_x;
	private int m_y;
	private int m_direction;
	
	private ArrayList<GridIterator> m_validPlacements;
	
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

	public void placeInGrid(Grid grid, int placementIndex) {
		GridIterator iter = m_validPlacements.get(placementIndex);
		
		for (int i = 0; i < m_word.length(); i++) {
			grid.setCharAt(iter.getX(), iter.getY(), m_word.charAt(i));
//			grid.get(iter.x).get(iter.y).importance++;
			if (iter.hasNext()) {
				iter.next();
			}
		}
		iter.dispose();
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
	
//	public void removeFromGrid() {
//		PointAndDirection start = m_currentPlacement;
//		CharacterField field;
//		
//		for (int i = 0; i < m_word.length(); i++) {
//			field = m_grid.get(start.row).get(start.col);
//			
//			field.importance--;
//			if (field.importance == 0) {
//				field.setText(' ');
//			}
//			
//			start.moveForward();
//		}
//		
//		m_isPlaced = false;
//		m_currentPlacement = null;
//		
//		System.err.println("AFTER REMOVAL: ");
//		System.err.println(Puzzle.this.toString());
//	}
	
	public boolean hasValidPlacements() {
		return (m_validPlacements.size() > 0);
	}
	
	@Override
	public String toString() {
		return m_word;
	}
	
	public String toStringDetailed() {
		return m_word + " x: " + m_x + " y: " + m_y + " dir: " + m_direction;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof String) {
			return ((String) o).compareTo(m_word);
		}
		else if (o instanceof Word) {
			((Word) o).toString().compareTo(m_word);
		}
		else {
			System.err.println("Unrecognized compare type: " + o.getClass().getName());
			System.err.println(Thread.currentThread().getStackTrace());
		}
		return 0;
	}
}
