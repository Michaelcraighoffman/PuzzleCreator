package puzzlemaker.puzzles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class Puzzle {
	
	protected String[] m_wordList;
	protected JPanel m_displayPanel;
	protected ArrayList<ArrayList<CharacterField>> m_grid;
	
	protected void instantiatePuzzle(int minSize) {
		if (minSize < 1) {
			minSize = 8;
		}
		
		m_grid = new ArrayList<ArrayList<CharacterField>>(minSize);

		ArrayList<CharacterField> tmpColumn;
		CharacterField tmpField;
		
		for (int i = 0; i < minSize; i++) {
			tmpColumn = new ArrayList<CharacterField>(minSize);
			for (int j = 0; j < minSize; j++) {
				tmpField = new CharacterField((char)(j+i));
				tmpColumn.add(tmpField);
			}
			m_grid.add(tmpColumn);
		}
	}
	
	protected void updateDisplayPanel() {
		m_displayPanel = new JPanel();
		m_displayPanel.setLayout(new GridLayout(m_grid.size(), m_grid.size()));
		m_displayPanel.setMinimumSize(new Dimension(m_grid.size() * 30, m_grid.size() * 30));
		m_displayPanel.setPreferredSize(new Dimension(m_grid.size() * 30, m_grid.size() * 30));
		
		for (ArrayList<CharacterField> column : m_grid) {
			for (CharacterField field : column) {
				m_displayPanel.add(field);
			}
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
	
	public abstract void generate(ArrayList<String> wordList);
	
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
	
	protected class CharacterField extends JTextField {
		private static final long serialVersionUID = 1L;
		
		public CharacterField(char chr) {
			super (Character.toString(Character.toUpperCase(chr)), 1);
			this.setHorizontalAlignment(JTextField.CENTER);
			
			// Changes to JLabel's look and feel (kind of)
			this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			this.setDisabledTextColor(Color.black);
			this.setForeground(Color.black);
			
			this.setEnabled(false);
		}
		
		public void setText(char chr) {
			this.setText(Character.toString(chr));
		}
		
	}
}
