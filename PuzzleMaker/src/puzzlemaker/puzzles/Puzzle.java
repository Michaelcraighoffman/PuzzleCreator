package puzzlemaker.puzzles;

import java.util.ArrayList;

import javax.swing.JTextField;

import puzzlemaker.Constants;
import puzzlemaker.tools.grid.Grid;
import puzzlemaker.tools.grid.GridWalker;

public abstract class Puzzle implements Comparable<Puzzle> {
	
	protected Grid m_grid;
	protected ArrayList<Word> m_wordList;
		
	public Grid getGrid() {
		return m_grid;
	}
	
	public ArrayList<Word> getWordList() {
		return m_wordList;
	}
	
	public boolean isLegal() {
		int[] validDirections;
		if (this instanceof Crossword) {
			validDirections = Constants.CROSSWORD_DIRECTIONS;
		}
		else if (this instanceof WordSearch) {
			validDirections = Constants.WORDSEARCH_DIRECTIONS;
		}
		else {
			return false;
		}
				
		GridWalker walker = new GridWalker(m_grid);
		char cellChar;
		String letters;
		ArrayList<Word> newWordList = new ArrayList<Word>();
		for (int direction : validDirections) {
			walker.dir = direction;
			walker.jumpToStart();
			
			do {
				letters = "";
				do {
					cellChar = m_grid.getCharAt(walker.x, walker.y);
//					System.out.println("Found \'" + cellChar + "\' at (" + walker.x + "," + walker.y + ");" + walker.dir);
					
					if (cellChar == Constants.EMPTY_CELL_CHARACTER) {
						if (letters.length() < 2) {
							letters = "";
						}
						else {
							boolean found = false;
							for (int i = 0; i < m_wordList.size(); i++) {
								
								if (this instanceof Crossword) {
									if (letters.equals(m_wordList.get(i).toString())) {
										Word tmpWord = m_wordList.remove(i);
										walker.setWordData(tmpWord, 0, 0);
										newWordList.add(tmpWord);
										found = true;
										letters = "";
									}
								}
								else if (this instanceof WordSearch) {
									if (letters.contains(m_wordList.get(i).toString())) {
										Word tmpWord = m_wordList.remove(i);
										walker.setWordData(tmpWord, letters.indexOf(tmpWord.toString()), letters.length() - (letters.indexOf(tmpWord.toString()) + tmpWord.toString().length()) );
										newWordList.add(tmpWord);
										found = true;
										letters = "";
									}
								}
							}
							
							if (!found) {
								if (this instanceof Crossword) {
									return false;
								}
								else if (this instanceof WordSearch) {
									letters = "";
								}
							}
						}
					}
					else {
						letters += cellChar;
					}
				} while (walker.tryNextCell());
				
				// Before going to the new line...
				if (letters.length() < 2) {
					letters = "";
				}
				else if (!m_wordList.isEmpty()) {
					boolean found = false;
					for (int i = 0; i < m_wordList.size(); i++) {
						if (this instanceof Crossword) {
							if (letters.equals(m_wordList.get(i).toString())) {
								Word tmpWord = m_wordList.remove(i);
								walker.setWordData(tmpWord, 0, 0);
								newWordList.add(tmpWord);
								found = true;
								letters = "";
							}
						}
						else if (this instanceof WordSearch) {
							if (letters.contains(m_wordList.get(i).toString())) {
								Word tmpWord = m_wordList.remove(i);
								walker.setWordData(tmpWord, letters.indexOf(tmpWord.toString()), letters.length() - (letters.indexOf(tmpWord.toString()) + tmpWord.toString().length()));
								newWordList.add(tmpWord);
								found = true;
								letters = "";
							}
						}
					}
					
					if (!found) {
						if (this instanceof Crossword) {
							return false;
						}
						else if (this instanceof WordSearch) {
							letters = "";
						}
					}
				}
				
			} while (walker.tryNextLine());	
		}
		
		if (m_wordList.isEmpty()) {
			m_wordList = newWordList;
		}
		else {
			return false;
		}
		
		return true;
	}
	
	
	public abstract void applyCellStyle(JTextField cell, boolean showSolution);		
		
	@Override
	public String toString() {
		String output = this.getClass().getSimpleName() + ":\n" + m_grid.toString();
		for (Word w : m_wordList) {
			output = output + "\n" +  w.toStringDetailed();
		}
		
		return output;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Puzzle) {
			Puzzle p = (Puzzle) o;
			
			return (this.getGrid().equals(p.getGrid()));
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int compareTo(Puzzle p) {
		return (this.getGrid().compareTo(p.getGrid()));
	}
}
