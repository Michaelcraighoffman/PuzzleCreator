package puzzlemaker;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import puzzlemaker.gui.PView;

public class PController implements KeyListener {

	private PView m_view;
	
	/**
	 * Top-level words container. Includes word list and word entry field.
	 */
	private JPanel m_wordsPanel;
	
	private ArrayList<JLabel> m_displayedWords;
	private JPanel m_displayedWordsPanel;
	private JTextField m_wordEntryField;
	private JMenuBar menuBar;
	
	public PController() {
		initMenuBar();
		initPuzzlePanel();
		initWordPanel();
		initPuzzleButtonPanel();
		
	}

	private void initMenuBar() {
		JMenu menu;
		JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;
		
		menuBar = new JMenuBar();
		
		// creates top level menu items
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription("This contains basic functions for the project");
		menuBar.add(menu);

		// creates the sub menus		
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
		
		
		
		// creates top level menu items
		menu = new JMenu("Puzzle");
		menu.setMnemonic(KeyEvent.VK_Z);
		menu.getAccessibleContext().setAccessibleDescription("This contains functions to alter the current puzzle");
		menuBar.add(menu);

		// creates the sub menus		
		menuItem = new JMenuItem("Randomize", KeyEvent.VK_R);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Reorder the current puzzle");
		menu.add(menuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("Show Solution");
		rbMenuItem.setSelected(false);
		rbMenuItem.setMnemonic(KeyEvent.VK_K);
		rbMenuItem.getAccessibleContext().setAccessibleDescription("Show or hide the puzzle key");
		menu.add(rbMenuItem);

		// creates top level menu items
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menu.getAccessibleContext().setAccessibleDescription("Learn about the program");
		menuBar.add(menu);

		// creates the sub menus		
		menuItem = new JMenuItem("How to Use", KeyEvent.VK_W);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Get help how to use the program");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("About", KeyEvent.VK_A);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Get the current version of the program");
		menu.add(menuItem);		
		
	}
	
	private void initPuzzlePanel() {
		
	}

	private void initWordPanel() {
		m_wordEntryField = new JTextField(12);
		m_wordEntryField.addKeyListener(this);
		m_wordEntryField.setMaximumSize(new Dimension(200, 36));
	
		m_displayedWords = new ArrayList<JLabel>(0);
		m_displayedWordsPanel = new JPanel();
		m_displayedWordsPanel.setLayout(new GridLayout(16, 2));
		
		m_wordsPanel = new JPanel();
		m_wordsPanel.setLayout(new BoxLayout(m_wordsPanel, BoxLayout.Y_AXIS));

		m_wordsPanel.add(m_displayedWordsPanel);
		m_wordsPanel.add(m_wordEntryField);
		
		// Leave in for debugging.
		addWord("fillerword");
//		addWord("doughnut");
//		addWord("coffee");
//		addWord("ignominious");
//		addWord("magnanimity");
		// Leave in for debugging.
	}
	
	private void initPuzzleButtonPanel() {
		
	}
	
	private void addWord(String word) {
		word = filterToLetters(word);
		if (!word.equals("")) {
			JLabel newWord = new JLabel(word);
			m_displayedWords.add(newWord);
			m_displayedWordsPanel.add(newWord);
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
		
		return menuBar;
	}
	
	public JPanel getPuzzlePanel() {
		
		return null;
	}
	
	public JPanel getWordsPanel() {
		return m_wordsPanel;
	}
	
	public JPanel getPuzzleButtonPanel() {
		
		return null;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Leave in for debugging.
//		m_displayedWords.get(0).setText(Integer.toString(e.getID()));
//		m_displayedWords.get(1).setText(Integer.toString(e.getKeyCode()));
//		m_displayedWords.get(2).setText(Integer.toString(e.getKeyLocation()));
//		m_displayedWords.get(3).setText(Integer.toString(e.getExtendedKeyCode()));
		// Leave in for debugging.
		
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			addWord(m_wordEntryField.getText().trim());
			m_wordEntryField.setText("");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
}
