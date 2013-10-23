package puzzlemaker.tools.grid;

import puzzlemaker.Constants;

public class GridCell {
	private char m_char;
	
	public GridCell(char character) {
		m_char = character;
	}
	
	public char getChar() {
		return m_char;
	}
	
	public void setChar(char character) {
		m_char = character;
	}
	
	public String toString() {
		if (m_char == ' ') {
			return Character.toString(Constants.EMPTY_CELL_CHARACTER);
		}
		return Character.toString(m_char);
	}
}
