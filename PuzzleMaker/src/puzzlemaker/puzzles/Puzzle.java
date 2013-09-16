package puzzlemaker.puzzles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import puzzlemaker.gui.CharacterField;

public abstract class Puzzle {
	
	protected ArrayList<ArrayList<CharacterField>> m_grid;
	protected JPanel m_displayPanel;
	
//	public Puzzle() {
//	}
	
	protected void instantiatePuzzle(int minSize) {
		m_grid = new ArrayList<ArrayList<CharacterField>>(minSize);
		m_displayPanel = new JPanel();
		// This line border still doesn't "hug" the characters..
		m_displayPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		m_displayPanel.setLayout(new GridLayout(minSize, minSize));
		m_displayPanel.setMinimumSize(new Dimension(minSize * 30, minSize * 30));
		m_displayPanel.setPreferredSize(new Dimension(minSize * 30, minSize * 30));

		ArrayList<CharacterField> tmpColumn;
		CharacterField tmpField;
		
		for (int i = 0; i < minSize; i++) {
			tmpColumn = new ArrayList<CharacterField>(minSize);
			for (int j = 0; j < minSize; j++) {
				tmpField = new CharacterField((char)(j+i));
				tmpColumn.add(tmpField);
				
				m_displayPanel.add(tmpField);

			}
			m_grid.add(tmpColumn);
		}
	}
	
	public int getSize() {
		return m_grid.size();
	}
	
	public JPanel getDisplayComponent() {
		return m_displayPanel;
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
	public abstract void generate(String[] wordList);
	
	public abstract void showSolution();
	
	public abstract void hideSolution();
	
	@Override
	public String toString() {
		String out = "";
		String append = "";
		
		for (int i = 0; i < m_grid.size(); i++) {
			for (int j = 0; j < m_grid.get(0).size(); j++) {
				append = m_grid.get(i).get(j).getText();
				if (append.length() == 0) {
					out += "  ";
				}
				else {
					out = out + append + " ";
				}
			}
			
			out = out + "\r";
		}
		
		return out;
	}
}
