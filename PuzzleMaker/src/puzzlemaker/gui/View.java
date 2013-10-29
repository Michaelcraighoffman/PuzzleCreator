package puzzlemaker.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.plaf.basic.BasicArrowButton;

import puzzlemaker.Constants;
import puzzlemaker.model.Model;
import puzzlemaker.puzzles.Crossword;
import puzzlemaker.puzzles.Puzzle;
import puzzlemaker.tools.grid.Grid;

public class View extends JFrame implements ActionListener, KeyListener, MouseListener {
	
	private static final long serialVersionUID = 3249856252715867854L;
	
	/* **********************************************************
	 * CLASS VARIABLES.
	 * 
	 * Organized by "has-a" relationships (JPanel listed first, followed by what it contains).
	 ************************************************************/
	
	private Model m_model;
	
	private JSplitPane m_verticalSplit;
	private JSplitPane m_horizontalSplit;
	
	private JMenuBar m_menuBar;
	private JDialog m_aboutDialog;
	
	/** Contains and displays the {@link #m_puzzle puzzle}. */
	private JPanel m_puzzlePanel;
	/** Arrow Buttons for wordLists */
	private BasicArrowButton nextWord;
	private BasicArrowButton prevWord;
	private BasicArrowButton nextWordPZL;
	private BasicArrowButton prevWordPZL;
	/** Contains and displays the {@link #m_wordListPanel word list} and the {@link #m_wordEntryField entry field}. */
	private JPanel m_wordsPanel;
	/** Contains one {@link JLabel} for each currently entered word. */
	private JPanel m_wordListPanel;
	/** @see WordLabelList */
	private WordLabelList m_wordLabelList;
	private JTextField m_wordEntryField;
	
	
   /** Belongs to {@code m_wordsPanel}.<br>
	 * Contains one button for each type of puzzle (e.g.: word search, crossword, etc.). */
	private JPanel m_puzzleButtonPanel;
	private final String WORD_SEARCH_BUTTON = "WORD_SEARCH_BUTTON";
	private final String CROSSWORD_BUTTON = "CROSSWORD_BUTTON";
	private final String PREVIOUS_BUTTON = "PREVIOUS_BUTTON";
	private final String NEXT_BUTTON = "NEXT_BUTTON";
	private final String PREVIOUS_BUTTON_PZL = "PREVIOUS_BUTTON_PZL";
	private final String NEXT_BUTTON_PZL = "NEXT_BUTTON_PZL";
	/* **********************************************************
	                      CLASS FUNCTIONS
	 ************************************************************/
	
	/** Initializes the main {@link javax.swing.JFrame window} and the GUI components therein. */
	public View(Model model) {
		m_model = model;
		
		initMenuBar();
		initPuzzlePanel();
		initWordPanel();
		initPuzzleButtonPanel();
        
        // Initialize the split pane.
        m_horizontalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        m_horizontalSplit.setTopComponent(m_wordsPanel);
        m_horizontalSplit.setBottomComponent(m_puzzleButtonPanel);
        m_horizontalSplit.setResizeWeight(0.5f);
        
        m_verticalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, m_puzzlePanel, m_horizontalSplit);
        m_verticalSplit.setResizeWeight(0.8f);

		// Initialize the window.
		this.setTitle("Puzzle Maker");
		this.setSize(new Dimension(640, 640));
		this.setMinimumSize(new Dimension(480, 480));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setJMenuBar(m_menuBar);
        
        // Add the split pane to the window.
        this.getContentPane().add(m_verticalSplit);
        this.pack();
	}
	
	/* **********************************************************
	                      INIT FUNCTIONS
	 ************************************************************/
	
	private void initMenuBar() {
		m_menuBar = new JMenuBar();		
		JMenu menu;
		
		// "File" 
		menu = createTopLevelMenu("File", KeyEvent.VK_F, "This contains basic functions for the project");
		menu.add(createMenuItem("Open", KeyEvent.VK_O, "Open a preexisting project", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK), Constants.IMPORT));
		menu.add(createMenuItem("Save Puzzle", KeyEvent.VK_U, "Save the current puzzle", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), null));
		menu.add(createMenuItem("Save Word List", KeyEvent.VK_L, "Save the current word list", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK), Constants.SAVE_WORDLIST));
		menu.add(createMenuItem("Export...", KeyEvent.VK_E, "Export puzzle or word list to...", KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK), Constants.EXPORT));
		menu.add(createMenuItem("Print", KeyEvent.VK_P, "Print current puzzle view", KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK), null));
		menu.add(createMenuItem("Exit", KeyEvent.VK_X, "Exit", KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK), Constants.EXIT));	
		m_menuBar.add(menu);
		
		// "Puzzle"
		menu = createTopLevelMenu("Puzzle", KeyEvent.VK_Z, "This contains functions to alter the current puzzle");
		menu.add(createMenuItem("Randomize", KeyEvent.VK_R, "Reorder the current puzzle", KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK), null));
		menu.add(createRadioButtonMenuItem("Show Solution", KeyEvent.VK_K, "Show or hide the puzzle key", false));
		m_menuBar.add(menu);

		// "Help"
		menu = createTopLevelMenu("Help", KeyEvent.VK_H, "Learn about the program");
		menu.add(createMenuItem("How to Use", KeyEvent.VK_W, "Get help how to use the program", KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK), null));
		menu.add(createMenuItem("About", KeyEvent.VK_A, "Get the current version of the program", KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK), Constants.ABOUT));
		m_menuBar.add(menu);
		
		m_menuBar.addMouseListener(this);
	}
	
	private void initPuzzlePanel() {
		m_puzzlePanel = new JPanel();
		setComponentSizes(m_puzzlePanel, 200, 200, 500, 500, 500, 500);
		m_puzzlePanel.addMouseListener(this);
	}
	
	private void initWordPanel() {
		// The top-level container
		m_wordsPanel = new JPanel();
		m_wordsPanel.addMouseListener(this);
		m_wordsPanel.setLayout(new BoxLayout(m_wordsPanel, BoxLayout.Y_AXIS));
		
		// Displays the currently entered words
		m_wordListPanel = new JPanel();
		m_wordListPanel.addMouseListener(this);
		setComponentSizes(m_wordListPanel, 200, 200, 200, 500, 200, 500);
		m_wordListPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));		
		m_wordListPanel.setLayout(new SpringLayout());	
		m_wordsPanel.add(m_wordListPanel);
		
		// The text field where the user types words
		m_wordEntryField = new JTextField(12);
		setComponentSizes(m_wordEntryField, 140, 20, 200, 20, 200, 24);
		m_wordEntryField.addKeyListener(this);
		m_wordEntryField.addMouseListener(this);
		m_wordsPanel.add(m_wordEntryField);		
		
		m_wordLabelList = new WordLabelList(m_model, m_wordListPanel);
		this.addComponentListener(m_wordLabelList);
        m_wordsPanel.addComponentListener(m_wordLabelList);
        
        //Buttons for navigating word lists
        nextWord = new BasicArrowButton(BasicArrowButton.EAST);
        prevWord = new BasicArrowButton(BasicArrowButton.WEST);
        JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new GridLayout(1, 2, 10, 10));
		prevWord.setName(PREVIOUS_BUTTON);
		nextWord.setName(NEXT_BUTTON);
		prevWord.addMouseListener(this);
		nextWord.addMouseListener(this);
		innerPanel.add(prevWord);
		innerPanel.add(nextWord);
		m_wordsPanel.add(innerPanel);
	}
	
	private void initPuzzleButtonPanel() {
		final int MAX_BUTTON_SIZE = 82;
		
		m_puzzleButtonPanel = new JPanel();
		m_puzzleButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		m_puzzleButtonPanel.setLayout(new BoxLayout(m_puzzleButtonPanel, BoxLayout.Y_AXIS));
		setComponentSizes(m_puzzleButtonPanel, 200, 100, 200, 150, 200, 200);
		m_puzzleButtonPanel.addMouseListener(this);
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new GridLayout(1, 2, 10, 10));
		
		//Buttons for navigating word lists
        nextWordPZL = new BasicArrowButton(BasicArrowButton.EAST);
        prevWordPZL = new BasicArrowButton(BasicArrowButton.WEST);
       
		prevWordPZL.setName(PREVIOUS_BUTTON_PZL);
		nextWordPZL.setName(NEXT_BUTTON_PZL);
		prevWordPZL.addMouseListener(this);
		nextWordPZL.addMouseListener(this);
		m_puzzlePanel.add(prevWordPZL);
		m_puzzlePanel.add(nextWordPZL);
		
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
	
	/* **********************************************************
              seems weird calling this from anywhere else
	 ************************************************************/
	
	/** Resets {@code m_puzzlePanel}'s contents, border, and layout. */
	private void updatePuzzlePanel() {
		m_puzzlePanel.removeAll();
		m_puzzlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		m_puzzlePanel.setLayout(new BoxLayout(m_puzzlePanel, BoxLayout.Y_AXIS));
		
		m_puzzlePanel.add(Box.createVerticalGlue());
		m_puzzlePanel.add(createPuzzlePanel());
		m_puzzlePanel.add(Box.createVerticalGlue());
		prevWordPZL.addMouseListener(this);
		nextWordPZL.addMouseListener(this);
		m_puzzlePanel.add(nextWordPZL);
		m_puzzlePanel.add(prevWordPZL);
		m_puzzlePanel.validate();
		
	}
	
	private JPanel createPuzzlePanel() {
		JPanel panel = new JPanel();
		Puzzle puzzle = m_model.getPuzzle();
		Grid grid = puzzle.getGrid();
		
		GridLayout layout = new GridLayout(grid.getHeight(), grid.getWidth());
		if (puzzle instanceof Crossword) {
			panel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
			panel.setBackground(Color.black);
		}
		panel.setLayout(layout);
		
		final int PIXELS_PER_CELL = 40;
		panel.setMinimumSize(new Dimension(grid.getWidth() * PIXELS_PER_CELL, grid.getHeight() * PIXELS_PER_CELL));
		panel.setPreferredSize(new Dimension(grid.getWidth() * PIXELS_PER_CELL, grid.getHeight() * PIXELS_PER_CELL));
		panel.setMaximumSize(new Dimension(grid.getWidth() * PIXELS_PER_CELL, grid.getHeight() * PIXELS_PER_CELL));
		
		JTextField cell;
		
		for (int y = 0; y < grid.getHeight(); y++) {
			for (int x = 0; x < grid.getWidth(); x++) {
				cell = new JTextField(Character.toString(grid.getCharAt(x, y)), 1);
				puzzle.applyCellStyle(cell);
				panel.add(cell);
			}
		}
		
		return panel;
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
            
		
		 
		if(!file.exists())
		{
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} 
		
		// Set default file name value as the unused file name we found
		
		
		PrintWriter fileOutput = null;
		try {
			fileOutput = new PrintWriter(file);
		} catch (FileNotFoundException e) {
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
				m_wordLabelList.changeTo(m_model.getNextWordList());
				
				
				while ((line = reader.readLine()) != null) {
					m_wordLabelList.addWord(line);//basic no string tokenizer yet
				}
		
			}
			catch(IOException e){
					;
			}
		}
		//return words;
	}
	/* **********************************************************
                        GETTER FUNCTIONS
	 ************************************************************/
	
	public JPanel getPuzzlePanel() {
		return m_puzzlePanel;
	}
	
	public JPanel getWordListPanel() {
		return m_wordsPanel;
	}
	
	public JPanel getPuzzleButtonPanel() {
		return m_puzzleButtonPanel;
	}
	
	public WordLabelList getWordList() {
		return m_wordLabelList;
	}
	
	/* **********************************************************
    					   HELPER FUNCTIONS
	 ************************************************************/
	
	/** Creates a JPanel with the specified sizes and {@linkplain MouseListener}. */
	public static JPanel createPanel(int minWidth, int minHeight, int prefWidth, int prefHeight, int maxWidth, int maxHeight, MouseListener mouseListener) {
		JPanel panel = new JPanel();
		panel.setMinimumSize(new Dimension(minWidth, minHeight));
		panel.setPreferredSize(new Dimension(prefWidth, prefHeight));
		panel.setMaximumSize(new Dimension(maxWidth, maxHeight));
		
		if (mouseListener != null) {
			panel.addMouseListener(mouseListener);
		}
		return panel;
	}
	
	private JMenu createTopLevelMenu(String label, int mnemonic, String description) {
		JMenu menu = new JMenu(label);
		menu.setMnemonic(mnemonic);
		menu.getAccessibleContext().setAccessibleDescription(description);
		menu.addMouseListener(this);
		return menu;
	}
	
	private JMenuItem createMenuItem(String label, int mnemonic, String description, KeyStroke shortcut, String actionCommand) {
		JMenuItem menuItem = new JMenuItem(label, mnemonic);
		menuItem.setAccelerator(shortcut);
		menuItem.getAccessibleContext().setAccessibleDescription(description);
		menuItem.setActionCommand(actionCommand);
		menuItem.addActionListener(this);
		return menuItem;
	}
	
	private JRadioButtonMenuItem createRadioButtonMenuItem(String label, int mnemonic, String description, boolean selected) {
		JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem(label);
		rbMenuItem.setMnemonic(mnemonic);
		rbMenuItem.getAccessibleContext().setAccessibleDescription(description);
		rbMenuItem.setSelected(selected);
		return rbMenuItem;
	}
	
	private void showAboutDialog(){
		if (m_aboutDialog == null) {
			final String aboutMessage = "Iteration 2";
			
			m_aboutDialog = new JDialog(this, "About");
			m_aboutDialog.add(new JLabel(aboutMessage));
			m_aboutDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		}
		m_aboutDialog.setSize(100, 100);
		m_aboutDialog.setLocationRelativeTo(this);
		m_aboutDialog.setVisible(true);
	}
	
	/** Sets the {@link Component#setMinimumSize(Dimension) minimum}, 
	 * {@link Component#setPreferredSize(Dimension) preferred}, and 
	 * {@link Component#setMaximumSize(Dimension) maximum} sizes of the given {@link Component component}. */
	private void setComponentSizes (Component c, int minWidth, int minHeight, int prefWidth, int prefHeight, int maxWidth, int maxHeight) {
		c.setMinimumSize(new Dimension(minWidth, minHeight));
		c.setPreferredSize(new Dimension(prefWidth, prefHeight));
		c.setMaximumSize(new Dimension(maxWidth, maxHeight));
	}
	
	/* **********************************************************
    					  LISTENER FUNCTIONS
	 ************************************************************/
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		switch (command) {
		case Constants.DELETE_WORD_LABEL:
			System.err.println("DELETE_WORD_LABEL NOT UNUSED");
			// This method also deletes the word from the model's list.
			if (!m_wordLabelList.deleteSelectedWord()) {
				System.err.println("Failed to delete word label");
			}
			break;
		case Constants.IMPORT:
			importFile();
			break;
		case Constants.SAVE_WORDLIST:
			saveFile(m_model.getWordList());
			break;
		case Constants.EXPORT:
			
			JFileChooser dlgSave;
			dlgSave = new JFileChooser ();
			File file;
			String path;
			int value = dlgSave.showSaveDialog(m_wordsPanel);
			if (value == JFileChooser.APPROVE_OPTION){ 
	            path = dlgSave.getSelectedFile().getAbsolutePath();
	            file = new File(path+".txt"); 
	            			 
				if(!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException ioe) {
						ioe.printStackTrace();
						return;
					}
				}
			
				BufferedWriter fileOutput = null;

				try {
					fileOutput = new BufferedWriter(new FileWriter(file));
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				
				if (fileOutput != null) {
					ArrayList<Puzzle> solutions = m_model.getSolutions();
					for (int index = 0; index < solutions.size(); index++) {
//						fileOutput.println(solutions.get(index));
						try {
							fileOutput.write(solutions.get(index).toString());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				}
				try {
					fileOutput.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			System.err.println("File written successfully.");
			break;
		case Constants.EXIT:
			System.exit(0);
			break;
		case Constants.ABOUT:
			showAboutDialog();
			break;
			
		default:
			System.err.println("Unrecognized command: " + command);
			break;
	
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			String word = m_wordEntryField.getText();
				if (m_wordLabelList.addWord(word)) {
					m_model.addWord(word);
					m_wordEntryField.setText("");
				}
		}
		else if (e.getKeyCode() == KeyEvent.VK_F7) {
			System.err.println("Printing model's data tree.");
			m_model.printTreeMap();
		}
		else if (e.getKeyCode() == KeyEvent.VK_F8) {
			System.err.println("Get new word list.");
			m_wordLabelList.changeTo(m_model.getNewWordList());
		}
		else if (e.getKeyCode() == KeyEvent.VK_F11) {
			System.err.println("Get previous word list.");
			m_wordLabelList.changeTo(m_model.getPreviousWordList());
		}
		else if (e.getKeyCode() == KeyEvent.VK_F12) {
			System.err.println("Get next word list.");
			m_wordLabelList.changeTo(m_model.getNextWordList());
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (!m_wordLabelList.deselectWord()) {
//			System.err.println("No word currently selected.");
		}
		
		String componentName = e.getComponent().getName();
		
		if (componentName == null) {
			return;
		}
		
		switch (componentName) {
			
			case WORD_SEARCH_BUTTON:
				System.err.println("Crossword button pressed.");
				m_model.generatePuzzles(Constants.TYPE_WORDSEARCH);
				break;
			
			case CROSSWORD_BUTTON:				
				System.err.println("Crossword button pressed.");
				m_model.generatePuzzles(Constants.TYPE_CROSSWORD);
				updatePuzzlePanel();
				break;
				
			case PREVIOUS_BUTTON:
				m_wordLabelList.changeTo(m_model.getNextWordList());
				
				break;
			case NEXT_BUTTON:
				if(m_model.hasNext())
					m_wordLabelList.changeTo(m_model.getNextWordList());
				else
					m_wordLabelList.changeTo(m_model.getNewWordList());
				break;
				
			case PREVIOUS_BUTTON_PZL:
				m_wordLabelList.changeTo(m_model.getNextWordList());
				
				break;
			case NEXT_BUTTON_PZL:
				if(m_model.hasNext())
					m_wordLabelList.changeTo(m_model.getNextWordList());
				else
					m_wordLabelList.changeTo(m_model.getNewWordList());
				break;
			default:
				System.err.println("Unhandled mouse click: " + e.getComponent().getClass().getName());
				break;
		}
	}
	
	/* **********************************************************
    				  UNUSED INHERITED FUNCTIONS
	 ************************************************************/
	
	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {
		m_wordLabelList.deselectWord();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
}
