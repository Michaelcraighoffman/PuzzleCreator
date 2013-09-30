package puzzlemaker.tools;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import puzzlemaker.Constants;

/** This class manages the list of words, their associated labels,
  * the existence those labels in their display panel}, and all
  * of their mouse-related interactivity.
  * 
  * @author szeren
  */
public class WordList implements ActionListener, MouseListener, ComponentListener {
	
	ArrayList<WordListItem> m_data;
	JPanel m_displayPanel;
	WordListItem m_selected;

	private JPopupMenu m_popupMenu;
	
	// For managing the WordListPanel's layout
	private final int ROW_HEIGHT = 16;
	private final int ROW_GAP = 3;
	private final int COLUMN_GAP = 10;
	
	/** @param displayPanel The panel that the words' labels should be placed in. */
	public WordList(JPanel displayPanel) {
		m_data = new ArrayList<WordListItem>();
		m_displayPanel = displayPanel;
		m_popupMenu = Model.createPopupMenu("WordLabel", new String[] {"Delete"}, this);
	}
	
	/** @return The word list as an {@code ArrayList<String>}. */
	public ArrayList<String> getWords() {
		ArrayList<String> words = new ArrayList<String>(m_data.size());
		for (WordListItem item : m_data) {
			words.add(item.getWord());
		}
		
		return words;
	}
	
	public int getSize() {
		return m_data.size();
	}
	
	/** Adds the word to the list <b>if</b> the word's length
	  * is non-zero after being filtered to only letters.<br>
	  * Capitalizes the word and adds it to the display panel.
	  * @param word The word to be added.
	  * @return <b>true</b> if the add was successful. */
	public boolean addWord(String word) {
		word = filterToLetters(word.trim().toUpperCase());
		
		if (word.length() < 1) {
			return false;
		}
		
		if (m_data.add(new WordListItem(word))) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean deleteSelectedWord() {
		if (m_selected.getLabel() == null) {
			return false;
		}
		m_selected.destroy();
		m_selected = null;
		return true;
	}
	
	/** @return If a selected word was de-selected. */
	public boolean deselectWord() {
		if (m_selected == null) {
			return false;
		}
		
		for (WordListItem item : m_data) {
			item.setBold(false);
		}
		
		return true;
	}
	
    /** Used to pair a String with a JLabel and manage the label's 
      * existence in the display panel.
	  * 
	  * @author szeren */
 	private class WordListItem {
		private String m_word;
		private JLabel m_label;
		
		public WordListItem(String word) {
			m_word = word;
			m_label = new JLabel(word);
			m_label.setFont(m_label.getFont().deriveFont(Font.PLAIN));
			
//			For debugging - to easily see the click-able area of the label.
//			m_label.setBackground(Color.white);
//			m_label.setOpaque(true);

			m_label.setName(Constants.DELETE_WORD_LABEL);
			m_label.addMouseListener(WordList.this); // refers to owner's (WordList's) instance		
			
			m_displayPanel.add(m_label);
			makeLayout();			
		}
		
		/** @return The word. */
		public String getWord() {
			return m_word;
		}
		
		/** @return The label. */
		public JLabel getLabel() {
			return m_label;
		}
		
		public int getSmallestWidth() {
			return m_label.getFontMetrics(m_label.getFont()).stringWidth(m_word);
		}
		
		/** @param bold Whether or not the label's text should be bold. */
		public void setBold(boolean bold) {
			int fontStyle;
			if (bold) {
				fontStyle = Font.BOLD;
			} else {
				fontStyle = Font.PLAIN;
			}
			
			m_label.setFont(m_label.getFont().deriveFont(fontStyle));
		}
		
		/** Handles all cleanup related to this WordListLabel. */
		public void destroy() {
			m_displayPanel.remove(m_label);
			m_data.remove(this);
			m_label.removeAll();
			m_word = null;
			
			if (!WordList.this.getWords().isEmpty()) {
				makeLayout();
			}
			else {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run () {
						m_displayPanel.getParent().repaint();
					}
				});
			}
		}
	}
	
	/** Sets the locations and sizes of all of the word list's labels using a SpringLayout. */
	private void makeLayout() {
		SpringLayout layout = (SpringLayout) m_displayPanel.getLayout();
				
		// The "available" prefix refers to the limit of the display panel.
        int availableHeight = m_displayPanel.getSize().height - (m_displayPanel.getInsets().top + m_displayPanel.getInsets().bottom);
		int availableWidth = m_displayPanel.getSize().width - (m_displayPanel.getInsets().left + m_displayPanel.getInsets().right);

        int availableRows = availableHeight / (ROW_HEIGHT + ROW_GAP);
        int rows = m_displayPanel.getComponentCount() > availableRows ? availableRows : m_displayPanel.getComponentCount();
		int columns = (m_displayPanel.getComponentCount() / availableRows) + 1;
		if (m_displayPanel.getComponentCount() % availableRows == 0) {
			columns--;
		}
		int maxColumnWidth = (availableWidth - ((columns - 1) * COLUMN_GAP)) / columns;

		
		// Set label locations and sizes.
		Component currentComponent;
		int x = 0;
		int componentIndex = 0;
		int biggestWidthInColumn = 0;
		for (int c = 0; c < columns; c++) {
			for (int r = 0; r < rows; r++) {
				if (componentIndex == m_displayPanel.getComponentCount()) {
					break;
				}
				currentComponent = m_displayPanel.getComponent(componentIndex);
				SpringLayout.Constraints constraints = layout.getConstraints(currentComponent);
				
				constraints.setX(Spring.constant(x));
				constraints.setY(Spring.constant(r * (ROW_HEIGHT + ROW_GAP)));
				int labelWidth = Math.min(maxColumnWidth, getLabelMinimumWidth((JLabel) currentComponent));
				constraints.setWidth(Spring.constant(labelWidth));
				constraints.setHeight(Spring.constant(ROW_HEIGHT));
				
				biggestWidthInColumn = Math.max(labelWidth, biggestWidthInColumn);
				componentIndex++;
			}
			x += biggestWidthInColumn + COLUMN_GAP;
			biggestWidthInColumn = 0;
		}
		
		SpringLayout.Constraints panelConstraints = layout.getConstraints(m_displayPanel);
		panelConstraints.setConstraint(SpringLayout.EAST, Spring.constant((columns * (maxColumnWidth + COLUMN_GAP)) - COLUMN_GAP));
		panelConstraints.setConstraint(SpringLayout.SOUTH, Spring.constant((rows * (ROW_HEIGHT + ROW_GAP)) - ROW_GAP));
		
		m_displayPanel.revalidate();
		
		/* When the most recently entered word gets deleted, the part of the word that wasn't covered
		 * by the popup menu hangs around because m_displayPanel shrinks and THEN revalidates its area.
		 * For this reason, we need to repaint m_displayPanel's parent. */
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run () {
				m_displayPanel.getParent().repaint();
			}
		});
	}
	
	private int getLabelMinimumWidth(JLabel label) {
		return label.getFontMetrics(label.getFont()).stringWidth(label.getText());
	}
	
	/** @returns The string with all non-letters removed. */
	private String filterToLetters(String word) {
		char[] input = word.toCharArray();
		String output = "";
		
		for (char c : input) {
			if (Character.isLetter(c)) {
				output = output + c;
			}
		}
		return output;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if (command.equals("WORDLABEL_DELETE")) {
			deleteSelectedWord();
		}
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
				
		// Without this check, exceptions will be thrown during initial loading.
		if (m_displayPanel.getComponentCount() > 0) {
			makeLayout();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getComponent().getName().equals(Constants.DELETE_WORD_LABEL)) {
			// "Select" the label and "deselect" all others.
			for (WordListItem wll : m_data) {
				if (wll.getLabel().equals(e.getSource())) {
					wll.setBold(true);
					m_selected = wll;
					wll.getLabel().setSize(wll.getSmallestWidth(), wll.getLabel().getHeight());
				} else {
					wll.setBold(false);
				}
				
				makeLayout(); // Since the label's width will change.
			}
			
			// If it was a right-click, also show the popup menu.
			if (e.getButton() == MouseEvent.BUTTON3) {
				/* TODO: This might need testing on a multi-monitor device;
				 * getX and getY might not work properly. They have alternatives,
				 * getXOnScreen and getYOnScreen, but I haven't tried them. -SBW */
				m_popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}
	
}
