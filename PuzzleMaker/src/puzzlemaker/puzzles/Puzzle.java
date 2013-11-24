package puzzlemaker.puzzles;

import java.util.ArrayList;

import javax.swing.JTextField;

import puzzlemaker.tools.grid.Grid;

public abstract class Puzzle implements Comparable<Puzzle> {
	
	protected Grid m_grid;
	protected ArrayList<Word> m_wordList;

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
		
	public Grid getGrid() {
		return m_grid;
	}
	
	public ArrayList<Word> getWordList() {
		return m_wordList;
	}
	
	public abstract void applyCellStyle(JTextField cell,Boolean showSolution);
		
	public abstract void makeSquare();
	
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
