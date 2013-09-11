package puzzlemaker;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTextField;

import puzzlemaker.gui.PView;
import puzzlemaker.puzzles.*;

public class PuzzleMaker {

	private Puzzle m_puzzle;
	
	public static void main(String[] args) {
		 PController controller = new PController();
		 
		 PView mainWindow = new PView(controller);
		 mainWindow.setSize(640, 640);
		 mainWindow.setVisible(true);

	}
}
