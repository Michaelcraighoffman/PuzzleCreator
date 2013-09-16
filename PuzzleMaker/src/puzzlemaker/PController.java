package puzzlemaker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

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

public class PController implements KeyListener, ComponentListener {
	// Puzzle grid.
	/**
	 * Contains {@code m_puzzleGrid}'s {@code JLabel}s.
	 */
	private JPanel m_puzzlePanel;
	private ArrayList<ArrayList<JLabel>> m_puzzleGrid;
	
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
		initPuzzlePanel(8, 8);
		initWordPanel();
		initPuzzleButtonPanel();
	}

	private void initMenuBar() {
		JMenu menu;
		JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;
		
		m_menuBar = new JMenuBar();
		
		// "File" 
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription("This contains basic functions for the project");
		m_menuBar.add(menu);

		// "File" sub-menus
		menuItem = new JMenuItem("Open", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Open a preexisting project");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Save Puzzle", KeyEvent.VK_U);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Save the current puzzle");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Save Word List", KeyEvent.VK_L);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Save the current word list");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Export...", KeyEvent.VK_E);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Export puzzle or word list to...");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Print", KeyEvent.VK_P);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Print current puzzle view");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Print current puzzle view");
		menu.add(menuItem);
		

		// "Puzzle"
		menu = new JMenu("Puzzle");
		menu.setMnemonic(KeyEvent.VK_Z);
		menu.getAccessibleContext().setAccessibleDescription("This contains functions to alter the current puzzle");
		m_menuBar.add(menu);

		// "Puzzle" sub-menus	
		menuItem = new JMenuItem("Randomize", KeyEvent.VK_R);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Reorder the current puzzle");
		menu.add(menuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("Show Solution");
		rbMenuItem.setSelected(false);
		rbMenuItem.setMnemonic(KeyEvent.VK_K);
		rbMenuItem.getAccessibleContext().setAccessibleDescription("Show or hide the puzzle key");
		menu.add(rbMenuItem);

		// "Help"
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menu.getAccessibleContext().setAccessibleDescription("Learn about the program");
		m_menuBar.add(menu);

		// "Help" sub-menus
		menuItem = new JMenuItem("How to Use", KeyEvent.VK_W);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Get help how to use the program");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("About", KeyEvent.VK_A);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Get the current version of the program");
		menu.add(menuItem);			
	}
	
	/*
	 * Author: Alex Shisler
	 * initPuzzelPanel
	 * Displays a 2D Grid of char holding objects using jtextfields(or jlabels) 
	 */
	private void initPuzzlePanel(int width, int height) {
		m_puzzlePanel = new JPanel(new GridLayout(height, width));
		m_puzzlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		m_puzzleGrid = new ArrayList<ArrayList<JLabel>>(width);
		
		ArrayList<JLabel> tmpColumn;
		JLabel tmpLabel;
		
		for (int i = 0; i < width; i++) {
			tmpColumn = new ArrayList<JLabel>(height);
			for (int j = 0; j < height; j++) {
				tmpLabel = new JLabel(Integer.toString(j + i), JLabel.CENTER);
				tmpLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
				tmpColumn.add(tmpLabel);
				
				m_puzzlePanel.add(tmpLabel);
			}
			m_puzzleGrid.add(tmpColumn);
		}
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
		
		btnWordSearch.addComponentListener(this);
		btnCrossWord.addComponentListener(this);
		btnWordSearch.setName("btnWordSearch");
		btnCrossWord.setName("btnCrossWord");
		btnWordSearch.setMinimumSize(MIN_BUTTON_DIMENSION);
		btnWordSearch.setMaximumSize(MAX_BUTTON_DIMENSION);
		btnCrossWord.setMinimumSize(MIN_BUTTON_DIMENSION);
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
	
	private void addWord(String word) {
		word = filterToLetters(word);
		if (!word.equals("")) {
			JLabel newWord = new JLabel(word);
			m_wordListPanel.add(newWord);
			m_wordListPanel.doLayout();
		}
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

	@Override
	public void keyPressed(KeyEvent e) {
		// Add user's typed word to m_wordListPanel.
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			addWord(m_wordEntryField.getText().trim());
			m_wordEntryField.setText("");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// unused
	}



	@Override
	public void keyTyped(KeyEvent e) {
		// unused
	}

	
	@Override
	public void componentResized(ComponentEvent e) {
		if (e.getComponent().getName().equals("innerButtonPanel")) {
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
	}
	

	@Override
	public void componentMoved(ComponentEvent e) {
		if (e.getComponent().getName().equals("innerButtonPanel")) {
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
	}

	
	@Override
	public void componentShown(ComponentEvent e) {
		
	}
	

	@Override
	public void componentHidden(ComponentEvent e) {
		
	}
}
