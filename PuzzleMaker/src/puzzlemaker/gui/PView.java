package puzzlemaker.gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import puzzlemaker.PController;

@SuppressWarnings("serial")
public class PView extends JFrame {

	private PController m_controller;
	
	private JSplitPane m_verticalSplit;
	private JSplitPane m_horizontalSplit;
	
	public PView(PController controller) {		
		m_controller = controller;
		
        //Create and set up the window.
		this.setTitle("Crossword");
		this.setSize(new Dimension(640, 640));
		this.setMinimumSize(new Dimension(480, 480));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setJMenuBar(m_controller.getMenuBar());
        
        //Add the ubiquitous "Hello World" label.
//        JLabel PuzzlePlaceholder = new JLabel("Puzzle PlaceHolder");
//        JLabel WordlistPlaceholder = new JLabel("Wordlist PlaceHolder");
//        JLabel ButtonsPlaceholder = new JLabel("Buttons PlaceHolder");
        //frame.getContentPane().add(label);
        
        m_horizontalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, m_controller.getWordListPanel(), m_controller.getPuzzleButtonPanel());
        m_horizontalSplit.setResizeWeight(0.5f);
        
        m_verticalSplit=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, m_controller.getPuzzlePanel(), m_horizontalSplit);
        m_verticalSplit.setResizeWeight(0.8f);
        
        this.getContentPane().add(m_verticalSplit);
        this.pack();
        
    	// Display the window.
        this.setVisible(true);
	}
}
