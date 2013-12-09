package puzzlemaker.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import puzzlemaker.Constants;
import puzzlemaker.model.Model;
import puzzlemaker.tools.WordCluePair;

/** This class manages the graphical representation of the word list.
  * 
  * @author Samuel Wiley */
public class WordLabelList implements ActionListener, MouseListener, PopupMenuListener {

	Model m_model;
	View m_view;
	JPanel m_displayPanel;
	JScrollPane m_scrollPane;
	
	ArrayList<JLabel> m_data;
	JLabel m_selectedWordLabel;
	JLabel m_selectedClueLabel;

	private JPopupMenu m_popupMenu;
	private final String WORD_LABEL = "WORD_LABEL_NAME";
	private final String CLUE_LABEL = "CLUE_LABEL_NAME";
	private final String DELETE_LABEL = "DELETE_LABEL";
	private final String EDIT_CLUE = "EDIT_CLUE_LABEL";
	
	// For managing the WordListPanel's layout
	private final int ROW_HEIGHT = 16;
	private final int ROW_GAP = 3;
	private final int COLUMN_GAP = 20;
	
	/** @param displayPanel The panel that the words' labels should be placed in. */
	public WordLabelList(Model model, View view, JPanel displayPanel) {
		m_model = model;
		m_view = view;
		
		m_data = new ArrayList<JLabel>();
		m_displayPanel = displayPanel;
		m_scrollPane = new JScrollPane();
				
		m_popupMenu = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Delete");
		menuItem.setActionCommand(DELETE_LABEL);
		menuItem.addActionListener(this);		
		m_popupMenu.add(menuItem);	
		menuItem = new JMenuItem("Edit Clue");
		menuItem.setActionCommand(EDIT_CLUE);
		menuItem.addActionListener(this);
		m_popupMenu.add(menuItem);
		m_popupMenu.addPopupMenuListener(this);
	}
	
	/************************************************************
						LABEL MANIPULATION
	 ************************************************************/
	
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
			newLabel.setName(WORD_LABEL);
			setStyle(newLabel, Font.PLAIN);
			JLabel clueLabel = new JLabel();
			clueLabel.addMouseListener(this);
			clueLabel.setName(CLUE_LABEL);
			setStyle(clueLabel, Font.PLAIN);
			if (m_data.add(newLabel)) {
				m_data.add(clueLabel);
				m_displayPanel.add(newLabel);
				m_displayPanel.add(clueLabel);
				doLayout();
				return true;
			} 
		}
		
		return false;
	}
	
	private void updateSelection(Component cmp) {
		deselectLabels();

		for (int i = 0; i < m_displayPanel.getComponentCount(); i++) {
			if (m_displayPanel.getComponent(i).equals(cmp)) {
				if ((i & 1) == 0) {
					m_selectedWordLabel = (JLabel) m_displayPanel.getComponent(i);
					m_selectedClueLabel = (JLabel) m_displayPanel.getComponent(i + 1);
				}
				else {
					m_selectedWordLabel = (JLabel) m_displayPanel.getComponent(i - 1);
					m_selectedClueLabel = (JLabel) m_displayPanel.getComponent(i);
				}
				setStyle(m_selectedWordLabel, Font.BOLD);
				setStyle(m_selectedClueLabel, Font.BOLD);
				
				if (m_model.getPuzzle() != null) {
					m_model.getPuzzle().selectWord(m_selectedWordLabel.getText());
					m_view.updatePuzzlePanel();
				}

				return;
			}
		}
		System.err.println("WordLabelList.updateSelection(): No matching component found.");
	}
	
	/** For use by the View when the user has clicked on a word in the puzzle. */
 	public void selectWord(String wordSelected) {
		for (int i = 0; i < m_data.size(); i += 2) {
			if (m_data.get(i).getText().equals(wordSelected)) {
				updateSelection(m_data.get(i));
			}
		}
	}
	
	/** Resets the current word clue by overwriting the current
	  * auto-generated or user-entered clue.<br>*/
	public boolean editSelectedWordClue() {
		WordCluePair wcp = m_model.getWordCluePairList().get(m_data.indexOf(m_selectedWordLabel) / 2);
		String newClue = JOptionPane.showInputDialog(m_view, "Edit the clue for \"" + wcp.getWord() + "\":", wcp.getClue());
		if (newClue == null) {
			return false;
		}
		wcp.setClue(newClue);
		deselectLabels();
		doLayout();
		
		return true;
	}
	
	/** Whether or not the delete was successful. 
	 * @return <b>false</b> if no word was selected or the label was not found. */
	private boolean deleteSelected() {
		if (m_selectedWordLabel == null || m_selectedClueLabel == null) {
			return false;
		}

		m_selectedWordLabel.removeMouseListener(this);
		m_selectedClueLabel.removeMouseListener(this);
		
		m_model.getWordCluePairList().remove(m_data.indexOf(m_selectedWordLabel) / 2);
		
		m_data.remove(m_selectedWordLabel);
		m_data.remove(m_selectedClueLabel);

		m_displayPanel.remove(m_selectedWordLabel);
		m_displayPanel.remove(m_selectedClueLabel);
		
		m_selectedWordLabel = null;
		doLayout();
		return true;
	}
	
	/** Deselects the selected labels, if there are any. */
	public void deselectLabels() {
		if (m_selectedWordLabel == null || m_selectedClueLabel == null) {
			return;
		}
		
		setStyle(m_selectedWordLabel, Font.PLAIN);
		setStyle(m_selectedClueLabel, Font.PLAIN);
		m_selectedWordLabel = null;
		m_selectedClueLabel = null;
		
		
		if (m_model.getPuzzle() != null) {
			m_model.getPuzzle().selectWord("");
			m_view.updatePuzzlePanel();
		}
		return;
	}
	
	public void changeToWordList(ArrayList<String> wordList) {
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

	private void setStyle(final JLabel label, int fontStyle) {
		label.setFont(label.getFont().deriveFont(fontStyle));
		label.setSize(minWidth(label), label.getHeight());
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run () {
				label.repaint();
			}
		});
	}
	
	private int minWidth(JLabel label) {
		return label.getFontMetrics(label.getFont()).stringWidth(label.getText());
	}
	
	/** Sets the locations and sizes of all of the word list's labels using a SpringLayout. 
	 * @author Samuel Wiley*/
	private void doLayout() {

		int availableWidth = m_displayPanel.getSize().width - (m_displayPanel.getInsets().left + m_displayPanel.getInsets().right);
		int maxColumnWidth = (availableWidth - COLUMN_GAP) / 2;

		
		ArrayList<WordCluePair> modelData = m_model.getWordCluePairList();
		SpringLayout layout = (SpringLayout) m_displayPanel.getLayout();
		Component currentComponent;
		int biggestWidthInColumn = 0;
		
		// Set up all word labels.
		for (int i = 0; i < modelData.size(); i++) {
			currentComponent = m_displayPanel.getComponent(i * 2); // this should grab all word labels
			SpringLayout.Constraints constraints = layout.getConstraints(currentComponent);
			constraints.setX(Spring.constant(0));
			constraints.setY(Spring.constant(i * (ROW_HEIGHT + ROW_GAP)));
			int labelWidth = Math.min(maxColumnWidth, minWidth(((JLabel)currentComponent)) + COLUMN_GAP);
//			constraints.setWidth(Spring.constant(labelWidth));
			constraints.setHeight(Spring.constant(ROW_HEIGHT));
			
			biggestWidthInColumn = Math.max(labelWidth, biggestWidthInColumn);
		}
		for (int i = 0; i < modelData.size(); i++) {
			currentComponent = m_displayPanel.getComponent(i * 2); // this should grab all word labels
			SpringLayout.Constraints constraints = layout.getConstraints(currentComponent);
			constraints.setWidth(Spring.constant(biggestWidthInColumn));
		}
		
		
		int remainingWidth = availableWidth - (biggestWidthInColumn);
		
		// Set up all clue labels.
		for (int i = 0; i < modelData.size(); i++) {
			currentComponent = m_displayPanel.getComponent((i * 2) + 1);
			((JLabel)currentComponent).setText(modelData.get(i).getClue());
			SpringLayout.Constraints constraints = layout.getConstraints(currentComponent);
			constraints.setX(Spring.constant(biggestWidthInColumn));
			constraints.setY(Spring.constant(i * (ROW_HEIGHT + ROW_GAP)));
			constraints.setWidth(Spring.constant(Math.min(remainingWidth, minWidth((JLabel)currentComponent) + 30)));
			constraints.setHeight(Spring.constant(ROW_HEIGHT));
		}
		
//		SpringLayout.Constraints panelConstraints = layout.getConstraints(m_displayPanel);
//		panelConstraints.setConstraint(SpringLayout.EAST, Spring.constant(availableWidth));
		
		// TODO: Not sure about this line.. whether or not this spring layout needs to constrain it's vertical size since we have a scroll bar...
//		panelConstraints.setConstraint(SpringLayout.SOUTH, Spring.constant(((modelData.size() / 2) * (ROW_HEIGHT + ROW_GAP)) - ROW_GAP));

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
	
	/************************************************************
	  					LISTENER FUNCTIONS
	 ************************************************************/
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if (command.equals(DELETE_LABEL)) {
			deleteSelected();
		}
		if (command.equals(EDIT_CLUE)) {			
			editSelectedWordClue();
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		updateSelection(e.getComponent());
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			m_popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
		deselectLabels();
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
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
}
