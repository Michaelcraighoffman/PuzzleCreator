package puzzlemaker.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
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
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicArrowButton;

import puzzlemaker.Constants;
import puzzlemaker.Constants.DefaultOptions;
import puzzlemaker.Constants.MenuCommand;
import puzzlemaker.model.Model;
import puzzlemaker.puzzles.Crossword;
import puzzlemaker.puzzles.Puzzle;
import puzzlemaker.tools.grid.Grid;

public class View extends JFrame implements ActionListener, Printable, KeyListener, MouseListener, WindowListener {
	
	private static final long serialVersionUID = 3249856252715867854L;
	
	private final String WORD_SEARCH_BUTTON = "WORD_SEARCH_BUTTON";
	private final String CROSSWORD_BUTTON = "CROSSWORD_BUTTON";
	private final String STOP_BUTTON = "STOP_BUTTON";
	private final String PREVIOUS_WORDLIST_BUTTON = "PREVIOUS_BUTTON";
	private final String NEXT_WORDLIST_BUTTON = "NEXT_BUTTON";
	private final String PREVIOUS_PUZZLE_BUTTON = "PREVIOUS_PUZZLE_BUTTON";
	private final String NEXT_PUZZLE_BUTTON = "NEXT_PUZZLE_BUTTON";
	private final String NEW_WORDLIST_BUTTON = "NEW_WORDLIST_BUTTON";
	private final String CHK_SIZE_EXACTLY = "CHK_SIZE_EXACTLY";
	private final String PUZZLE_SIZE_OK = "PUZZLE_SIZE_OK";
	private final String PUZZLE_SIZE_CANCEL = "PUZZLE_SIZE_CANCEL";
	private final String PUZZLE_BUTTONS = "PUZZLE_BUTTONS";
	
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
	private int m_puzzleSizeMinX = DefaultOptions.PUZZLE_SIZE_MIN_X, m_puzzleSizeMinY = DefaultOptions.PUZZLE_SIZE_MIN_Y, m_puzzleSizeMaxX = DefaultOptions.PUZZLE_SIZE_MAX_X, m_puzzleSizeMaxY = DefaultOptions.PUZZLE_SIZE_MAX_Y, m_puzzleSizeExactlyX = DefaultOptions.PUZZLE_SIZE_EXACT_X, m_puzzleSizeExactlyY = DefaultOptions.PUZZLE_SIZE_EXACT_Y;
	private JCheckBoxMenuItem m_chkBoxNonSquare;
	private JCheckBoxMenuItem m_chkBoxShowSolutions, m_popupChkBoxShowSolutions;
	private boolean m_puzzleShowSolutions = DefaultOptions.PUZZLE_SHOW_SOLUTIONS;
	private boolean m_generateCrossword = DefaultOptions.PUZZLE_GENERATE_CROSSWORD;
	private JButton m_StartStopButton;
	private JPopupMenu m_puzzlePopupMenu;
	private JDialog m_aboutDialog;
	
	/** Contains and displays the {@link #m_puzzle puzzle}. */
	private JPanel m_puzzlePanel;
	/** For displaying the grid of characters in the puzzle panel. */
	private JPanel m_puzzleDisplayPanel;
	/** For displaying which puzzle is currently selected of the solution set. */
	private JLabel m_lblPuzzleIndex;
	/** Arrow Buttons for wordLists */
	private JButton m_newWordList;
	private BasicArrowButton nextWordPZL, prevWordPZL;
	/** Contains and displays the {@link #m_wordListPanel word list} and the {@link #m_wordEntryField entry field}. */
	private JPanel m_wordsPanel;
	private ArrayList<JPanel> m_wordsPanels = new ArrayList<JPanel>();
	/** Contains one {@link JLabel} for each currently entered word. */
	private JPanel m_wordListPanel;
	private JScrollPane m_wordListScrollPane;
	
	/** @see WordLabelList */
	private JTabbedPane m_Tabs;
	private WordLabelList m_wordLabelList;
	private ArrayList<WordLabelList> m_wordLabelLists = new ArrayList<WordLabelList>();
	private ArrayList<JTextField> m_wordEntryFields = new ArrayList<JTextField>();
	private JTextField m_wordEntryField;
	private int m_puzzleIndex = 1;
	/** Used for tabs and wordlist Jpanels*/
	private int m_Windex = 0;
	/**Keeps track of selectedWordList in relation to next selectedTab basic stupid method **/
	private int wordDex = 0;
	private int pageNumber = 0;

	
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
		initTabPane();
		initPuzzleButtonPanel();
        
        // Initialize the split pane.
        m_horizontalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        m_horizontalSplit.setTopComponent(m_Tabs);
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
        
        this.getRootPane().getGlassPane().addMouseListener(this);
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
		menu.add(createMenuItem("Print", KeyEvent.VK_P, "Print current puzzle view", KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK), MenuCommand.PRINT));
		menu.add(createMenuItem("Exit", KeyEvent.VK_X, "Exit", KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK), MenuCommand.EXIT));	
		m_menuBar.add(menu);
		
		// "Puzzle"
			menu = createTopLevelMenu("Puzzle", KeyEvent.VK_Z, "This contains functions to alter the current puzzle");
			menu.add(createMenuItem("Randomize", KeyEvent.VK_R, "Reorder the current puzzle", KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK), null));
			menu.add(createMenuItem("Set Puzzle Size Limits...", KeyEvent.VK_S, "Set size constraints on the puzzles that are generated", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK + ActionEvent.CTRL_MASK), MenuCommand.PUZZLE_SIZE));
			m_chkBoxNonSquare = createCheckBoxMenuItem("Allow Non-square Puzzles", KeyEvent.VK_N, "Allow puzzles to have a width different from their height", MenuCommand.PUZZLE_NON_SQUARE, DefaultOptions.PUZZLE_ALLOW_NON_SQUARE);
			menu.add(m_chkBoxNonSquare);
			m_chkBoxShowSolutions = createCheckBoxMenuItem("Show Solutions", KeyEvent.VK_K, "Show or hide the puzzle key", MenuCommand.PUZZLE_SHOW_SOLUTIONS, DefaultOptions.PUZZLE_SHOW_SOLUTIONS);
			menu.add(m_chkBoxShowSolutions);
			m_popupChkBoxShowSolutions = createCheckBoxMenuItem("Show Solutions", KeyEvent.VK_K, "Show or hide the puzzle key", MenuCommand.PUZZLE_SHOW_SOLUTIONS, DefaultOptions.PUZZLE_SHOW_SOLUTIONS);
			m_puzzlePopupMenu = new JPopupMenu();
			m_puzzlePopupMenu.add(m_popupChkBoxShowSolutions);
				
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
        m_lblPuzzleIndex = new JLabel("0/0");
		prevWordPZL.setName(PREVIOUS_PUZZLE_BUTTON);
		nextWordPZL.setName(NEXT_PUZZLE_BUTTON);
		prevWordPZL.addMouseListener(this);
		nextWordPZL.addMouseListener(this);
		innerPanel.add(prevWordPZL);
		innerPanel.add(m_lblPuzzleIndex);
		innerPanel.add(nextWordPZL);
		innerPanel.setMaximumSize(new Dimension((MAX_BUTTON_SIZE * 2) + 10, MAX_BUTTON_SIZE));
		m_puzzlePanel.add(innerPanel);
		
		m_puzzleDisplayPanel = new JPanel();
	}
	
	// FIXME: This is the initWordPanel method with some variable replacements that I'm not convinced couldn't
//	 be squashed into a single method. Please do so. -SBW
	private void nextWordTab(){	
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
		m_wordEntryFields.add(m_wordEntryField);
		m_wordsPanel.add(m_wordEntryFields.get(m_Windex));		
		
		m_wordLabelList = new WordLabelList(m_model, m_wordListPanel);
		this.addComponentListener(m_wordLabelList);
		m_wordLabelLists.add(m_wordLabelList);
        m_wordsPanel.addComponentListener(m_wordLabelLists.get(m_Windex));
       
        JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new GridLayout(1, 2, 10, 10));
		
		m_newWordList = new JButton("+");
		m_newWordList.setName(NEW_WORDLIST_BUTTON);
		m_newWordList.addMouseListener(this);
		innerPanel.add(m_newWordList);
		m_wordsPanel.add(innerPanel);
		m_wordsPanels.add(m_wordsPanel);
		
		m_wordsPanels.get(m_Windex).addMouseListener(this);
		
	}
	
	private void initWordPanel() {
		
		
		
		// The top-level container
		
		m_wordsPanel = new JPanel();
		m_wordsPanel.addMouseListener(this);
		m_wordsPanel.setLayout(new BoxLayout(m_wordsPanel, BoxLayout.Y_AXIS));
		
		// Displays the currently entered words
		m_wordListPanel = new JPanel();
		m_wordListPanel.addMouseListener(this);
		
		setComponentSizes(m_wordListPanel, 100, 100, 100, 400, 100, 400);
		m_wordListPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));		
		m_wordListPanel.setLayout(new SpringLayout());	
//		m_wordsPanel.add(m_wordListPanel);
		
		m_wordListScrollPane = new JScrollPane(m_wordListPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	// AEZ removed set size to allow scrollpane to resize with panel 
	//	setComponentSizes(m_wordListScrollPane, 200, 200, 200, 500, 200, 500);
		m_wordsPanel.add(m_wordListScrollPane);


		// The text field where the user types words
		m_wordEntryField = new JTextField(12);
		setComponentSizes(m_wordEntryField, 140, 20, 200, 20, 200, 24);
		m_wordEntryField.addKeyListener(this);
		m_wordEntryField.addMouseListener(this);
		m_wordEntryFields.add(m_wordEntryField);
		m_wordsPanel.add(m_wordEntryFields.get(0));		
		
		m_wordLabelList = new WordLabelList(m_model, m_wordListPanel);
		this.addComponentListener(m_wordLabelList);
		m_wordLabelLists.add(m_wordLabelList);
        m_wordsPanel.addComponentListener(m_wordLabelLists.get(0));
        
        //Buttons for navigating word lists
        //nextWord = new BasicArrowButton(BasicArrowButton.EAST);
       // prevWord = new BasicArrowButton(BasicArrowButton.WEST);
        JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new GridLayout(1, 2, 10, 10));
		/*prevWord.setName(PREVIOUS_BUTTON);
		nextWord.setName(NEXT_BUTTON);
		prevWord.addMouseListener(this);
		nextWord.addMouseListener(this);*/
		m_newWordList = new JButton("+");
		m_newWordList.setName(NEW_WORDLIST_BUTTON);
		m_newWordList.addMouseListener(this);
		//innerPanel.add(prevWord);
		//innerPanel.add(nextWord);
		innerPanel.add(m_newWordList);
		m_wordsPanel.add(innerPanel);
		m_wordsPanels.add(m_wordsPanel);
		m_wordsPanels.get(m_Windex).addMouseListener(this);
	}
	
	private void initPuzzleButtonPanel() {
		final int MAX_BUTTON_SIZE = 82;
		
		m_puzzleButtonPanel = new JPanel();
		m_puzzleButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		m_puzzleButtonPanel.setLayout(new BoxLayout(m_puzzleButtonPanel, BoxLayout.Y_AXIS));
		setComponentSizes(m_puzzleButtonPanel, 200, 100, 200, 150, 200, 200);
		m_puzzleButtonPanel.addMouseListener(this);
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new GridBagLayout());
		
		JRadioButton Ws = new JRadioButton();
		JLabel lblWordSearch = new JLabel(new ImageIcon("res/wordsearch.png"));
		//btnWordSearch.addMouseListener(this);
		//btnWordSearch.setName(WORD_SEARCH_BUTTON);
		Ws.addMouseListener(this);
		Ws.setName(WORD_SEARCH_BUTTON);
		Ws.setSelected(!m_generateCrossword);
		
		innerPanel.add(Ws);
		innerPanel.add(lblWordSearch);
		
		JRadioButton Cw = new JRadioButton();
		JLabel lblCrossword= new JLabel(new ImageIcon("res/crossword.png"));
		//btnCrossWord.addMouseListener(this);
		//btnCrossWord.setName(CROSSWORD_BUTTON);
		Cw.addMouseListener(this);
		Cw.setName(CROSSWORD_BUTTON);
		Cw.setSelected(m_generateCrossword);
		innerPanel.add(Cw);
		innerPanel.add(lblCrossword);
		
		ButtonGroup group = new ButtonGroup();
		group.add(Ws);
		group.add(Cw);
		
		m_StartStopButton = new JButton("Start");
		m_StartStopButton.addMouseListener(this);
		m_StartStopButton.setName(STOP_BUTTON);
		innerPanel.add(m_StartStopButton);
		// Minimum and preferred sizes don't need to be set since 
		// m_horizontalSplit.getBottomComponent()'s minimum size is set.
		innerPanel.setMaximumSize(new Dimension((MAX_BUTTON_SIZE * 4) + 20, MAX_BUTTON_SIZE));

		m_puzzleButtonPanel.add(Box.createVerticalGlue());
		m_puzzleButtonPanel.add(innerPanel);
		m_puzzleButtonPanel.add(Box.createVerticalGlue());
	}
	/** @author Alex */
	private void initTabPane(){
		m_Tabs = new JTabbedPane();
		m_Tabs.addMouseListener(this);
		m_Tabs.addTab("Tab " + (m_Tabs.getTabCount()+1), m_wordsPanels.get(0));
		setComponentSizes(m_Tabs, 200, 200, 200, 500, 200, 500);
		add(m_Tabs);
		m_Tabs.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
		    	updateTab();
		    	m_model.getFirstWordPuzzle();
		    	m_puzzleIndex = 1;
		    	updatePuzzlePanel();
		    }
		});
	}
	/** @author Samuel Wiley */
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
		innerPanel.setName(PUZZLE_BUTTONS);
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
		m_lblPuzzleIndex.setText(m_puzzleIndex + "/" + m_model.getNumPuzzles());
		innerPanel.add(prevWordPZL);
		innerPanel.add(m_lblPuzzleIndex);
		innerPanel.add(nextWordPZL);
		innerPanel.setMaximumSize(new Dimension((MAX_BUTTON_SIZE * 2) + 10, MAX_BUTTON_SIZE));
		m_puzzlePanel.add(innerPanel);
		m_puzzlePanel.add(Box.createVerticalGlue());		
		m_puzzleDisplayPanel = createPuzzlePanel();
		m_puzzlePanel.add(m_puzzleDisplayPanel);
		m_puzzlePanel.add(Box.createVerticalGlue());
		m_puzzlePanel.validate();
	}
	
	private JPanel createPuzzlePanel() {
		JPanel panel = new JPanel();
		Puzzle puzzle = m_model.getPuzzle();
		if (puzzle == null) {
			m_puzzleIndex = 0;
			m_lblPuzzleIndex.setText("0/0");
			return panel;
		}
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
				puzzle.applyCellStyle(cell,m_chkBoxShowSolutions.isSelected());
				cell.setInheritsPopupMenu(true);
				panel.add(cell);
			}
		}

		panel.setComponentPopupMenu(m_puzzlePopupMenu);
		return panel;
	}
	
	/** Saves a wordList to a txt file */
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
/** Takes a txt file and outputs the filtered contents as a wordList */
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

	private JCheckBoxMenuItem createCheckBoxMenuItem(String label, int mnemonic, String description, String actionCommand, boolean selected) {
		JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem(label);
		cbMenuItem.setMnemonic(mnemonic);
		cbMenuItem.getAccessibleContext().setAccessibleDescription(description);
		cbMenuItem.setSelected(selected);
		cbMenuItem.setActionCommand(actionCommand);
		cbMenuItem.addActionListener(this);
		return cbMenuItem;
	}
	
	
	/** Shows the puzzle size dialog and updates the text field values to reflect the program's current values. 
	 * 
	 * @author Samuel Wiley */
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
	 * @author Samuel Wiley
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
	
	/** @author Samuel Wiley */
	private void showInvalidSizeEntryDialog(String message) {
		JOptionPane.showMessageDialog(m_puzzleSizeDialog, message, "Invalid Entry", JOptionPane.ERROR_MESSAGE);
	}
	
	/** @author Samuel Wiley */
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
	
	/** Shows about dialog box. */
	private void showAboutDialog(){
		if (m_aboutDialog == null) {
			String aboutMessage = "Iteration 2";
			aboutMessage = "<html>" + aboutMessage + "<br><br>Max memory: " + ((Runtime.getRuntime().maxMemory()) / 1048576) + "MB<br>";
			aboutMessage += "Total memory: " + ((Runtime.getRuntime().totalMemory()) / 1048576) + "MB<br>";
			aboutMessage += "Free memory: " + ((Runtime.getRuntime().freeMemory()) / 1048576) + "MB";
			
			m_aboutDialog = new JDialog(this, "About");
			m_aboutDialog.add(new JLabel(aboutMessage));
//			m_aboutDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			m_aboutDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // To refresh the memory numbers.
		}
		m_aboutDialog.setSize(300, 200);
		m_aboutDialog.setLocationRelativeTo(this);
		m_aboutDialog.setVisible(true);
	}
	
	/** Sets the {@link Component#setMinimumSize(Dimension) minimum}, 
	 * {@link Component#setPreferredSize(Dimension) preferred}, and 
	 * {@link Component#setMaximumSize(Dimension) maximum} sizes of the given {@code Component}. 
	 * 
	 * @author Samuel Wiley
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
/*		case Constants.DELETE_WORD_LABEL:
			System.err.println("DELETE_WORD_LABEL NOT UNUSED");
			// This method also deletes the word from the model's list.
			if (!m_wordLabelList.deleteSelectedWord()) {
				System.err.println("Failed to delete word label");
			}
			break;
		
		case MenuCommand.SAVE_WORDLIST:
			saveFile(m_model.getWordList());
			break;
			*/
		case MenuCommand.EXPORT:
			
			JFileChooser dlgSave;
			dlgSave = new JFileChooser ();
			
			FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV File", "csv", "CSV");
			dlgSave.addChoosableFileFilter(filter);
			filter = new FileNameExtensionFilter("TXT File", "txt", "TXT");
			dlgSave.addChoosableFileFilter(filter);
			filter = new FileNameExtensionFilter("HTML File", "html", "HTML");
			dlgSave.addChoosableFileFilter(filter);
			
			File file_key;
			File file_puzzle;
			String path;
			String extension;
			int value = dlgSave.showSaveDialog(m_wordsPanel);
	
			if (value == JFileChooser.APPROVE_OPTION){ 
	            path = dlgSave.getSelectedFile().getAbsolutePath();
	            FileFilter chosenFilter = dlgSave.getFileFilter();
	            if(chosenFilter.getDescription() == "CSV File"){
	            	System.out.println("CSV BITCHES");
	            	extension = ".csv";
	            }
	            else if(chosenFilter.getDescription() == "HTML File"){
	            	System.out.println("HTML ASSWIPE");
	            	extension = ".html";	            	
	            }
	            else{
	            	System.out.println("TXT MOTHERFUCKR");
	            	extension = ".txt";
	            }
	            file_key = new File(path+"-key"+extension);
	            file_puzzle = new File(path+"-puzzle"+extension);
	            			 
				if(!file_key.exists()) {
					try {
						file_key.createNewFile();
					} catch (IOException ioe) {
						ioe.printStackTrace();
						return;
					}
				}else if(!file_puzzle.exists()){
					try {
						file_puzzle.createNewFile();
					} catch (IOException ioe) {
						ioe.printStackTrace();
						return;
					}
				}
			
				BufferedWriter file_keyOutput = null;
				BufferedWriter file_puzzleOutput = null;

				try {
					file_keyOutput = new BufferedWriter(new FileWriter(file_key));
					file_puzzleOutput = new BufferedWriter(new FileWriter(file_puzzle));
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				
				if(file_keyOutput != null) {
					String newLine = System.getProperty("line.separator");
					//m_model.getWordList();
					String puzzle = m_model.getPuzzle().toString();
					String toWrite_key = "";
					String toWrite_puzzle = "";
	
					String[] linesplit = puzzle.split("\n");
					StringBuilder csvsoln = new StringBuilder();
					StringBuilder csvpuz = new StringBuilder();
					StringBuilder txtsoln = new StringBuilder();
					StringBuilder txtpuz = new StringBuilder();
					StringBuilder htmlsoln = new StringBuilder();
					StringBuilder htmlpuz = new StringBuilder();
					htmlsoln.append("<!DOCTYPE html><html lang='en'><head><meta charset='utf-8'>"+newLine+"<title>Puzzle - Key</title></head>"+newLine+"<body><table>");
					htmlpuz.append("<!DOCTYPE html><html lang='en'><head><meta charset='utf-8'>"+newLine+"<title>Puzzle - Blank</title></head>"+newLine+"<body><table>");
// to consider to use
// https://code.google.com/p/puz/source/browse/trunk/static/crossword.css?r=27
					//puzzle
					for (int i = 0; i < linesplit.length; i++) {
						htmlpuz.append(newLine+"<tr>");
						htmlsoln.append(newLine+"<tr>");
						String[] tokensplit = linesplit[i].split(" ");
						for(int j = 0; j < tokensplit.length; j++){
							String token = tokensplit[j];
							if(token.length() > 0){
								if(token.length() == 1){
									if(token.equalsIgnoreCase("*")){ 
										csvpuz.append(token+",");
										txtpuz.append(token+" ");	
										htmlpuz.append(newLine+"<td>"+token+"</td>");//consider changing to styling
									}
									else{
										csvpuz.append("?,");
										txtpuz.append("? ");
										htmlpuz.append(newLine+"<td>?</td>");//consider changing to styling
									}
									htmlsoln.append(newLine+"<td>"+token+"</td>");
								}
								else{//puzzle type
									csvpuz.append(token);
									txtpuz.append(token);
								}
								csvsoln.append(token+",");
								txtsoln.append(token+" ");
							}							
						}
						csvpuz.deleteCharAt(csvpuz.length()-1);
						csvsoln.deleteCharAt(csvsoln.length()-1);
					
						csvsoln.append(newLine);
						csvpuz.append(newLine);
							
						txtsoln.append(newLine);
						txtpuz.append(newLine);
						htmlpuz.append(newLine+"</tr>");
						htmlsoln.append(newLine+"</tr>");
					}
					htmlpuz.append(newLine+"</table>");
					htmlsoln.append(newLine+"</table>");
					
					//wordlist
					csvpuz.append(newLine+"Word List:"+newLine);
					csvsoln.append(newLine+"Word List:"+newLine);					
					txtpuz.append(newLine+"Word List:"+newLine);
					txtsoln.append(newLine+"Word List:"+newLine);
					htmlpuz.append(newLine+"<strong><p>Word List:</p></strong>");
					htmlsoln.append(newLine+"<strong><p>Word List:</p></strong>");
					ArrayList<String> word_list = m_model.getWordList();
					for(int i=0; i<word_list.size(); i++){
						String word = word_list.get(i);
						
						csvpuz.append(word+newLine);
						csvsoln.append(word+newLine);
						
						txtpuz.append(word+newLine);
						txtsoln.append(word+newLine);
						
						htmlpuz.append(newLine+"<p>"+word+"</p>");
						htmlsoln.append(newLine+"<p>"+word+"</p>");
					}
					
					/*csvsoln.insert(0, "==Puzzle Key=="+newLine);
					htmlsoln.insert(0, "==Puzzle Key=="+newLine);
					txtsoln.insert(0, "==Puzzle Key=="+newLine);
*/
					htmlpuz.append(newLine+"</body></html>");
					htmlsoln.append(newLine+"</body></html>");
						
			

					String solution = "";
					String blank = "";
					if(extension == ".csv"){
						solution = csvsoln.toString();
						blank = csvpuz.toString();							
					}else if(extension == ".html"){
						solution = htmlsoln.toString();
						blank = htmlpuz.toString();
					}else{
						solution = txtsoln.toString();
						blank = txtpuz.toString();
					}
						
					toWrite_key = solution;
					toWrite_puzzle = blank;
					
					try {
						file_keyOutput.write(toWrite_key);
						file_keyOutput.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					try {
						file_puzzleOutput.write(toWrite_puzzle);
						file_puzzleOutput.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
		
				}
				
				System.err.println("File written successfully.");
				break;
			}
			

			case MenuCommand.IMPORT:
				m_model.clearSelectedPuzzle();
				//updatePuzzlePanel();
				//System.err.println(m_model.getWordList().toString());
				m_Windex = m_Tabs.getTabCount();		
				nextWordTab();
				wordDex = m_Tabs.getTabCount()-1;
				m_model.getNewWordList();
				//System.err.println(m_model.getWordList().toString());
				m_Tabs.add("Tab " + (m_Tabs.getTabCount()+1) , m_wordsPanels.get(m_Windex));
				m_Windex++;
				wordDex++;
				importFile();
				updateTab();
				break;
			case MenuCommand.PRINT:
				 PrinterJob job = PrinterJob.getPrinterJob();
		         job.setPrintable(this);
				 PageFormat pf = job.defaultPage();
				 Paper paper = pf.getPaper();
				 paper.setSize(8.5 * 72, 11 * 72);
				 paper.setImageableArea(0.5 * 72, 0.0 * 72, 7.5 * 72, 10.5 * 72);
				 pf.setPaper(paper);
		         Book book = new Book();
		         book.append(this, pf);
		         job.setPageable(book);
		        
		         boolean ok = job.printDialog();
		         if (ok) {
		             try {
		                  job.print();
		                  job.print();
		             } catch (PrinterException ex) {
		             
		             }
		         } 
				break;
			case MenuCommand.SAVE_WORDLIST:
				saveFile(m_model.getWordList());
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
			case MenuCommand.PUZZLE_NON_SQUARE:
				m_model.setAllowNonSquare(m_chkBoxNonSquare.isSelected());
				break;
			case MenuCommand.ABOUT:
				showAboutDialog();
				break;
			case MenuCommand.PUZZLE_SHOW_SOLUTIONS:
				Object source = e.getSource();
				if (source instanceof JCheckBoxMenuItem) {
					m_puzzleShowSolutions = ((JCheckBoxMenuItem)source).isSelected();
				}
				m_chkBoxShowSolutions.setSelected(m_puzzleShowSolutions);
				m_popupChkBoxShowSolutions.setSelected(m_puzzleShowSolutions);
				updatePuzzlePanel();
				break;
		default:
			System.err.println("Unrecognized command: " + command);
			break;
	
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//updateTab();
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			String word = m_wordEntryFields.get(m_Tabs.getSelectedIndex()).getText();
			if(m_model.getPuzzle()!=null) {
				  ArrayList<String> old=m_model.getWordList();
				  m_model.getNewWordList();
				  for(String s : old) {
					  m_model.addWord(s);
				  }
				  m_wordLabelLists.get(m_Tabs.getSelectedIndex()).changeToWordList(m_model.getWordList());
				 // m_model.clearSelectedPuzzle();
			}
			if (m_model.addWord(word)) {
				m_wordLabelLists.get(m_Tabs.getSelectedIndex()).addWord(word);
				m_wordEntryFields.get(m_Tabs.getSelectedIndex()).setText("");
			}
			
		}
		else if (e.getKeyCode() == KeyEvent.VK_F7) {
			System.err.println("Printing model's data tree.");
			m_model.printTreeMap();
		}
		else if (e.getKeyCode() == KeyEvent.VK_F8) {
			System.err.println("Get new word list.");
			m_wordLabelLists.get(m_Tabs.getSelectedIndex()).changeToWordList(m_model.getNewWordList());
		}
		else if (e.getKeyCode() == KeyEvent.VK_F11) {
			System.err.println("Get previous word list.");
			m_wordLabelLists.get(m_Tabs.getSelectedIndex()).changeToWordList(m_model.getPreviousWordList());
		}
		else if (e.getKeyCode() == KeyEvent.VK_F12) {
			System.err.println("Get next word list.");
			m_wordLabelLists.get(m_Tabs.getSelectedIndex()).changeToWordList(m_model.getNextWordList());
		}
	}
	public void updateTab(){
		System.err.println(m_model.getWordList().toString());
		System.err.println(wordDex + ", " + (m_Tabs.getSelectedIndex()));
		if (wordDex ==  m_Tabs.getSelectedIndex())
			return;
		while(wordDex > m_Tabs.getSelectedIndex()){
			if(wordDex <= 0)
				wordDex = m_Tabs.getTabCount();
			else
				wordDex--;
			System.err.println("Prev");
			m_model.getPreviousWordList();
			
		}//going down tree
		while(wordDex < m_Tabs.getSelectedIndex()){
			if(wordDex >= m_Tabs.getTabCount())
				wordDex = 0;
			else
				wordDex++;
			System.err.println("Next");
			m_model.getNextWordList();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		String componentName = e.getComponent().getName();
		updateTab();
		if (componentName == null) {
			return;
		}
		switch (componentName) {
			
			case WORD_SEARCH_BUTTON:
				m_generateCrossword=false;
				break;
			
			case CROSSWORD_BUTTON:				
				m_generateCrossword=true;
				break;
			case STOP_BUTTON:
				stopStartGeneration();
				 break;
			case PREVIOUS_WORDLIST_BUTTON:
			//	m_wordLabelList.changeTo(m_model.getNextWordList());
//				if(m_model.getCurrentWordPuzzle() != null){
				//	m_puzzleIndex = 1;
				//	updatePuzzlePanel();
//				}
				break;
			case NEXT_WORDLIST_BUTTON:
		//		m_wordLabelList.changeTo(m_model.getNextWordList());
//				if(m_model.getCurrentWordPuzzle() != null){
			//		m_puzzleIndex = 1;
		//			updatePuzzlePanel();
//				}
				break;
				
			case PREVIOUS_PUZZLE_BUTTON:
				m_model.getPreviousPuzzle();
				if(m_puzzleIndex != 1)
					m_puzzleIndex--;
				else
					m_puzzleIndex = m_model.getNumPuzzles();
				updatePuzzlePanel();
				break;
				
			case NEW_WORDLIST_BUTTON:
				//m_wordLabelList.changeToWordList(m_model.getNewWordList());
				m_model.clearSelectedPuzzle();
				//updatePuzzlePanel();
				//System.err.println(m_model.getWordList().toString());
				m_Windex = m_Tabs.getTabCount();		
				nextWordTab();
				wordDex = m_Tabs.getTabCount()-1;
				m_model.getNewWordList();
				//System.err.println(m_model.getWordList().toString());
				m_Tabs.add("Tab " + (m_Tabs.getTabCount()+1) , m_wordsPanels.get(m_Windex));
				m_Windex++;
				wordDex++;
				updateTab();
				break;
			case NEXT_PUZZLE_BUTTON:
				m_model.getNextPuzzle();
				if(m_puzzleIndex >= m_model.getNumPuzzles())
					m_puzzleIndex = 1;
				else
					m_puzzleIndex++;
				
				updatePuzzlePanel();
				
				break;	
			//case "puzzleDisplayPanel":
		//		m_puzzlePopupMenu.show(e.getComponent(), e.getX(), e.getY());
			//	break;
			default:
				System.err.println("Unhandled mouse click: " + e.getComponent().getClass().getName());
				break;
		}
	}
	
	private void stopStartGeneration() {
		if(m_model.isPuzzleGeneratorRunning()) {
			toggleButtonActivation(true);
			m_model.stopPuzzleGenerator();
		}
		else {
			toggleButtonActivation(false);
			//TODO This needs to be converted over to work with Tabs
			//	ArrayList<String> old=m_model.getWordList();
			//	m_model.getNewWordList();
			//	for(String s : old) {
			//		m_model.addWord(s);
			//	}
			//	m_wordLabelList.changeTo(m_model.getWordList()); // except we should call this after startPuzzleGenerator in case the Model made a new word list
			//	updatePuzzlePanel();
			if (!m_model.getWordList().isEmpty()) {
				if(m_generateCrossword) {
					m_model.startPuzzleGenerator(Constants.TYPE_CROSSWORD);
				}
				else {
					m_model.startPuzzleGenerator(Constants.TYPE_WORDSEARCH);
				}
				runSolutionMonitor();
			}
		
		}
		
	}
	
	private void toggleButtonActivation(boolean activate) {
		
		//FIXME Getting these by index seems REALLY brittle.  Solutions welcome
		
		//Puzzle buttons
		JPanel PuzzleButtons=(JPanel)m_puzzlePanel.getComponent(0);
		PuzzleButtons.getComponent(0).setEnabled(activate);
		PuzzleButtons.getComponent(1).setEnabled(activate);
		PuzzleButtons.getComponent(2).setEnabled(activate);
		if(activate) {
			m_StartStopButton.setText("Start");
		}
		else {
			m_StartStopButton.setText("Stop");
		}
	}

	private void runSolutionMonitor() {
		m_puzzleIndex = 1;
		Thread puzzlePanelUpdater = new Thread() {
			@Override
			public void run() {
				Puzzle displayPuzzle = null;
				int solutionsSize = 0;
				
				while (m_model.isPuzzleGeneratorRunning()) {
					if (displayPuzzle != m_model.getFirstWordPuzzle()) {
						displayPuzzle = m_model.getFirstWordPuzzle();
						if (displayPuzzle != null) {
							updatePuzzlePanel();
						}
					}
					else if (displayPuzzle != null && solutionsSize != m_model.getNumPuzzles()) {
						m_lblPuzzleIndex.setText(m_puzzleIndex + "/" + m_model.getNumPuzzles());
					}
					
					
					try {
						sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				if (displayPuzzle != m_model.getFirstWordPuzzle()) {
					displayPuzzle = m_model.getFirstWordPuzzle();
					if (displayPuzzle != null) {
						updatePuzzlePanel();
					}
				}
				else if (displayPuzzle != null && solutionsSize != m_model.getNumPuzzles()) {
					m_lblPuzzleIndex.setText(m_puzzleIndex + "/" + m_model.getNumPuzzles());
				}
				toggleButtonActivation(true);
			}
		};
		
		puzzlePanelUpdater.start();
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

	@Override
	public int print(Graphics g, PageFormat pf, int page)
			throws PrinterException {
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        
        if(pageNumber == 0)
        {
        	m_puzzlePanel.printAll(g);
        	pageNumber++;
        }
        else if(pageNumber == 1)
        {
        	m_wordsPanel.printAll(g);
        	pageNumber--;
        }
		return 0;
	}
	
	
}
