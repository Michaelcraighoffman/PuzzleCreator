package puzzlemaker.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.plaf.basic.BasicArrowButton;

import puzzlemaker.Constants;
import puzzlemaker.Constants.MenuCommand;
import puzzlemaker.Constants.ProgramDefault;
import puzzlemaker.model.Model;
import puzzlemaker.puzzles.Crossword;
import puzzlemaker.puzzles.Puzzle;
import puzzlemaker.tools.grid.Grid;

public class View extends JFrame implements ActionListener, KeyListener, MouseListener, WindowListener {
	
	private static final long serialVersionUID = 3249856252715867854L;
	
	private final String WORD_SEARCH_BUTTON = "WORD_SEARCH_BUTTON";
	private final String CROSSWORD_BUTTON = "CROSSWORD_BUTTON";
	private final String STOP_BUTTON = "STOP_BUTTON";
	private final String PREVIOUS_BUTTON = "PREVIOUS_BUTTON";
	private final String NEXT_BUTTON = "NEXT_BUTTON";
	private final String PREVIOUS_BUTTON_PZL = "PREVIOUS_BUTTON_PZL";
	private final String NEXT_BUTTON_PZL = "NEXT_BUTTON_PZL";
	private final String NEW_WORDBUTTON = "NEW_WORDBUTTON";
	private final String CHK_SIZE_EXACTLY = "CHK_SIZE_EXACTLY";
	private final String PUZZLE_SIZE_OK = "PUZZLE_SIZE_OK";
	private final String PUZZLE_SIZE_CANCEL = "PUZZLE_SIZE_CANCEL";
	
	/* **********************************************************
	 * CLASS VARIABLES.
	 * 
	 * Organized by "has-a" relationships (JPanel listed first, followed by what it contains).
	 ************************************************************/
	
	private Model m_model;
	
	private JSplitPane m_verticalSplit, m_horizontalSplit;
	
	private JMenuBar m_menuBar;
	private JDialog m_puzzleSizeDialog;
	private JCheckBox m_chkSizeAtLeast, m_chkSizeAtMost, m_chkSizeExactly;
	private boolean m_puzzleSizeConstrainMin = false, m_puzzleSizeConstrainMax = true, m_puzzleSizeConstrainExactly = false; 
	private JTextField m_txtSizeAtLeastX, m_txtSizeAtLeastY, m_txtSizeAtMostX, m_txtSizeAtMostY, m_txtSizeExactlyX, m_txtSizeExactlyY;
	private int m_puzzleSizeMinX = ProgramDefault.PUZZLE_SIZE_MIN_X, m_puzzleSizeMinY = ProgramDefault.PUZZLE_SIZE_MIN_Y, m_puzzleSizeMaxX = ProgramDefault.PUZZLE_SIZE_MAX_X, m_puzzleSizeMaxY = ProgramDefault.PUZZLE_SIZE_MAX_Y, m_puzzleSizeExactlyX = ProgramDefault.PUZZLE_SIZE_EXACT_X, m_puzzleSizeExactlyY = ProgramDefault.PUZZLE_SIZE_EXACT_Y;
	private JDialog m_aboutDialog;
	
	/** Contains and displays the {@link #m_puzzle puzzle}. */
	private JPanel m_puzzlePanel;
	/** Arrow Buttons for wordLists */
	private BasicArrowButton nextWord, prevWord;
	private JButton newWord;
	private BasicArrowButton nextWordPZL, prevWordPZL;
	/** Contains and displays the {@link #m_wordListPanel word list} and the {@link #m_wordEntryField entry field}. */
	private JPanel m_wordsPanel;
	/** Contains one {@link JLabel} for each currently entered word. */
	private JPanel m_wordListPanel;
	/** @see WordLabelList */
	private WordLabelList m_wordLabelList;
	private JTextField m_wordEntryField;
	private int m_index = 1;

	
   /** Belongs to {@code m_wordsPanel}.<br>
	 * Contains one button for each type of puzzle (e.g.: word search, crossword, etc.). */
	private JPanel m_puzzleButtonPanel;

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
		menu.add(createMenuItem("Open", KeyEvent.VK_O, "Open a preexisting project", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK), MenuCommand.IMPORT));
		menu.add(createMenuItem("Save Puzzle", KeyEvent.VK_U, "Save the current puzzle", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), null));
		menu.add(createMenuItem("Save Word List", KeyEvent.VK_L, "Save the current word list", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK), MenuCommand.SAVE_WORDLIST));
		menu.add(createMenuItem("Export...", KeyEvent.VK_E, "Export puzzle or word list to...", KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK), MenuCommand.EXPORT));
		menu.add(createMenuItem("Print", KeyEvent.VK_P, "Print current puzzle view", KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK), null));
		menu.add(createMenuItem("Exit", KeyEvent.VK_X, "Exit", KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK), MenuCommand.EXIT));	
		m_menuBar.add(menu);
		
		// "Puzzle"
		menu = createTopLevelMenu("Puzzle", KeyEvent.VK_Z, "This contains functions to alter the current puzzle");
		menu.add(createMenuItem("Randomize", KeyEvent.VK_R, "Reorder the current puzzle", KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK), null));
		menu.add(createMenuItem("Set Puzzle Size Limits...", KeyEvent.VK_S, "Set size constraints on the puzzles that are generated", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK + ActionEvent.CTRL_MASK), MenuCommand.PUZZLE_SIZE));
		menu.add(createRadioButtonMenuItem("Show Solution", KeyEvent.VK_K, "Show or hide the puzzle key", false));
		m_menuBar.add(menu);

		// "Help"
		menu = createTopLevelMenu("Help", KeyEvent.VK_H, "Learn about the program");
		menu.add(createMenuItem("How to Use", KeyEvent.VK_W, "Get help how to use the program", KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK), null));
		menu.add(createMenuItem("About", KeyEvent.VK_A, "Get the current version of the program", KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK), MenuCommand.ABOUT));
		m_menuBar.add(menu);
		
		m_menuBar.addMouseListener(this);
	}
	
	private void initPuzzlePanel() {
		final int MAX_BUTTON_SIZE = 82;
		m_puzzlePanel = new JPanel();
		setComponentSizes(m_puzzlePanel, 200, 190, 500, 490, 500, 490);
		m_puzzlePanel.addMouseListener(this);
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new GridLayout(1, 2, 10, 10));
		nextWordPZL = new BasicArrowButton(BasicArrowButton.EAST);
        prevWordPZL = new BasicArrowButton(BasicArrowButton.WEST);
        JLabel index = new JLabel("0/0");
		prevWordPZL.setName(PREVIOUS_BUTTON_PZL);
		nextWordPZL.setName(NEXT_BUTTON_PZL);
		prevWordPZL.addMouseListener(this);
		nextWordPZL.addMouseListener(this);
		innerPanel.add(prevWordPZL);
		innerPanel.add(index);
		innerPanel.add(nextWordPZL);
		innerPanel.setMaximumSize(new Dimension((MAX_BUTTON_SIZE * 2) + 10, MAX_BUTTON_SIZE));
		m_puzzlePanel.add(innerPanel);
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
		newWord = new JButton("+");
		newWord.setName(NEW_WORDBUTTON);
		newWord.addMouseListener(this);
		innerPanel.add(prevWord);
		innerPanel.add(nextWord);
		innerPanel.add(newWord);
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
		
		JButton btnWordSearch = new JButton(new ImageIcon("res/wordsearch.png"));
		btnWordSearch.addMouseListener(this);
		btnWordSearch.setName(WORD_SEARCH_BUTTON);
		innerPanel.add(btnWordSearch);
		
		JButton btnCrossWord = new JButton(new ImageIcon("res/crossword.png"));
		btnCrossWord.addMouseListener(this);
		btnCrossWord.setName(CROSSWORD_BUTTON);
		innerPanel.add(btnCrossWord);
		
//		JButton btnStop = new JButton("Stop");
//		btnStop.addMouseListener(this);
//		btnStop.setName(STOP_BUTTON);
//		innerPanel.add(btnStop);
		// Minimum and preferred sizes don't need to be set since 
		// m_horizontalSplit.getBottomComponent()'s minimum size is set.
		innerPanel.setMaximumSize(new Dimension((MAX_BUTTON_SIZE * 2) + 10, MAX_BUTTON_SIZE));

		m_puzzleButtonPanel.add(Box.createVerticalGlue());
		m_puzzleButtonPanel.add(innerPanel);
		m_puzzleButtonPanel.add(Box.createVerticalGlue());
	}
	
	/** @author szeren */
	public void initPuzzleSizeDialog() {
		m_puzzleSizeDialog = new JDialog(View.this, "Puzzle Size Limits", true);
		m_puzzleSizeDialog.addWindowListener(this);
		m_puzzleSizeDialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setComponentSizes(m_puzzleSizeDialog, 250, 150, 250, 150, 250, 150);
		m_puzzleSizeDialog.setResizable(false);
		
		m_chkSizeAtLeast = new JCheckBox("At least:", false);
		m_txtSizeAtLeastX = new JTextField(2);
		m_txtSizeAtLeastY = new JTextField(2);
		m_chkSizeAtMost = new JCheckBox("At most:", true);
		m_txtSizeAtMostX = new JTextField(2);
		m_txtSizeAtMostY = new JTextField(2);
		m_chkSizeExactly = new JCheckBox("Exactly:", false);
		m_txtSizeExactlyX = new JTextField(2);
		m_txtSizeExactlyY = new JTextField(2);
		
		m_chkSizeExactly.addActionListener(this);
		m_chkSizeExactly.setActionCommand(CHK_SIZE_EXACTLY);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.CENTER;
		c.insets.right = 5;
		inputPanel.add(m_chkSizeAtLeast, c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets.right = 0;
		inputPanel.add(new JLabel("X: "), c);
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets.right = 5;
		inputPanel.add(m_txtSizeAtLeastX, c);
		c.gridx = 3;
		c.anchor = GridBagConstraints.LINE_END;
		inputPanel.add(new JLabel("Y: "), c);
		c.gridx = 4;
		c.anchor = GridBagConstraints.LINE_START;
		inputPanel.add(m_txtSizeAtLeastY, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.insets.right = 5;
		inputPanel.add(m_chkSizeAtMost, c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets.right = 0;
		inputPanel.add(new JLabel("X: "), c);
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets.right = 5;
		inputPanel.add(m_txtSizeAtMostX, c);
		c.gridx = 3;
		c.anchor = GridBagConstraints.LINE_END;
		inputPanel.add(new JLabel("Y: "), c);
		c.gridx = 4;
		c.anchor = GridBagConstraints.LINE_START;
		inputPanel.add(m_txtSizeAtMostY, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.CENTER;
		c.insets.right = 5;
		inputPanel.add(m_chkSizeExactly, c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets.right = 0;
		inputPanel.add(new JLabel("X: "), c);
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets.right = 5;
		inputPanel.add(m_txtSizeExactlyX, c);
		c.gridx = 3;
		c.anchor = GridBagConstraints.LINE_END;
		inputPanel.add(new JLabel("Y: "), c);
		c.gridx = 4;
		c.anchor = GridBagConstraints.LINE_START;
		inputPanel.add(m_txtSizeExactlyY, c);
		
		JButton btnOK = new JButton("OK");
		btnOK.setActionCommand(PUZZLE_SIZE_OK);
		btnOK.addActionListener(this);
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setActionCommand(PUZZLE_SIZE_CANCEL);
		btnCancel.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(btnOK);
		buttonPanel.add(Box.createHorizontalStrut(20));
		buttonPanel.add(btnCancel);
		
		m_puzzleSizeDialog.getContentPane().setLayout(new BoxLayout(m_puzzleSizeDialog.getContentPane(), BoxLayout.Y_AXIS));
		m_puzzleSizeDialog.getContentPane().add(inputPanel);
//		m_puzzleSizeDialog.getContentPane().add(Box.createVerticalStrut(15));
		m_puzzleSizeDialog.getContentPane().add(buttonPanel);
//		m_puzzleSizeDialog.getContentPane().add(Box.createVerticalStrut(15));
	}
	
	/** Resets {@code m_puzzlePanel}'s contents, border, and layout. */
	private void updatePuzzlePanel() {
		final int MAX_BUTTON_SIZE = 82;
		m_puzzlePanel.removeAll();
		m_puzzlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		m_puzzlePanel.setLayout(new BoxLayout(m_puzzlePanel, BoxLayout.Y_AXIS));
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
		JLabel index = new JLabel(m_index + "/" + m_model.getNumPuzzles());
		innerPanel.add(prevWordPZL);
		innerPanel.add(index);
		innerPanel.add(nextWordPZL);
		innerPanel.setMaximumSize(new Dimension((MAX_BUTTON_SIZE * 2) + 10, MAX_BUTTON_SIZE));
		m_puzzlePanel.add(innerPanel);
		m_puzzlePanel.add(Box.createVerticalGlue());
		m_puzzlePanel.add(createPuzzlePanel());
		m_puzzlePanel.add(Box.createVerticalGlue());
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
	
	/**
	 * Saves a wordList to a txt file
	 * @param words
	 */
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
/**
 * Takes a txt file and outputs the filtered contents as a wordList
 */
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
				while ((line = reader.readLine()) != null) {
					m_wordLabelList.addWord(line);//basic no string tokenizer yet
					m_model.addWord(line);
				}
		
			}
			catch(IOException e){
					;
			}
		}
		
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
	
	/** Shows the puzzle size dialog and updates the text field values to reflect the program's current values. 
	 * 
	 * @author szeren */
	private void showPuzzleSizeDialog() {
		if (m_puzzleSizeDialog == null) {
			initPuzzleSizeDialog();
		}
		
		if (m_puzzleSizeMinX != -1) {
			m_txtSizeAtLeastX.setText(Integer.toString(m_puzzleSizeMinX));
		}
		else {
			m_txtSizeAtLeastX.setText("");
		}
		if (m_puzzleSizeMinY != -1) {
			m_txtSizeAtLeastY.setText(Integer.toString(m_puzzleSizeMinY));
		}
		else {
			m_txtSizeAtLeastY.setText("");
		}
		if (m_puzzleSizeMaxX != -1) {
			m_txtSizeAtMostX.setText(Integer.toString(m_puzzleSizeMaxX));
		}
		else {
			m_txtSizeAtMostX.setText("");
		}
		if (m_puzzleSizeMaxY != -1) {
			m_txtSizeAtMostY.setText(Integer.toString(m_puzzleSizeMaxY));
		}
		else {
			m_txtSizeAtMostY.setText("");
		}
		if (m_puzzleSizeExactlyX != -1) {
			m_txtSizeExactlyX.setText(Integer.toString(m_puzzleSizeExactlyX));
		}
		else {
			m_txtSizeExactlyX.setText("");
		}
		if (m_puzzleSizeExactlyY != -1) {
			m_txtSizeExactlyY.setText(Integer.toString(m_puzzleSizeExactlyY));
		}
		else {
			m_txtSizeExactlyY.setText("");
		}
		
		m_puzzleSizeDialog.setLocationRelativeTo(View.this);
		m_txtSizeAtLeastX.setBackground(Color.WHITE);
		m_txtSizeAtLeastY.setBackground(Color.WHITE);
		m_txtSizeAtMostX.setBackground(Color.WHITE);
		m_txtSizeAtMostY.setBackground(Color.WHITE);
		m_txtSizeExactlyX.setBackground(Color.WHITE);
		m_txtSizeExactlyY.setBackground(Color.WHITE);
		
		m_chkSizeAtLeast.setSelected(m_puzzleSizeConstrainMin);
		m_chkSizeAtMost.setSelected(m_puzzleSizeConstrainMax);
		if (m_chkSizeExactly.isSelected() != m_puzzleSizeConstrainExactly) {
			m_chkSizeExactly.doClick();
		}
		
		m_puzzleSizeDialog.setVisible(true);
	}
	
	/** Updates the puzzle size constraints and hides the puzzle size dialog.<br>
	 * Will not update constraints if input is illegal.<br>
	 * Will not enforce legality of input if constraint type is not selected.<br>
	 * Updates are atomic.
	 * 
	 * @param applyChanges {@code false} if the user clicked Cancel.
	 * 
	 * @author szeren
	 */
	private void hidePuzzleSizeDialog(boolean applyChanges) {
		if (applyChanges) {
			m_txtSizeAtLeastX.setBackground(Color.WHITE);
			m_txtSizeAtLeastY.setBackground(Color.WHITE);
			m_txtSizeAtMostX.setBackground(Color.WHITE);
			m_txtSizeAtMostY.setBackground(Color.WHITE);
			m_txtSizeExactlyX.setBackground(Color.WHITE);
			m_txtSizeExactlyY.setBackground(Color.WHITE);
			
			boolean invalidNumberFormat = false, invalidNumberValue = false, invalidMinMax = false;

			int minX, minY, maxX, maxY, exactX, exactY;
			minX = minY = maxX = maxY = exactX = exactY = Integer.MIN_VALUE;
			
			if (m_chkSizeAtLeast.isSelected() && m_chkSizeAtLeast.isEnabled()) {
				try {
					minX = Integer.valueOf(m_txtSizeAtLeastX.getText());
				} catch (NumberFormatException nfe) {
					invalidNumberFormat = true;
					m_txtSizeAtLeastX.setBackground(Color.RED);
				}
				
				if ((minX < 1 || minX > 50) && minX != Integer.MIN_VALUE) {
					invalidNumberValue = true;
					m_txtSizeAtLeastX.setBackground(Color.RED);
				}
				
				try {
					minY = Integer.valueOf(m_txtSizeAtLeastY.getText());
				} catch (NumberFormatException nfe) {
					invalidNumberFormat = true;
					m_txtSizeAtLeastY.setBackground(Color.RED);
				}
				
				if ((minY < 1 || minY > 50) && minY != Integer.MIN_VALUE) {
					invalidNumberValue = true;
					m_txtSizeAtLeastY.setBackground(Color.RED);
				}
			}
			
			if (m_chkSizeAtMost.isSelected() && m_chkSizeAtMost.isEnabled()) {
				try {
					maxX = Integer.valueOf(m_txtSizeAtMostX.getText());
				} catch (NumberFormatException nfe) {
					invalidNumberFormat = true;
					m_txtSizeAtMostX.setBackground(Color.RED);
				}
				
				if ((maxX < 1 || maxX > 50) && maxX != Integer.MIN_VALUE) {
					invalidNumberValue = true;
					m_txtSizeAtMostX.setBackground(Color.RED);
				}
				
				try {
					maxY = Integer.valueOf(m_txtSizeAtMostY.getText());
				} catch (NumberFormatException nfe) {
					invalidNumberFormat = true;
					m_txtSizeAtMostY.setBackground(Color.RED);
				}
				
				if ((maxY < 1 || maxY > 50) && maxY != Integer.MIN_VALUE) {
					invalidNumberValue = true;
					m_txtSizeAtMostY.setBackground(Color.RED);
				}
			}

			if (m_chkSizeExactly.isSelected()) {
				try {
					exactX = Integer.valueOf(m_txtSizeExactlyX.getText());
				} catch (NumberFormatException nfe) {
					invalidNumberFormat = true;
					m_txtSizeExactlyX.setBackground(Color.RED);
				}
				
				if ((exactX < 1 || exactX > 50) && exactX != Integer.MIN_VALUE) {
					invalidNumberValue = true;
					m_txtSizeExactlyX.setBackground(Color.RED);
				}
				
				try {
					exactY = Integer.valueOf(m_txtSizeExactlyY.getText());
				} catch (NumberFormatException nfe) {
					invalidNumberFormat = true;
					m_txtSizeExactlyY.setBackground(Color.RED);
				}
				
				if ((exactY < 1 || exactY > 50) && exactY != Integer.MIN_VALUE) {
					invalidNumberValue = true;
					m_txtSizeExactlyY.setBackground(Color.RED);
				}
			}
			
			if (m_chkSizeAtLeast.isSelected() && m_chkSizeAtMost.isSelected() && !m_chkSizeExactly.isSelected()) {
				if ((maxX < minX || maxY < minY) && minX != Integer.MIN_VALUE && minY != Integer.MIN_VALUE && maxX != Integer.MIN_VALUE && maxY != Integer.MIN_VALUE) {
					invalidMinMax = true;
				}
			}
			
			if (invalidNumberFormat || invalidNumberValue || invalidMinMax) {
				String errorMessage = "";
				
				if (invalidNumberFormat) {
					errorMessage = "Invalid number format.";
				}
				
				if (invalidNumberValue) {
					if (!errorMessage.isEmpty()) {
						errorMessage = errorMessage + "\n\n";
					}
					
					errorMessage = errorMessage + "Invalid number value (must be between 1 and 50 (inclusive)).";
				}
				
				if (invalidMinMax) {
					if (!errorMessage.isEmpty()) {
						errorMessage = errorMessage + "\n\n";
					}
					
					errorMessage = errorMessage + "Minimum dimensions cannot exceed maximum dimensions. (really?)";
				}
				
				showInvalidSizeEntryDialog(errorMessage);
				return;
			}
			else {
				m_puzzleSizeConstrainMin = m_chkSizeAtLeast.isSelected();
				m_puzzleSizeConstrainMax = m_chkSizeAtMost.isSelected();
				m_puzzleSizeConstrainExactly = m_chkSizeExactly.isSelected();
				
				if (minX != Integer.MIN_VALUE) {
					m_puzzleSizeMinX = minX;
				}
				if (minY != Integer.MIN_VALUE) {
					m_puzzleSizeMinY = minY;
				}
				if (maxX != Integer.MIN_VALUE) {
					m_puzzleSizeMaxX = maxX;
				}
				if (maxY != Integer.MIN_VALUE) {
					m_puzzleSizeMaxY = maxY;
				}
				if (exactX != Integer.MIN_VALUE) {
					m_puzzleSizeExactlyX = exactX;
				}
				if (exactY != Integer.MIN_VALUE) {
					m_puzzleSizeExactlyY = exactY;
				}
				
				m_model.setMinPuzzleSize(m_puzzleSizeConstrainMin, m_puzzleSizeMinX, m_puzzleSizeMinY);
				m_model.setMaxPuzzleSize(m_puzzleSizeConstrainMax, m_puzzleSizeMaxX, m_puzzleSizeMaxY);
				m_model.setExactlPuzzleSize(m_puzzleSizeConstrainExactly, m_puzzleSizeExactlyX, m_puzzleSizeExactlyY);
			}
		}
		
		if (m_txtSizeAtLeastX.getText() == "") {
			m_puzzleSizeMinX = -1;
		}
		if (m_txtSizeAtLeastY.getText() == "") {
			m_puzzleSizeMinY = -1;
		}
		if (m_txtSizeAtMostX.getText() == "") {
			m_puzzleSizeMaxX = -1;
		}
		if (m_txtSizeAtMostY.getText() == "") {
			m_puzzleSizeMaxY = -1;
		}
		if (m_txtSizeExactlyX.getText() == "") {
			m_puzzleSizeExactlyX = -1;
		}
		if (m_txtSizeExactlyY.getText() == "") {
			m_puzzleSizeExactlyY = -1;
		}
		
		m_puzzleSizeDialog.setVisible(false);
	}
	
	/** @author szeren */
	private void showInvalidSizeEntryDialog(String message) {
		JOptionPane.showMessageDialog(m_puzzleSizeDialog, message, "Invalid Entry", JOptionPane.ERROR_MESSAGE);
	}
	
	/** @author szeren */
	private boolean puzzleSizeDialogHasUnconfirmedChanges() {
		if (m_chkSizeAtLeast.isSelected() != m_puzzleSizeConstrainMin || m_chkSizeAtMost.isSelected() != m_puzzleSizeConstrainMax || m_chkSizeExactly.isSelected() != m_puzzleSizeConstrainExactly) {
			return true;
		}
		
		if (m_puzzleSizeMinX != -1) {
			try {
				if (m_puzzleSizeMinX != Integer.valueOf(m_txtSizeAtLeastX.getText())) {
					return true;
				}
			} catch (NumberFormatException nfe) {}
		}
		
		if (m_puzzleSizeMinY != -1) {
			try {
				if (m_puzzleSizeMinY != Integer.valueOf(m_txtSizeAtLeastY.getText())) {
					return true;
				}
			} catch (NumberFormatException nfe) {}
		}
		
		if (m_puzzleSizeMaxX != -1) {
			try {
				if (m_puzzleSizeMaxX != Integer.valueOf(m_txtSizeAtMostX.getText())) {
					return true;
				}
			} catch (NumberFormatException nfe) {}
		}
		
		if (m_puzzleSizeMaxY != -1) {
			try {
				if (m_puzzleSizeMaxY != Integer.valueOf(m_txtSizeAtMostY.getText())) {
					return true;
				}
			} catch (NumberFormatException nfe) {}
		}
		
		if (m_puzzleSizeExactlyX != -1) {
			try {
				if (m_puzzleSizeExactlyX != Integer.valueOf(m_txtSizeExactlyX.getText())) {
					return true;
				}
			} catch (NumberFormatException nfe) {}
		}
		
		if (m_puzzleSizeExactlyY != -1) {
			try {
				if (m_puzzleSizeExactlyY != Integer.valueOf(m_txtSizeExactlyY.getText())) {
					return true;
				}
			} catch (NumberFormatException nfe) {}
		}
		
		System.err.println("No changes in puzzle size dialog.");
		return false;
	}
	
	/**
	 * Shows about dialog box.
	 */
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
	 * {@link Component#setMaximumSize(Dimension) maximum} sizes of the given {@code Component}. 
	 * 
	 * @author szeren
	 */
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
		case MenuCommand.IMPORT:
			m_wordLabelList.changeTo(m_model.getNewWordList());
			importFile();
			break;
		case MenuCommand.SAVE_WORDLIST:
			saveFile(m_model.getWordList());
			break;
		case MenuCommand.EXPORT:
			
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
		case MenuCommand.EXIT:
			System.exit(0);
			break;
		case MenuCommand.PUZZLE_SIZE:
			showPuzzleSizeDialog();
			break;
		case CHK_SIZE_EXACTLY:
			if (m_chkSizeExactly.isSelected()) {
				m_chkSizeAtLeast.setEnabled(false);
				m_txtSizeAtLeastX.setEnabled(false);
				m_txtSizeAtLeastY.setEnabled(false);
				
				m_chkSizeAtMost.setEnabled(false);
				m_txtSizeAtMostX.setEnabled(false);
				m_txtSizeAtMostY.setEnabled(false);
			}
			else {
				m_chkSizeAtLeast.setEnabled(true);
				m_txtSizeAtLeastX.setEnabled(true);
				m_txtSizeAtLeastY.setEnabled(true);
				
				m_chkSizeAtMost.setEnabled(true);
				m_txtSizeAtMostX.setEnabled(true);
				m_txtSizeAtMostY.setEnabled(true);
			}
			break;
		case PUZZLE_SIZE_OK:
			System.err.println("ok");
			hidePuzzleSizeDialog(true);
			break;
		case PUZZLE_SIZE_CANCEL:
			System.err.println("cancel");
			hidePuzzleSizeDialog(false);
			break;
		case MenuCommand.ABOUT:
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
			if(m_model.getPuzzle()!=null) {
				  ArrayList<String> old=m_model.getWordList();
				  m_model.getNewWordList();
				  for(String s : old) {
					  m_model.addWord(s);
				  }
				  m_wordLabelList.changeTo(m_model.getWordList());
				  m_model.clearSelected();
			}
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
				System.err.println("Wordsearch button pressed.");
				if (!m_model.getWordList().isEmpty()) {
					if(m_model.getPuzzle()!=null) {
						ArrayList<String> old=m_model.getWordList();
						m_model.getNewWordList();
						for(String s : old) {
							m_model.addWord(s);
						}
						m_wordLabelList.changeTo(m_model.getWordList());
						updatePuzzlePanel();
					}
					m_model.startPuzzleGenerator(Constants.TYPE_WORDSEARCH);
					updatePuzzlePanel();
				}
			break;
			
			case CROSSWORD_BUTTON:				
				System.err.println("Crossword button pressed.");
				if (!m_model.getWordList().isEmpty()) {
					if(m_model.getPuzzle()!=null) {
						  ArrayList<String> old=m_model.getWordList();
						  m_model.getNewWordList();
						  for(String s : old) {
							  m_model.addWord(s);
						  }
						  m_wordLabelList.changeTo(m_model.getWordList());
						  updatePuzzlePanel();
					  }
					m_model.startPuzzleGenerator(Constants.TYPE_CROSSWORD);
					updatePuzzlePanel();
				}
				break;
			case STOP_BUTTON:
				// m_model.stopGeneration();
				 break;
			case PREVIOUS_BUTTON:
				m_wordLabelList.changeTo(m_model.getNextWordList());
				if(m_model.getCurrentWordPuzzle() != null){
					m_index = 1;
					updatePuzzlePanel();
				}
				break;
			case NEXT_BUTTON:
				m_wordLabelList.changeTo(m_model.getNextWordList());
				if(m_model.getCurrentWordPuzzle() != null){
					m_index = 1;
					updatePuzzlePanel();
				}
				break;
				
			case PREVIOUS_BUTTON_PZL:
				m_model.getPreviousPuzzle();
				if(m_index != 1)
					m_index--;
				else
					m_index = m_model.getNumPuzzles();
				updatePuzzlePanel();
				break;
				
			case NEW_WORDBUTTON:
				m_wordLabelList.changeTo(m_model.getNewWordList());
				m_model.clearSelected();
				break;
			case NEXT_BUTTON_PZL:
				m_model.getNextPuzzle();
				if(m_index >= m_model.getNumPuzzles())
					m_index = 1;
				else
					m_index++;
				updatePuzzlePanel();
				break;
			default:
				System.err.println("Unhandled mouse click: " + e.getComponent().getClass().getName());
				break;
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		m_wordLabelList.deselectWord();
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		if (puzzleSizeDialogHasUnconfirmedChanges()) {
			int result = JOptionPane.showOptionDialog(m_puzzleSizeDialog, "Discard changes?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] {"Apply", "Discard"}, null);
			if (result == 0) {
				hidePuzzleSizeDialog(true);
			}
			else {
				hidePuzzleSizeDialog(false);
			}
		}
		else {
			hidePuzzleSizeDialog(false);
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		System.err.println("Puzzle size window closed.");
	}
	
	/* **********************************************************
    				  UNUSED INHERITED FUNCTIONS
	 ************************************************************/
	
	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
}
