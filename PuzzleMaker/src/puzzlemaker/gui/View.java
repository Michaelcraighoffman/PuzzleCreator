package puzzlemaker.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import puzzlemaker.Constants;
import puzzlemaker.puzzles.Crossword;
import puzzlemaker.puzzles.Puzzle;
import puzzlemaker.puzzles.WordSearch;
import puzzlemaker.tools.Model;
import puzzlemaker.tools.WordList;

public class View extends JFrame implements ActionListener, KeyListener, MouseListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3249856252715867854L;
	
	/************************************************************
	 * CLASS VARIABLES.
	 * 
	 * Organized by "has-a" relationships (JPanel listed first, followed by what it contains).
	 ************************************************************/
	
	private JSplitPane m_verticalSplit;
	private JSplitPane m_horizontalSplit;
	
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
	
	/** Initializes the main {@link javax.swing.JFrame window} and the GUI components therein. */
	public View() {
		m_menuBar = Model.initMenuBar(this, this);
		m_menuBar.addMouseListener(this);
		m_puzzlePanel = Model.createPanel(200, 200, 500, 500, 500, 500, this);
//		m_wordListPanel = Model.createPanel(200, 200, 200, 200, 200, 200, this);
		initWordPanel();
		m_wordList = new WordList(m_wordListPanel);
		initPuzzleButtonPanel();		
		
		// Initialize the window.
		this.setTitle("Crossword");
		this.setSize(new Dimension(640, 640));
		this.setMinimumSize(new Dimension(480, 480));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // TODO: Might not need to keep track of m_menuBar ?
        // Initialize the menu bar.
        
        this.setJMenuBar(m_menuBar);
        
        // Initialize the split pane.   
        m_horizontalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, m_wordsPanel, m_puzzleButtonPanel);
        m_horizontalSplit.setResizeWeight(0.5f);
        m_horizontalSplit.getBottomComponent ().setMinimumSize (new Dimension(200, 100)); // Prevents button icons from needing resizing.
        
        m_verticalSplit=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, m_puzzlePanel, m_horizontalSplit);
        m_verticalSplit.setResizeWeight(0.8f);
        
        // Send notifications to WordList on resize so WordList can update its layout.
        this.addComponentListener(m_wordList);
        m_horizontalSplit.getTopComponent().addComponentListener(m_wordList);
        

        // Add the split pane to the window and show the window.
        this.getContentPane().add(m_verticalSplit);
        this.pack();		
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
		m_wordListPanel = Model.createPanel(200, 200, 200, 500, 200, 500, this);
		m_wordListPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));		
		m_wordListPanel.setLayout(new SpringLayout());		
		m_wordsPanel.add(m_wordListPanel);
		
		// The text field where the user types words
		m_wordEntryField = new JTextField(12);
		m_wordEntryField.addKeyListener(this);
		m_wordEntryField.addMouseListener(this);
		Model.setComponentSizes(m_wordEntryField, 140, 20, 200, 20, 200, 24);
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
	
	private void saveFile(ArrayList<String> words)
	{
		JFileChooser dlgSave;
		dlgSave = new JFileChooser ();
		File file;
		String path;
		int value = dlgSave.showSaveDialog(m_wordsPanel);
		if (value == JFileChooser.APPROVE_OPTION){ 
             path = dlgSave.getSelectedFile().getAbsolutePath();
           file = new File(path+".txt"); 
            
		//String path = "C:/Users/Kaien/git/pinky-brains-crossword/PuzzleMaker/res/words.txt";
		 
		if(!file.exists())
		{
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		
		// Set default file name value as the unused file name we found
		
		
		PrintWriter fileOutput = null;
		try {
			fileOutput = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (fileOutput != null) {
			for(int i = 0; i < words.size(); i++)
				fileOutput.println(words.get(i));
			fileOutput.close();
		}
		
	//	dlgSave.setSelectedFile (new File (path));
		}
	}
	
	private void importFile() {
//		ArrayList<String> words = new ArrayList<String>();
		
		JFileChooser dlgSave;
		dlgSave = new JFileChooser ();
		File file;
		int value = dlgSave.showOpenDialog(m_wordsPanel);
		if (value == JFileChooser.APPROVE_OPTION){ 
            file = dlgSave.getSelectedFile();
		
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = reader.readLine()) != null) {
					m_wordList.addWord(line);//basic no string tokenizer yet
				}
		
			}
			catch(IOException e){
					;
			}
		}
		//return words;
	}

	/************************************************************
                        GETTER FUNCTIONS
	 ************************************************************/
	
//	public JMenuBar getMenuBar() {
//		return m_menuBar;
//	}
	
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
		
		switch (command) {
		case Constants.DELETE_WORD_LABEL:
			if (!m_wordList.deleteSelectedWord()) {
				System.err.println("Failed to delete word label");
			}
			break;
		case Constants.IMPORT:
			importFile();
			break;
		case Constants.SAVE_WORDLIST:
			saveFile(getWordList().getWords());
			break;
		case Constants.EXIT:
			System.exit(0);
			break;
		case Constants.ABOUT:
			Model.showAboutDialog(this, "Iteration 1").setVisible(true);
			break;
			
		default:
			System.err.println("Unrecognized command: " + command);
			break;
	
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
					m_puzzle = new WordSearch(m_wordList.getWords());
					updatePuzzlePanel();
				}
				
				break;
			
			case CROSSWORD_BUTTON:				
				m_puzzle = new Crossword(m_wordList.getWords());
				
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
