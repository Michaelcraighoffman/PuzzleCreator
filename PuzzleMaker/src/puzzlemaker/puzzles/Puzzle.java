package puzzlemaker.puzzles;

import java.util.ArrayList;

import javax.swing.JPanel;

import puzzlemaker.Constants;
import puzzlemaker.tools.grid.Grid;
import puzzlemaker.tools.grid.GridIterator;

public abstract class Puzzle {
	
	protected Grid m_grid;
	protected ArrayList<Word> m_wordList;
	protected static int[] m_validDirections;
	
	protected JPanel m_displayPanel;
	
	protected void updateDisplayPanel() {
//		m_displayPanel = new JPanel();
//		m_displayPanel.setLayout(new GridLayout(m_grid.size(), m_grid.size()));
//		m_displayPanel.setMinimumSize(new Dimension(m_grid.size() * 30, m_grid.size() * 30));
//		m_displayPanel.setPreferredSize(new Dimension(m_grid.size() * 30, m_grid.size() * 30));
//		
//		for (ArrayList<CharacterField> column : m_grid) {
//			for (CharacterField field : column) {
//				m_displayPanel.add(field);
//			}
//		}
		
		
		// From the old CharacterField constructor (when it used to extend JTextField) :
//		public CharacterField(char chr) {
//		super (Character.toString(Character.toUpperCase(chr)), 1);
//		this.setHorizontalAlignment(JTextField.CENTER);
//		
//		// Changes to JLabel's look and feel (kind of)
//		this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//		this.setDisabledTextColor(Color.black);
//		this.setForeground(Color.black);
//		
//		this.setEnabled(false);
//	}
	}
	
	// TODO: Implement from updateDisplayPanel's commented out code.
	public JPanel getDisplayComponent() {
		if (m_displayPanel == null) {
			m_displayPanel = new JPanel();
		}
		
		
		return m_displayPanel;
	}
	
	public abstract void generate();
	
	public abstract void showSolution();
	
	public abstract void hideSolution();
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":\n" + m_grid.toString();
	}
	
	protected class Word {

		private String m_word;	
		private ArrayList<GridIterator> m_validPlacements;
		

		public Word (String word) {
			m_word = word;
		}
		
		public int findValidPlacements(Grid grid) {
			m_validPlacements = new ArrayList<GridIterator>();
			
			// I know this is super inefficient; I will fix it later.
			// If the grid is empty, then we will traverse checking for valid placement without demanding intersections.
			if (gridIsEmpty(grid)) {
//				System.err.println("grid is empty");
				for (int direction : m_validDirections) {
					for (int x = 0; x < grid.getWidth(); x++) {
						for (int y = 0; y < grid.getHeight(); y++) {
							if (checkPlacement(x, y, direction, 0)) {
								m_validPlacements.add(grid.getIterator(x, y, direction));
							}
						}
					}
				}
			}
			// If the grid is not empty, then we MUST intersect with another word.
			// So for each grid cell, let's find all the places we can intersect with that letter and see if we can find any legal placements.
			else {
				ArrayList<Integer> intersections;
				for (int x = 0; x < grid.getWidth(); x++) {
					for (int y = 0; y < grid.getHeight(); y++) {
						intersections = getIntersectionIndices(m_word, grid.getCharAt(x, y));
						for (int intersection : intersections) {
							for (int direction : m_validDirections) {
								if (checkPlacement(x, y, direction, intersection)) {
//									System.err.println("Placement check PASSED");
									GridIterator validPlacement = grid.getIterator(x, y, direction);
									for (int i = 0; i < intersection; i++) {
										validPlacement.previous();
									}
									m_validPlacements.add(validPlacement);
								}
							}
						}
					}
				}
			}
			
			return m_validPlacements.size();
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
		
		

		
		private boolean gridIsEmpty(Grid grid) {
			for (int x = 0; x < grid.getWidth(); x++) {
				for (int y = 0; y < grid.getHeight(); y++) {
					if (grid.getCharAt(x, y) != Constants.EMPTY_CELL_CHARACTER) {
						return false;
					}
				}
			}
			
			return true;
		}
		
		private ArrayList<Integer> getIntersectionIndices(String word, char c) {
			ArrayList<Integer> intersections = new ArrayList<Integer>();
			String s = word;
			
			while (s.indexOf(c) > -1) {
				intersections.add(s.indexOf(c));
				s = s.substring(s.indexOf(c) + 1);
			}
			
			return intersections;
		}
		
		public ArrayList<GridIterator> getValidPlacements() {
			return m_validPlacements;
		}
		
		public void clearValidPlacements() {
			if (m_validPlacements != null) {
				for (GridIterator iter : m_validPlacements) {
					iter.dispose();
				}
				m_validPlacements = null;
			}
		}
		
		private boolean checkPlacement(int x, int y, int dir, int offset) {
			char gridChar;
			GridIterator iter = m_grid.getIterator(x, y, dir);
			
//			System.err.println("BEFORE: " + iter);
			while (offset > 0) {
				if (!iter.hasPrevious()) {
					iter.dispose();
					return false;
				}
				iter.previous();
				offset--;
			}
//			System.err.println("AFTER: " + iter);
//			System.err.println(m_grid);
			
			
			for (int i = 0; i < m_word.length(); i++) {
				gridChar = m_grid.getCharAt(iter.getX(), iter.getY());
				
				if (m_word.equals("TWO")) {
//					System.err.println("gridChar = " + (int) gridChar + " (" + Character.toString(gridChar) + "), m_word.charAt(i) = " + (int) m_word.charAt(i) + " (" + Character.toString(m_word.charAt(i)));
				}
				
				// Are we placing in an empty or already-correct-letter grid space?
//				System.err.println("Comparing " + Character.toString(gridChar) + " at X: " + iter.getX() + ", Y: " + iter.getY());
				if (gridChar != Constants.EMPTY_CELL_CHARACTER && gridChar != m_word.charAt(i)) {
					iter.dispose();
					return false;
				}
				
				// Try to move the iterator forward. If we can't and we are not at the end of the word, return false.
				if (!iter.hasNext()) {
					if (i != m_word.length() - 1) {
						iter.dispose();
						return false;
					}
					return true;
				}
				iter.next();
			}
			
//			System.err.println("VALID: (" +);
			
			return true;
		}
		
		// TODO: there's a smarter wawy to write this.. but without the hasNext check it goes out by one.
		// but we already know tht the placement is valid...
		public void placeInGrid(Grid grid, int placementIndex) {
			GridIterator iter = m_validPlacements.get(placementIndex);
			
			for (int i = 0; i < m_word.length(); i++) {
				grid.setCharAt(iter.getX(), iter.getY(), m_word.charAt(i));
//				grid.get(iter.x).get(iter.y).importance++;
				if (iter.hasNext()) {
					iter.next();
				}
			}
			iter.dispose();
		}
		
//		public void removeFromGrid() {
//			PointAndDirection start = m_currentPlacement;
//			CharacterField field;
//			
//			for (int i = 0; i < m_word.length(); i++) {
//				field = m_grid.get(start.row).get(start.col);
//				
//				field.importance--;
//				if (field.importance == 0) {
//					field.setText(' ');
//				}
//				
//				start.moveForward();
//			}
//			
//			m_isPlaced = false;
//			m_currentPlacement = null;
//			
//			System.err.println("AFTER REMOVAL: ");
//			System.err.println(Puzzle.this.toString());
//		}
		
		public boolean hasValidPlacements() {
			return (m_validPlacements.size() > 0);
		}
		
		@Override
		public String toString() {
			return m_word;
		}
	}
}
