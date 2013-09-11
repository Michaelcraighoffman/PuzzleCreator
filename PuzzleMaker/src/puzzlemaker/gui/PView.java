package puzzlemaker.gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSplitPane;

import puzzlemaker.PController;

public class PView extends JFrame {

	private PController m_controller;
	
	public PView(PController controller) {
//        super("Crossword");
		
		m_controller = controller;
		
        //Create and set up the window.
		this.setTitle("Crossword");
		this.setSize(new Dimension(640, 640));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setJMenuBar(m_controller.getMenuBar());
        
        //Add the ubiquitous "Hello World" label.
        JLabel PuzzlePlaceholder = new JLabel("Puzzle PlaceHolder");
        JLabel WordlistPlaceholder = new JLabel("Wordlist PlaceHolder");
        JLabel ButtonsPlaceholder = new JLabel("Buttons PlaceHolder");
        //frame.getContentPane().add(label);
        
//        JSplitPane rightSplit=new JSplitPane(JSplitPane.VERTICAL_SPLIT, WordlistPlaceholder, ButtonsPlaceholder);
        JSplitPane rightSplit=new JSplitPane(JSplitPane.VERTICAL_SPLIT, m_controller.getWordsPanel(), ButtonsPlaceholder);
        rightSplit.setResizeWeight(0.6f);
        
        JSplitPane mainSplit=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, PuzzlePlaceholder, rightSplit);
        mainSplit.setResizeWeight(0.8f);
        
        this.getContentPane().add(mainSplit);

        //Display the window.
        this.pack();

        this.setVisible(true);
	}
}
