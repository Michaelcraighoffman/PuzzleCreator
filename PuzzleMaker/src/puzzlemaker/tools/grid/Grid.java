package puzzlemaker.tools.grid;

import java.util.ArrayList;

import puzzlemaker.model.Constants;

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
			if (c == ' ' || c == Constants.EMPTY_CELL_CHARACTER) {
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
	
	public boolean isEmpty() {
		for (int x = 0; x < this.getWidth(); x++) {
			for (int y = 0; y < this.getHeight(); y++) {
				if (this.getCharAt(x, y) != Constants.EMPTY_CELL_CHARACTER) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void ensureCapacity(int size) {
		ensureCapacity(size, size);
	}
	
	public void ensureCapacity(int width, int height) {
		int rowsNeeded = height - this.getHeight();
		int colsNeeded = width - this.getWidth();
		
		if (rowsNeeded > 0) {
			for (int i = 0; i < rowsNeeded; i++) {
				this.addRowOnBottom();
			}
		}
		
		if (colsNeeded > 0) {
			for (int i = 0; i < colsNeeded; i++) {
				this.addColumnOnRight();
			}
		}
	}
	
	public int getWidth() {
		return m_data.size();
	}
	
	public int getHeight() {
		return m_data.get(0).size();
	}
	
	public void addColumnOnLeft() {
		ArrayList<GridCell> column = new ArrayList<GridCell>(this.getHeight());
		for (int i = 0; i < this.getHeight(); i++) {
			column.add(Constants.EMPTY_CELL);
		}
		
		m_data.ensureCapacity(m_data.size() + 1);
		m_data.add(0, column);
	}
	
	public void addColumnOnRight() {
		ArrayList<GridCell> column = new ArrayList<GridCell>(this.getHeight());
		for (int i = 0; i < this.getHeight(); i++) {
			column.add(Constants.EMPTY_CELL);
		}
		
		m_data.ensureCapacity(m_data.size() + 1);
		m_data.add(column);
	}
	
	public void addRowOnTop() {
		ArrayList<GridCell> column;
		for (int i = 0; i < this.getWidth(); i++) {
			column = m_data.get(i);
			column.ensureCapacity(column.size() + 1);
			column.add(0, Constants.EMPTY_CELL);
		}
	}
	
	public void addRowOnBottom() {
		ArrayList<GridCell> column;
		for (int i = 0; i < this.getWidth(); i++) {
			column = m_data.get(i);
			column.ensureCapacity(column.size() + 1);
			column.add(Constants.EMPTY_CELL);
		}
	}
	
	/** Removes empty columns and empty rows and trims all ArrayLists' capacities to size. */
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
			m_data.get(x).trimToSize();
		}
	}
	
	private void removeColumn(int x) {
		m_data.remove(x);
		m_data.trimToSize();
	}
	
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
		char cellChar;
		for (int y = 0; y < this.getHeight(); y++) {
			for (int x = 0; x < this.getWidth(); x++) {
				cellChar = m_data.get(x).get(y).getChar();
				
				// To make reading console output easier.
				if (cellChar == Constants.EMPTY_CELL_CHARACTER) {
					cellChar = '*'; 
				}
				
				output += cellChar + " ";
			}
			output += "\n";
		}
		
		output = output.substring(0, output.length() - 1);
		return output;
	}
	
	/** Set all the ArrayLists in m_data (and possibly also all tracked iterators) to null. */
	public void dispose() {
		ArrayList<GridCell> column;

		while (!m_data.isEmpty()) {
			column = m_data.remove(0);
			while (!column.isEmpty()) {
				column.remove(0);
			}
			column = null;
		}
	}
}
