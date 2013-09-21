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
	
	/**
	 * Initializes the main {@link javax.swing.JFrame window} and its underlying {@link javax.swing.JSplitPane split pane}. 
	 * 
	 * @param controller Passed in to get references to GUI Panels:<br>
	 * {@linkplain PController#getPuzzlePanel()},<br>
	 * {@linkplain PController#getWordListPanel()},<br>
	 * {@linkplain PController#getPuzzleButtonPanel()} 
	 */
	public PView(PController controller) {		
		m_controller = controller;
		
        // Initialize the window.
		this.setTitle("Crossword");
		this.setSize(new Dimension(640, 640));
		this.setMinimumSize(new Dimension(480, 480));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setJMenuBar(m_controller.getMenuBar());
        
        // Initialize the split pane.
        m_horizontalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, m_controller.getWordListPanel(), m_controller.getPuzzleButtonPanel());
        m_horizontalSplit.setResizeWeight(0.5f);
        m_horizontalSplit.getBottomComponent ().setMinimumSize (new Dimension(200, 100)); // Prevents button icons from needing resizing.
        
        m_verticalSplit=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, m_controller.getPuzzlePanel(), m_horizontalSplit);
        m_verticalSplit.setResizeWeight(0.8f);
        
        // Send notifications to WordList on resize so WordList can update its layout.
        this.addComponentListener(m_controller.getWordList());
        m_horizontalSplit.getTopComponent().addComponentListener(m_controller.getWordList());
        
        // Add the split pane to the window and show the window.
        this.getContentPane().add(m_verticalSplit);
        this.pack();
        this.setVisible(true);
	}
}
