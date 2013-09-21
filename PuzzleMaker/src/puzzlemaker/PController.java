package puzzlemaker;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import puzzlemaker.puzzles.Puzzle;
import puzzlemaker.puzzles.WordSearchPuzzle;
import puzzlemaker.tools.PModel;
import puzzlemaker.tools.WordList;

public class PController implements ActionListener, KeyListener, MouseListener {
	
	/************************************************************
	 * CLASS VARIABLES.
	 * 
	 * Organized by "has-a" relationships (JPanel listed first, followed by what it contains).
	 ************************************************************/
	
	private JMenuBar m_menuBar;
	
	/** Contains and displays the {@link #m_puzzle puzzle}. */
	private JPanel m_puzzlePanel;
	private Puzzle m_puzzle;
	
	/** Contains and displays the {@link #m_wordListPanel word list} and the {@link #m_wordEntryField entry field}. */
	private JPanel m_wordsPanel;
	/** Contains one {@link JLabel} for each currently entered word. */
	private JPanel m_wordListPanel;
	/** @see WordList */
	private WordList m_wordList;
	private JTextField m_wordEntryField;
	
	
   /** Belongs to {@code m_wordsPanel}.<br>
	 * Contains one button for each type of puzzle (e.g.: word search, crossword, etc.). */
	private JPanel m_puzzleButtonPanel;
	private final String WORD_SEARCH_BUTTON = "WORD_SEARCH_BUTTON";
	private final String CROSSWORD_BUTTON = "CROSSWORD_BUTTON";
	
	/************************************************************
	                      CLASS FUNCTIONS
	 ************************************************************/
	
	public PController() {
		m_menuBar = PModel.initMenuBar(this);
		m_menuBar.addMouseListener(this);
		m_puzzlePanel = PModel.createPanel(200, 200, 500, 500, 500, 500, this);
		initWordPanel();
		initPuzzleButtonPanel();
		
		m_wordList = new WordList(m_wordListPanel);
	}
	
	/************************************************************
	                      INIT FUNCTIONS
	 ************************************************************/
	
	private void initWordPanel() {
		// The top-level container
		m_wordsPanel = new JPanel();
		m_wordsPanel.addMouseListener(this);
		m_wordsPanel.setLayout(new BoxLayout(m_wordsPanel, BoxLayout.Y_AXIS));
		
		// Displays the currently entered words
		m_wordListPanel = PModel.createPanel(200, 200, 200, 500, 200, 500, this);
		m_wordListPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));		
		m_wordListPanel.setLayout(new SpringLayout());		
		m_wordsPanel.add(m_wordListPanel);
		
		// The text field where the user types words
		m_wordEntryField = new JTextField(12);
		m_wordEntryField.addKeyListener(this);
		m_wordEntryField.addMouseListener(this);
		PModel.setComponentSizes(m_wordEntryField, 140, 20, 200, 20, 200, 24);
		m_wordsPanel.add(m_wordEntryField);		
	}
	
	private void initPuzzleButtonPanel() {
		final int MAX_BUTTON_SIZE = 82;
		
		m_puzzleButtonPanel = new JPanel();
		m_puzzleButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		m_puzzleButtonPanel.setLayout(new BoxLayout(m_puzzleButtonPanel, BoxLayout.Y_AXIS));
		m_puzzleButtonPanel.setPreferredSize(new Dimension(200, 200));
		m_puzzleButtonPanel.setMaximumSize(new Dimension(200, 200));
		m_puzzleButtonPanel.addMouseListener(this);
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new GridLayout(1, 2, 10, 10));
		
		JButton btnWordSearch = new JButton(new ImageIcon("res/wordsearch.png"));
		btnWordSearch.addMouseListener(this);
		btnWordSearch.setName(WORD_SEARCH_BUTTON);
		innerPanel.add(btnWordSearch);
		
		JButton btnCrossWord = new JButton(new ImageIcon("res/crossword.png"));
		btnCrossWord.addMouseListener(this);
		btnCrossWord.setName(CROSSWORD_BUTTON);
		innerPanel.add(btnCrossWord);
		// Minimum and preferred sizes don't need to be set since 
		// m_horizontalSplit.getBottomComponent()'s minimum size is set.
		innerPanel.setMaximumSize(new Dimension((MAX_BUTTON_SIZE * 2) + 10, MAX_BUTTON_SIZE));

		m_puzzleButtonPanel.add(Box.createVerticalGlue());
		m_puzzleButtonPanel.add(innerPanel);
		m_puzzleButtonPanel.add(Box.createVerticalGlue());
	}
	
	/************************************************************
              seems weird calling this from anywhere else
	 ************************************************************/
	
	/** Resets {@code m_puzzlePanel}'s contents, border, and layout. */
	private void updatePuzzlePanel() {
		m_puzzlePanel.removeAll();
		m_puzzlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		m_puzzlePanel.setLayout(new BoxLayout(m_puzzlePanel, BoxLayout.Y_AXIS));
		
		m_puzzlePanel.add(Box.createVerticalGlue());
		m_puzzlePanel.add(m_puzzle.getDisplayComponent());
		m_puzzlePanel.add(Box.createVerticalGlue());
		m_puzzlePanel.validate();
	}

	/************************************************************
                        GETTER FUNCTIONS
	 ************************************************************/
	
	public JMenuBar getMenuBar() {
		return m_menuBar;
	}
	
	public JPanel getPuzzlePanel() {
		return m_puzzlePanel;
	}
	
	public JPanel getWordListPanel() {
		return m_wordsPanel;
	}
	
	public JPanel getPuzzleButtonPanel() {
		return m_puzzleButtonPanel;
	}
	
	public WordList getWordList() {
		return m_wordList;
	}
	
	/************************************************************
    					  LISTENER FUNCTIONS
	 ************************************************************/
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		System.err.println(command);
		if (command.equals("WORDLABEL_DELETE")) {
			// PModel.deleteSelectedWordLabel(m_wordListPanel.getComponents());
			System.err.println("do_delete");
			
			if (!m_wordList.deleteSelectedWord()) {
				System.err.println("Failed to delete word");
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (m_wordList.addWord(m_wordEntryField.getText())) {
				m_wordEntryField.setText("");
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (!m_wordList.deselectWord()) {
//			System.err.println("No word currently selected.");
		}
		
		String componentName = e.getComponent().getName();
		
		if (componentName == null) {
			return;
		}
		
		switch (componentName) {
			
			case WORD_SEARCH_BUTTON:
				
				if (m_wordList.getSize() > 0) {
					m_puzzle = new WordSearchPuzzle(m_wordList.getWords());
					updatePuzzlePanel();
				}
				
				break;
			
			case CROSSWORD_BUTTON:				
				// m_puzzle = new CrossWordPuzzle(this.getWordList());
				
				if (m_puzzle != null) {
					System.err.println(m_puzzle.toString());
				}
				break;
			
			default:
				System.err.println("Unhandled mouse click: " + e.getComponent().getClass().getName());
				break;
		}
	}
	
	/************************************************************
    				  UNUSED INHERITED FUNCTIONS
	 ************************************************************/
	
	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
}
