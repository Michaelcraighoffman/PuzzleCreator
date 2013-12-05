package puzzlemaker.tools.grid;

import java.util.ArrayList;

import puzzlemaker.Constants;

/** A Grid is a 2D ArrayList of Characters.<br>
 * A grid's dimensions can range (0, 128) or [1, 127].
 * 
 * @author Samuel Wiley
 *
 */
public class Grid implements Comparable<Grid> {
	ArrayList<ArrayList<Character>> m_data;
	
	public Grid(int width, int height) {
		m_data = new ArrayList<ArrayList<Character>>(width);
		ArrayList<Character> gridColumn;
		for (int x = 0; x < width; x++) {
			gridColumn = new ArrayList<Character>(height);
			for (int y = 0; y < height; y++) {
				gridColumn.add(Constants.EMPTY_CELL_CHARACTER);
			}
			m_data.add(gridColumn);
		}
	}
	
	public Grid(Grid g) {
		m_data = new ArrayList<ArrayList<Character>>(g.getWidth());
		ArrayList<Character> gridColumn;
		for (int x = 0; x < g.getWidth(); x++) {
			gridColumn = new ArrayList<Character>(g.getHeight());
			for (int y = 0; y < g.getHeight(); y++) {
				gridColumn.add(g.getCharAt(x, y));
			}
			m_data.add(gridColumn);
		}
	}
	
	public char getCharAt(int x, int y) {
		return m_data.get(x).get(y);
	}
	
//	/** @return Whether or not the location specified was within bounds. */
//	public boolean setCharAt(int x, int y, char c) {
//		if (x < m_data.size() && y < m_data.get(0).size()) {
//			m_data.get(x).set(y, c);
//			return true;
//		}
//		return false;
//	}
	
	public void setCharAt (int x, int y, char c) {
		m_data.get(x).set(y, c);
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
		ArrayList<Character> column = new ArrayList<Character>(this.getHeight());
		for (int i = 0; i < this.getHeight(); i++) {
			column.add(Constants.EMPTY_CELL_CHARACTER);
		}
		
		m_data.ensureCapacity(m_data.size() + 1);
		m_data.add(0, column);
	}
	
	public void addColumnOnRight() {
		ArrayList<Character> column = new ArrayList<Character>(this.getHeight());
		for (int i = 0; i < this.getHeight(); i++) {
			column.add(Constants.EMPTY_CELL_CHARACTER);
		}
		
		m_data.ensureCapacity(m_data.size() + 1);
		m_data.add(column);
	}
	
	public void addRowOnTop() {
		ArrayList<Character> column;
		for (int i = 0; i < this.getWidth(); i++) {
			column = m_data.get(i);
			column.ensureCapacity(column.size() + 1);
			column.add(0, Constants.EMPTY_CELL_CHARACTER);
		}
	}
	
	public void addRowOnBottom() {
		ArrayList<Character> column;
		for (int i = 0; i < this.getWidth(); i++) {
			column = m_data.get(i);
			column.ensureCapacity(column.size() + 1);
			column.add(Constants.EMPTY_CELL_CHARACTER);
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
	
	public void makeSquare() {
		int difference = getWidth()-getHeight();
		if(difference==0) return;
		while(difference > 0) {
			//TODO: is there no cheap operation for getting least significant bit?
			// or maybe a mod 2 gets optimized to the equivalent?
			switch(difference%2){
				case 0:
					addRowOnBottom();
					break;
				case 1:
					addRowOnTop();
					break;
			}
			difference--;
		}
		if(difference==0) {
			return;
		}
		while(difference < 0) {
			switch(Math.abs(difference%2)){
				case 0:
					addColumnOnLeft();
					break;
				case 1:
					addColumnOnRight();
					break;
			}
			difference++;
		}
	}
	
	
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Grid) {
			Grid g = (Grid) o;
			
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
		else
		{
			return false;
		}
	}
	
	@Override
	public String toString() {
		String output = "";
		char cellChar;
		for (int y = 0; y < this.getHeight(); y++) {
			for (int x = 0; x < this.getWidth(); x++) {
				cellChar = m_data.get(x).get(y);
				
				// To make reading console output easier.
				if (cellChar == Constants.EMPTY_CELL_CHARACTER) {
					cellChar = '*'; 
				}

				output += cellChar + " ";
			}
			output += "\n";
		}
		
		output = output.substring(0, output.length() - 1);
		output = output.toUpperCase();
		return output;
	}
	
	/** Set all the ArrayLists in m_data to null. */
	public void dispose() {
		ArrayList<Character> column;

		while (!m_data.isEmpty()) {
			column = m_data.remove(0);
			while (!column.isEmpty()) {
				column.remove(0);
			}
			column = null;
		}
		
		m_data = null;
	}

	@Override
	public int compareTo(Grid g) {
		if ((this.getWidth() + this.getHeight()) == (g.getWidth() + g.getHeight())) {
			if (this.getWidth() == g.getWidth()) {
				for (int x = 0; x < this.getWidth(); x++) {
					for (int y = 0; y < this.getHeight(); y++) {
						if (this.getCharAt(x, y) != g.getCharAt(x, y)) {
							return (this.getCharAt(x, y) - g.getCharAt(x, y));
						}
					}
				}
				return 0;
			}
			else {
				return (this.getWidth() - g.getWidth());
			}
		}
		else {
			return (this.getWidth() + this.getHeight()) - (g.getWidth() + g.getHeight());
		}
	}
}
