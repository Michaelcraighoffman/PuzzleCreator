package puzzlemaker.puzzles;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;

import puzzlemaker.Constants;
import puzzlemaker.tools.grid.Grid;
import puzzlemaker.tools.grid.GridWalker;

public class Crossword extends Puzzle {
	
	private static final Border m_border = BorderFactory.createLineBorder(Color.black, 2);
	
	static final int[] m_validDirections = Constants.CROSSWORD_DIRECTIONS;
	LinkedBlockingQueue<Grid> m_solutions;
	
	public Crossword(Grid grid, ArrayList<Word> wordList) {
		m_grid = grid;
		m_wordList = wordList;
	}
	
	@Override
	public boolean isLegal() {
		GridWalker walker = new GridWalker(m_grid);
		char cellChar;
		String letters;
		ArrayList<Word> newWordList = new ArrayList<Word>();
		
		for (int direction : Constants.CROSSWORD_DIRECTIONS) {
			walker.dir = direction;
			walker.jumpToStart();
			
			do {
				letters = "";
				do {
					cellChar = m_grid.getCharAt(walker.x, walker.y);
//					System.out.println("Found \'" + cellChar + "\' at (" + walker.x + "," + walker.y + ");" + walker.dir);
					
					if (cellChar != Constants.EMPTY_CELL_CHARACTER) {
						letters += cellChar;
					}
					else {
						if (letters.length() < 2) {
							letters = "";
						}
						else {
							boolean found = false;
							for (int i = 0; i < m_wordList.size(); i++) {
								if (letters.equals(m_wordList.get(i).toString())) {
									Word tmpWord = m_wordList.remove(i);
									walker.setWordData(tmpWord, 0, 0);
									newWordList.add(tmpWord);
									found = true;
									letters = "";
									break;
								}
							}
							if (!found) { return false; }
						}
					}
				} while (walker.tryNextCell());
				
				// Before going to the new line...
				if (letters.length() > 1) {
						boolean found = false;
						for (int i = 0; i < m_wordList.size(); i++) {
							if (letters.equals(m_wordList.get(i).toString())) {
								Word tmpWord = m_wordList.remove(i);
								walker.setWordData(tmpWord, 0, 0);
								newWordList.add(tmpWord);
								found = true;
								break;
							}
						}
						if (!found) { return false; }
				}
			} while (walker.tryNextLine());	
		}
		
		if (m_wordList.isEmpty()) {
			m_wordList = newWordList;
			return true;
		}
		else {
			System.out.println("Crossword.isLegal(): not all words found in puzzle. Logic error in generation algorithm?");
			return false;
		}
	}

	@Override
	public void applyCellStyle(JTextField cell, boolean showSolution) {
		cell.setHorizontalAlignment(JTextField.CENTER);
		cell.setBorder(m_border);
		cell.setDisabledTextColor(Color.black);
		cell.setForeground(Color.black);
		cell.setEnabled(false);
			
		
		if (cell.getText().equals(Character.toString(Constants.EMPTY_CELL_CHARACTER))) {
			cell.setBackground(Color.black);
			return;
		}
		else {
			cell.setBackground(Color.white);
		}
		
		if(showSolution) {
			cell.setFont(cell.getFont().deriveFont(Font.BOLD));
		}
		else {
			cell.setText("");
		}
		
	}
}
