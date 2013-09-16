package puzzlemaker;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import puzzlemaker.puzzles.Puzzle;
import puzzlemaker.puzzles.WordSearchPuzzle;

public class PController implements KeyListener, ComponentListener, MouseListener {
	private Puzzle m_puzzle;
	
	// Puzzle grid.
	/**
	 * Contains {@code m_puzzleGrid}'s {@code JLabel}s.
	 */
	private JPanel m_puzzlePanel;
	
	// Word list.
	/**
	 * Top-level words container. Includes {@code m_wordListPanel}, {@code m_wordEntryField}, and {@code m_puzzleButtonPanel}.
	 */
	private JPanel m_wordsPanel;
	/**
	 * Contains one {@link JLabel} for each currently entered word.
	 */
	private JPanel m_wordListPanel;
	private JTextField m_wordEntryField;
	/**
	 * Belongs to {@code m_wordsPanel}.<br>
	 * Contains one button for each type of puzzle (e.g.: word search, crossword, etc.).
	 */
	private JPanel m_puzzleButtonPanel;
	private ImageIcon m_wordSearchIcon;
	private ImageIcon m_crossWordIcon;
	
	// Menu bar.
	private JMenuBar m_menuBar;
	
	public PController() {
		initMenuBar();
		initPuzzlePanel();
		initWordPanel();
		initPuzzleButtonPanel();
	}

	// Init methods.
	
	private void initMenuBar() {
		JMenu menu;
		JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;
		
		m_menuBar = new JMenuBar();
		
		// "File" 
		menu = createTopLevelMenu("File", KeyEvent.VK_F, "This contains basic functions for the project");
		m_menuBar.add(menu);

		// "File" sub-menus
		menuItem = createSubMenuItem("Open", KeyEvent.VK_O, "Open a preexisting project", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		menuItem = createSubMenuItem("Save Puzzle", KeyEvent.VK_U, "Save the current puzzle", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		menuItem = createSubMenuItem("Save Word List", KeyEvent.VK_L, "Save the current word list", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		menu.add(menuItem);
		menuItem = createSubMenuItem("Export...", KeyEvent.VK_E, "Export puzzle or word list to...", KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		menuItem = createSubMenuItem("Print", KeyEvent.VK_P, "Print current puzzle view", KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		menuItem = createSubMenuItem("Exit", KeyEvent.VK_X, "Print current puzzle view", KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		menu.add(menuItem);

		// "Puzzle"
		menu = createTopLevelMenu("Puzzle", KeyEvent.VK_Z, "This contains functions to alter the current puzzle");
		m_menuBar.add(menu);

		// "Puzzle" sub-menus	
		menuItem = createSubMenuItem("Randomize", KeyEvent.VK_R, "Reorder the current puzzle", KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("Show Solution");
		rbMenuItem.setSelected(false);
		rbMenuItem.setMnemonic(KeyEvent.VK_K);
		rbMenuItem.getAccessibleContext().setAccessibleDescription("Show or hide the puzzle key");
		menu.add(rbMenuItem);

		// "Help"
		menu = createTopLevelMenu("Help", KeyEvent.VK_H, "Learn about the program");
		m_menuBar.add(menu);

		// "Help" sub-menus
		menuItem = createSubMenuItem("How to Use", KeyEvent.VK_W, "Get help how to use the program", KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		
		menuItem = createSubMenuItem("About", KeyEvent.VK_A, "Get the current version of the program", KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		menu.add(menuItem);			
	}
	
	/*
	 * Author: Alex Shisler
	 * initPuzzelPanel
	 * Displays a 2D Grid of char holding objects using jtextfields(or jlabels) 
	 */
	private void initPuzzlePanel() {
		m_puzzlePanel = new JPanel();
		m_puzzlePanel.setMinimumSize(new Dimension(200, 200));
		m_puzzlePanel.setPreferredSize(new Dimension(500, 500));
		m_puzzlePanel.setMaximumSize(new Dimension(500, 500));
	}
	
	private void initWordPanel() {
		m_wordEntryField = new JTextField(12);
		m_wordEntryField.addKeyListener(this);
		m_wordEntryField.setMinimumSize(new Dimension(140, 20));
		m_wordEntryField.setPreferredSize(new Dimension(200, 20));
		m_wordEntryField.setMaximumSize(new Dimension(200, 24));
	
		m_wordListPanel = new JPanel();
		m_wordListPanel.setBorder(BorderFactory.createEmptyBorder(5,  5,  5, 5));
		m_wordListPanel.setLayout(new GridLayout(16, 2));
		m_wordListPanel.setMinimumSize(new Dimension(200, 200));
		m_wordListPanel.setPreferredSize(new Dimension(200, 500));
		
		m_wordsPanel = new JPanel();
		m_wordsPanel.setLayout(new BoxLayout(m_wordsPanel, BoxLayout.Y_AXIS));

		m_wordsPanel.add(m_wordListPanel);
		m_wordsPanel.add(m_wordEntryField);
	}
	
	private void initPuzzleButtonPanel() {
		final int MIN_BUTTON_SIZE = 56;
		final int MAX_BUTTON_SIZE = 82;
		Dimension MIN_BUTTON_DIMENSION = new Dimension(MIN_BUTTON_SIZE, MIN_BUTTON_SIZE);
		Dimension MAX_BUTTON_DIMENSION = new Dimension(MAX_BUTTON_SIZE, MAX_BUTTON_SIZE);
		
		m_wordSearchIcon = new ImageIcon("res/wordsearch.png", "Word Search");
		m_crossWordIcon = new ImageIcon("res/crossword.png", "Crossword");
		
		JButton btnWordSearch = new JButton(m_wordSearchIcon);
		JButton btnCrossWord = new JButton(m_crossWordIcon);
		
//		btnWordSearch.addComponentListener(this);
//		btnCrossWord.addComponentListener(this);
		
		btnWordSearch.addMouseListener(this);
		btnCrossWord.addMouseListener(this);
		
		btnWordSearch.setName("btnWordSearch");
		btnCrossWord.setName("btnCrossWord");
		
		btnWordSearch.setMinimumSize(MIN_BUTTON_DIMENSION);
		btnCrossWord.setMinimumSize(MIN_BUTTON_DIMENSION);

		btnWordSearch.setMaximumSize(MAX_BUTTON_DIMENSION);
		btnCrossWord.setMaximumSize(MAX_BUTTON_DIMENSION);
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new GridLayout (1, 2, 10, 10));
		innerPanel.add(btnWordSearch);
		innerPanel.add(btnCrossWord);
		innerPanel.setMaximumSize(new Dimension((MAX_BUTTON_SIZE * 2) + 10, MAX_BUTTON_SIZE));
		innerPanel.setPreferredSize(new Dimension((MAX_BUTTON_SIZE * 2) + 10, MAX_BUTTON_SIZE));
		innerPanel.setName("innerButtonPanel");
		innerPanel.addComponentListener(this);
		
		m_puzzleButtonPanel = new JPanel();
		m_puzzleButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		m_puzzleButtonPanel.setLayout(new BoxLayout(m_puzzleButtonPanel, BoxLayout.Y_AXIS));
		m_puzzleButtonPanel.setPreferredSize(new Dimension(200, 200));
		m_puzzleButtonPanel.setMaximumSize(new Dimension(200, 200));
		
		m_puzzleButtonPanel.add(Box.createVerticalGlue());
		m_puzzleButtonPanel.add(innerPanel);
		m_puzzleButtonPanel.add(Box.createVerticalGlue());
	}
	
	// Functional methods.
	
	private void updatePuzzlePanel() {
		m_puzzlePanel.removeAll();
		m_puzzlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		m_puzzlePanel.setLayout(new BoxLayout(m_puzzlePanel, BoxLayout.Y_AXIS));
		
		m_puzzlePanel.add(Box.createVerticalGlue());
		m_puzzlePanel.add(m_puzzle.getDisplayComponent());
		m_puzzlePanel.add(Box.createVerticalGlue());
		m_puzzlePanel.validate();
	}
	
	private void addWord(String word) {
		word = filterToLetters(word);
		if (!word.equals("")) {
			JLabel newWord = new JLabel(word);
			m_wordListPanel.add(newWord);
			m_wordListPanel.validate();
		}
	}
	
	private void resizeButtonIcons(ComponentEvent e) {
		JPanel sender = (JPanel) e.getComponent();
		for (Component c : sender.getComponents()) {
			JButton button = (JButton) c;
			if (button.getName().equals("btnCrossWord")) {
				button.setIcon(new ImageIcon(m_crossWordIcon.getImage().getScaledInstance(button.getWidth() - 10, button.getHeight() - 8, Image.SCALE_SMOOTH)));
			}
			else if (button.getName().equals("btnWordSearch")) {
				button.setIcon(new ImageIcon(m_wordSearchIcon.getImage().getScaledInstance(button.getWidth() - 10, button.getHeight() - 8, Image.SCALE_SMOOTH)));
			}
		}
	}
	// "Get" methods.
	
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

	public String[] getWordList() {
		Component[] labelList = m_wordListPanel.getComponents();
		String[] wordList = new String[labelList.length];
		
		for (int i = 0; i < wordList.length; i++) {
			wordList[i] = ((JLabel) labelList[i]).getText();
		}
		
		return wordList;
	}
	
	// Helper methods.
	
	private JMenu createTopLevelMenu(String label, int mnemonic, String description) {
		JMenu menu = new JMenu(label);
		menu.setMnemonic(mnemonic);
		menu.getAccessibleContext().setAccessibleDescription(description);
		return menu;
	}
	
	private JMenuItem createSubMenuItem(String label, int mnemonic, String description, KeyStroke shortcut) {
		JMenuItem menuItem = new JMenuItem(label, mnemonic);
		menuItem.setAccelerator(shortcut);
		menuItem.getAccessibleContext().setAccessibleDescription(description);
		return menuItem;
	}
	
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
	
 	// Inherited methods.
 	
	@Override
	public void keyPressed(KeyEvent e) {
		// Add user's typed word to m_wordListPanel.
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			addWord(m_wordEntryField.getText().trim().toUpperCase());
			m_wordEntryField.setText("");
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getComponent().getName().equals("btnWordSearch")) {
			System.err.println("Make a word search puzzle!");
			m_puzzle = new WordSearchPuzzle(this.getWordList());
			updatePuzzlePanel();
		}
		else if (e.getComponent().getName().equals("btnCrossWord")) {
			System.err.println("Make a crossword puzzle!");
			System.err.println(m_puzzle.toString());
		}
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		// If statements could be useful if other components add this class as a listener.
//		if (e.getComponent() instanceof JPanel) {
//			if (e.getComponent().getName().equals("innerButtonPanel")) {
				resizeButtonIcons(e);
//			}
//		}
	}
	
	@Override
	public void componentMoved(ComponentEvent e) {
		// If statements could be useful if other components add this class as a listener.
//		if (e.getComponent() instanceof JPanel) {
//			if (e.getComponent().getName().equals("innerButtonPanel")) {
				resizeButtonIcons(e);
//			}
//		}
	}

	// Unused inherited methods.
	
	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}
	
	@Override
	public void componentHidden(ComponentEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
