package puzzlemaker.puzzles;

import java.util.ArrayList;

import puzzlemaker.gui.CharacterField;

public abstract class Puzzle {
	
	private ArrayList<String> m_wordList;
	private ArrayList<ArrayList<CharacterField>> m_grid;
	
	public Puzzle(int width, int height) {
		m_grid = new ArrayList<ArrayList<CharacterField>>(width);
		
		for (ArrayList<CharacterField> column : m_grid) {
			column = new ArrayList<CharacterField>(height);
			column.add(new CharacterField());
		}
	}
	
	public void addWord(String word) {
		m_wordList.add(word);
	}
	
	public void removeWord(String word) {
		if (!m_wordList.remove(word)) {
			System.err.println("removeWord returned false for: \"" + word + "\" (word not found)");
		}
	}
	
	public void clearGrid() {
		for (ArrayList<CharacterField> column : m_grid) {
			for (CharacterField character : column) {
				character.setText("");
			}
		}
	}
	
	/**
	 * Do something like clear(), generate()
	 */
	public abstract void randomize();
	
	public abstract void showSolution();
	
	public abstract void hideSolution();
	
}
