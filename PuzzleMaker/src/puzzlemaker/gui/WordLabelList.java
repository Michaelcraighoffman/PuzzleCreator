package puzzlemaker.gui;

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
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import puzzlemaker.Constants;
import puzzlemaker.model.Model;

/** This class manages the graphical representation of the word list.
  * 
  * @author szeren */
public class WordLabelList implements ActionListener, MouseListener, ComponentListener {

	Model m_model;
	JPanel m_displayPanel;
	
	ArrayList<JLabel> m_data;
	JLabel m_selectedLabel;

	private JPopupMenu m_popupMenu;
	private final String WORD_LABEL_NAME = "WORD_LABEL_NAME";
	private final String DELETE_LABEL = "DELETE_LABEL";
	
	// For managing the WordListPanel's layout
	private final int ROW_HEIGHT = 16;
	private final int ROW_GAP = 3;
	private final int COLUMN_GAP = 10;
	
	/** @param displayPanel The panel that the words' labels should be placed in. */
	public WordLabelList(Model model, JPanel displayPanel) {
		m_model = model;
		
		m_data = new ArrayList<JLabel>();
		m_displayPanel = displayPanel;
				
		m_popupMenu = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Delete");
		menuItem.setActionCommand(DELETE_LABEL);
		menuItem.addActionListener(this);
		m_popupMenu.add(menuItem);
	}
	
	/** Adds the word to the list <b>if</b> the word's length
	  * is non-zero after being filtered to only letters.<br>
	  * Capitalizes the word and adds it to the display panel.
	  * @param word The word to be added.
	  * @return <b>true</b> if the add was successful. */
	public boolean addWord(String word) {
		word = Constants.filterWord(word);
		
		if (word != null) {
			JLabel newLabel = new JLabel(word);
			newLabel.addMouseListener(this);
			newLabel.setName(WORD_LABEL_NAME);
			if (m_data.add(newLabel)) {
				m_displayPanel.add(newLabel);
				doLayout();
				return true;
			} 
		}
		
		return false;
	}
	
	/** @return If a selected word was de-selected. */
	public boolean deselectWord() {
		if (m_selectedLabel == null) {
			return false;
		}
		
		setStyle(m_selectedLabel, Font.PLAIN);
		m_selectedLabel = null;
		return true;
	}
	
	/** Whether or not the delete was successful. 
	 * @return <b>false</b> if no word was selected or the label was not found. */
	public boolean deleteSelectedWord() {
		if (m_selectedLabel == null) {
			System.err.println("WordList.deleteSelectedWord(): no word selected. Delete failed");
			return false;
		}
		
		m_selectedLabel.removeMouseListener(this);
		if (!m_data.remove(m_selectedLabel)) {
			System.err.println("WordList.deleteSelectedWord(): selected word (" + m_selectedLabel.getText() + ") not found in m_data. Delete failed");
			return false;
		}
		
		m_displayPanel.remove(m_selectedLabel);
		if (!m_model.removeWord(m_selectedLabel.getText())) {
			System.err.println("Failed to remove word from model's list.");
		}
		m_selectedLabel = null;
		doLayout();
		return true;
	}
	

	
	private void setStyle(JLabel label, int fontStyle) {
		label.setFont(label.getFont().deriveFont(fontStyle));
		label.setSize(minWidth(label), label.getHeight());
	}
	
	/** Sets the locations and sizes of all of the word list's labels using a SpringLayout. */
	private void doLayout() {
		if (m_displayPanel.getComponentCount() > 0) {
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
					int labelWidth = Math.min(maxColumnWidth, minWidth((JLabel) currentComponent));
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
		}
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
	
	private int minWidth(JLabel label) {
		return label.getFontMetrics(label.getFont()).stringWidth(label.getText());
	}
	
	private void updateSelection(Component cmp) {
		deselectWord();
		
		if (cmp.getName().equals(WORD_LABEL_NAME)) {
			m_selectedLabel = (JLabel) cmp;
			setStyle(m_selectedLabel, Font.BOLD);
			doLayout(); // Since the label's width will change.
		}
	}
	
	/************************************************************
	  					LISTENER FUNCTIONS
	 ************************************************************/
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if (command.equals(DELETE_LABEL)) {
			deleteSelectedWord();
		}
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
				
		// Without this check, exceptions will be thrown during initial loading.
		if (m_displayPanel.getComponentCount() > 0) {
			doLayout();
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		updateSelection(e.getComponent());
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getComponent().getName().equals(WORD_LABEL_NAME)) {
			updateSelection(e.getComponent());
			
			// If it was a right-click, also show the popup menu.
			if (e.getButton() == MouseEvent.BUTTON3) {
				/* TODO: This might need testing on a multi-monitor device;
				 * getX and getY might not work properly. They have alternatives,
				 * getXOnScreen and getYOnScreen, but I haven't tried them. -SBW */
				m_popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
	
	/************************************************************
	  				UNUSED INHERITED FUNCTIONS
	 ************************************************************/
	
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

	public void changeTo(ArrayList<String> wordList) {
		while (!m_data.isEmpty()) {
			m_data.get(0).removeMouseListener(this);
			m_displayPanel.remove(m_data.remove(0));
		}
		
		for (String s : wordList) {
			addWord(s); // TODO: reallly inefficient that this calls doLayout() each time
		}
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run () {
				m_displayPanel.getParent().repaint();
			}
		});
	}
	
}
