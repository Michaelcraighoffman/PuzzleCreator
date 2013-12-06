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
public class WordLabelList implements ActionListener, MouseListener, ComponentListener, PopupMenuListener {

	Model m_model;
	JPanel m_displayPanel;
	JScrollPane m_scrollPane;
	
	ArrayList<JLabel> m_data;
	JLabel m_selectedLabel;

	private JPopupMenu m_popupMenu;
	private JPopupMenu m_popupMenu2;
	private final String WORD_LABEL_NAME = "WORD_LABEL_NAME";
	private final String CLUE_LABEL_NAME = "CLUE_LABEL_NAME";
	private final String DELETE_LABEL = "DELETE_LABEL";
	private final String EDIT_CLUE_LABEL = "EDIT_CLUE_LABEL";
	
	// For managing the WordListPanel's layout
	private final int ROW_HEIGHT = 16;
	private final int ROW_GAP = 3;
	private final int COLUMN_GAP = 10;
	
	/** @param displayPanel The panel that the words' labels should be placed in. */
	public WordLabelList(Model model, JPanel displayPanel) {
		m_model = model;
		
		m_data = new ArrayList<JLabel>();
		m_displayPanel = displayPanel;
		m_scrollPane = new JScrollPane();
				
		m_popupMenu = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Delete");
		menuItem.setActionCommand(DELETE_LABEL);
		menuItem.addActionListener(this);		
		m_popupMenu.add(menuItem);	
		m_popupMenu.addPopupMenuListener(this);
		
		m_popupMenu2 = new JPopupMenu();
		JMenuItem menuItemEdit = new JMenuItem("Edit Clue");
		menuItemEdit.setActionCommand(EDIT_CLUE_LABEL);
		menuItemEdit.addActionListener(this);		
		m_popupMenu2.add(menuItemEdit);
		m_popupMenu2.addPopupMenuListener(this);
		

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
			setStyle(newLabel, Font.PLAIN);
			JLabel clueLabel = new JLabel();
			clueLabel.addMouseListener(this);
			clueLabel.setName(CLUE_LABEL_NAME);
			setStyle(clueLabel, Font.PLAIN);
			if (m_data.add(newLabel)) {
				m_data.add(clueLabel);
				m_displayPanel.add(newLabel);
				m_displayPanel.add(clueLabel);
				System.out.println("from add word to do layout");
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
	private boolean deleteSelectedWord() {
		if (m_selectedLabel == null) {
			System.err.println("WordList.deleteSelectedWord(): no word selected. Delete failed");
			return false;
		}
		
		JLabel m_selectedLabelClue = null;
		for (int i = 0; i < m_displayPanel.getComponentCount(); i++) {
			if (m_displayPanel.getComponent(i) == m_selectedLabel) {
				System.out.println("WordLabelList.deleteSelectedWord(): Match found.");
				m_selectedLabelClue = (JLabel) m_displayPanel.getComponent(i+1);
			}
		}
		m_selectedLabel.removeMouseListener(this);
		if (!m_data.remove(m_selectedLabel) && !m_data.remove(m_selectedLabelClue)) {
			System.err.println("WordList.deleteSelectedWord(): selected word (" + m_selectedLabel.getText() + ") not found in m_data. Delete failed");
			return false;
		}
		
		m_displayPanel.remove(m_selectedLabel);
		m_displayPanel.remove(m_selectedLabelClue);
		if (!m_model.removeWord(m_selectedLabel.getText()) && !m_model.removeWord(m_selectedLabelClue.getText())) {
			System.err.println("Failed to remove word from model's list.");
		}
		m_selectedLabel = null;
		doLayout();
		return true;
	}
	
	/** Resets the current word clue by overwriting the current
	  * auto-generated or user-entered clue.<br>
	 */
	// TODO: need to get this to work when right clicking clue
	// consider adding delete word option to get rid of all
	// consider adding edit from the word (it would modify component i+1)
	public boolean editSelectedWordClue() {
	/*	m_selectedLabel.setText("user set text bitches");
		m_selectedLabel.setName("user set text bitches");
		for (int i = 0; i < m_displayPanel.getComponentCount(); i++) {
			if (m_displayPanel.getComponent(i) == m_selectedLabel) {
				System.out.println("WordLabelList.editSelectedWordClue(): Match found.");
				m_data.set(i, m_selectedLabel);
			}
		}*/	
		
		JLabel m_clueword = null;
		for (int i = 0; i < m_displayPanel.getComponentCount(); i++) {
			if (m_displayPanel.getComponent(i) == m_selectedLabel) {
				m_clueword = (JLabel) m_displayPanel.getComponent(i-1);
			}
		}
		String userText = "user set text";
		JLabel userclue = new JLabel();
		userclue.setText(userText);
		if(modifyWordClue(m_clueword, userclue)){		
			doLayout();
			m_selectedLabel = null;
	
		}
		

		return true;
	}
// FIXME: Still not doing what it should be -AEZ
	private boolean modifyWordClue(JLabel word, JLabel clue){
		for (int i=0; i<m_data.size(); i++) {
			if (m_data.get(i).equals(word)) {
				m_data.set(i+1, clue);
				return true;
			}
		}
		return false;		
	}
	
	
	private void setStyle(JLabel label, int fontStyle) {
		label.setFont(label.getFont().deriveFont(fontStyle));
		label.setSize(minWidth(label), label.getHeight());
	}
	
	/** Sets the locations and sizes of all of the word list's labels using a SpringLayout. 
	 * @author Samuel Wiley*/
	private void doLayout() {
		boolean useNewLayout = true;
		System.out.println("Doing layout.");
		
		if (useNewLayout) {
			
			int availableWidth = m_displayPanel.getSize().width - (m_displayPanel.getInsets().left + m_displayPanel.getInsets().right);
			int maxColumnWidth = (availableWidth - COLUMN_GAP) / 2;

			
			ArrayList<WordCluePair> modelData = m_model.getWordCluePairList();
			SpringLayout layout = (SpringLayout) m_displayPanel.getLayout();
			Component currentComponent;
			int biggestWidthInColumn = 0;
			
			System.out.println("modelData.size() = " + modelData.size());
			for (int i = 0; i < modelData.size(); i++) {
				//TODO: Need a different way to realign words in the list, this causes out of bound error.
				currentComponent = m_displayPanel.getComponent(i*2); // this should grab all word labels
//				System.out.println("Laying out " + ((JLabel)currentComponent).getText());
				SpringLayout.Constraints constraints = layout.getConstraints(currentComponent);
				constraints.setX(Spring.constant(0));
				constraints.setY(Spring.constant((i) * (ROW_HEIGHT + ROW_GAP)));
				System.out.println(((JLabel)currentComponent).getText() + "'s height: " + (i * (ROW_HEIGHT + ROW_GAP)));
				int labelWidth = Math.min(maxColumnWidth, minWidth(((JLabel)currentComponent)));
				constraints.setWidth(Spring.constant(labelWidth));
				constraints.setHeight(Spring.constant(ROW_HEIGHT));
				
				biggestWidthInColumn = Math.max(labelWidth, biggestWidthInColumn);
			}
			
			System.out.println("Biggest width in column = " + biggestWidthInColumn);
			int remainingWidth = availableWidth - (biggestWidthInColumn + COLUMN_GAP);
			
			for (int i = 0; i < modelData.size(); i++) {
				currentComponent = m_displayPanel.getComponent((i * 2) + 1);
//				System.out.println("Laying out " + ((JLabel)currentComponent).getText());
				((JLabel)currentComponent).setText(modelData.get(i).getClue());
				SpringLayout.Constraints constraints = layout.getConstraints(currentComponent);
				constraints.setX(Spring.constant(biggestWidthInColumn + COLUMN_GAP));
				constraints.setY(Spring.constant(i * (ROW_HEIGHT + ROW_GAP)));
				System.out.println(((JLabel)currentComponent).getText() + "'s height: " + (i * (ROW_HEIGHT + ROW_GAP)));
				constraints.setWidth(Spring.constant(Math.min(remainingWidth, minWidth((JLabel)currentComponent))));
				constraints.setHeight(Spring.constant(ROW_HEIGHT));
			}
			
			SpringLayout.Constraints panelConstraints = layout.getConstraints(m_displayPanel);
			panelConstraints.setConstraint(SpringLayout.EAST, Spring.constant(availableWidth));
			
			// TODO: Not sure about this line.. whether or not this spring layout needs to constrain it's vertical size since we have a scroll bar...
			panelConstraints.setConstraint(SpringLayout.SOUTH, Spring.constant(((modelData.size() / 2) * (ROW_HEIGHT + ROW_GAP)) - ROW_GAP));
		}
		else {
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
		if (cmp.getName().equals(CLUE_LABEL_NAME)) {
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
		if (command.equals(EDIT_CLUE_LABEL)) {
			editSelectedWordClue();
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
			if(e.getButton()==MouseEvent.BUTTON1) {
				JLabel lbl=(JLabel)e.getComponent();
				m_model.setSelected(lbl.getText());
				m_model.getView().updatePuzzlePanel();
			}
			// If it was a right-click, also show the popup menu.
			if (e.getButton() == MouseEvent.BUTTON3) {
				/* TODO: This might need testing on a multi-monitor device;
				 * getX and getY might not work properly. They have alternatives,
				 * getXOnScreen and getYOnScreen, but I haven't tried them. -SBW */
				m_popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		if(e.getComponent().getName().equals(CLUE_LABEL_NAME)) {
			updateSelection(e.getComponent());
			
			//highlights the word associated with the clue
			if(e.getButton()==MouseEvent.BUTTON1) {
				JLabel lbl=(JLabel)e.getComponent();
				JLabel m_clueword = null;
				for (int i = 0; i < m_displayPanel.getComponentCount(); i++) {
					if (m_displayPanel.getComponent(i) == lbl) {
						m_clueword = (JLabel) m_displayPanel.getComponent(i-1);
					}
				}
				m_model.setSelected(m_clueword.getText());
				m_model.getView().updatePuzzlePanel();
			}
			if (e.getButton() == MouseEvent.BUTTON3) {
				/* TODO: This might need testing on a multi-monitor device;
				 * getX and getY might not work properly. They have alternatives,
				 * getXOnScreen and getYOnScreen, but I haven't tried them. -SBW */
				m_popupMenu2.show(e.getComponent(), e.getX(), e.getY());
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

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
		deselectWord();
	}
}
