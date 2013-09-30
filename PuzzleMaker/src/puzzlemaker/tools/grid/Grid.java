package puzzlemaker.tools.grid;

import java.util.ArrayList;

import puzzlemaker.Constants;

public class Grid {
	ArrayList<ArrayList<GridCell>> m_data;
	
	public Grid(int width, int height) {
		m_data = new ArrayList<ArrayList<GridCell>>(width);
		ArrayList<GridCell> gridColumn;
		for (int x = 0; x < width; x++) {
			gridColumn = new ArrayList<GridCell>(height);
			for (int y = 0; y < height; y++) {
				gridColumn.add(Constants.EMPTY_CELL);
			}
			m_data.add(gridColumn);
		}
	}
	
	public Grid(Grid g) {
		m_data = new ArrayList<ArrayList<GridCell>>(g.getWidth());
		ArrayList<GridCell> gridColumn;
		for (int x = 0; x < g.getWidth(); x++) {
			gridColumn = new ArrayList<GridCell>(g.getHeight());
			for (int y = 0; y < g.getHeight(); y++) {
				if (g.getCharAt(x, y) == ' ') {
					gridColumn.add(Constants.EMPTY_CELL);
				}
				else {
					gridColumn.add(new GridCell(g.getCharAt(x, y)));
				}
			}
			m_data.add(gridColumn);
		}
	}
	
	public char getCharAt(int x, int y) {
		return m_data.get(x).get(y).getChar();
	}
	
	/** @return Whether or not the location specified was within bounds. */
	public boolean setCharAt(int x, int y, char c) {
		if (x < m_data.size() && y < m_data.get(0).size()) {
			if (c == ' ') {
				m_data.get(x).set(y, Constants.EMPTY_CELL);
			}
			else {
				if (m_data.get(x).get(y) == Constants.EMPTY_CELL) {
					m_data.get(x).set(y, new GridCell(c));
				}
				else {
					m_data.get(x).get(y).setChar(c);
				}
			}
			return true;
		}
		return false;
	}
	
	public int getWidth() {
		return m_data.size();
	}
	
	public int getHeight() {
		return m_data.get(0).size();
	}
	
	public void trim() {
		nextColumn:
			for (int x = 0; x < this.getWidth();) {
				for (int y = 0; y < this.getHeight(); y++) { 
					if (this.getCharAt(x, y) != Constants.EMPTY_CELL_CHARACTER) {
						x++;
						continue nextColumn;
					}
				}
				
				this.removeColumn(x);
			}
		
		nextRow:
			for (int y = 0; y < this.getHeight();) {
				for (int x = 0; x < this.getWidth(); x++) {
					if (this.getCharAt(x, y) != Constants.EMPTY_CELL_CHARACTER) {
						y++;
						continue nextRow;
					}
				}
				
				this.removeRow(y);
			}
	}
	
	private void removeRow(int y) {
		for (int x = 0; x < this.getWidth(); x++) {
			m_data.get(x).remove(y);
		}
	}
	
	private void removeColumn(int x) {
		m_data.remove(x);
	}
	
	/** Do these need to be tracked to prevent memory leaks? */
	public GridIterator getIterator(int x, int y, int dir) {
		return new GridIterator(this, x, y, dir);
	}
	
	public boolean equals(Grid g) {
		if (this.getWidth() != g.getWidth() || this.getHeight() != g.getHeight()) {
			return false;
		}
		
		for (int x = 0; x < this.getWidth(); x++) {
			for (int y = 0; y < this.getHeight(); y++) {
				if (this.getCharAt(x, y) != g.getCharAt(x, y)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		String output = "";
		for (int y = 0; y < this.getHeight(); y++) {
			for (int x = 0; x < this.getWidth(); x++) {
				output += m_data.get(x).get(y).toString() + " ";
			}
			output += "\n";
		}
		
		return output;
	}
	
	/** Set all the ArrayLists in m_data (and possibly also all tracked iterators) to null. */
	public void dispose() {
		
	}
}
