package puzzlemaker;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import puzzlemaker.gui.PView;

public class PuzzleMaker {
	
	public static void main(String[] args) {
		// Set the look and feel of the program to the OS defaults.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
				
		PController controller = new PController();
		 
		PView mainWindow = new PView(controller);
		mainWindow.setSize(640, 640);
		mainWindow.setVisible(true);
	}
}
