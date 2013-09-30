package puzzlemaker;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import puzzlemaker.gui.View;

public class Controller {
	
	public static void main(String[] args) {
		// Set the look and feel of the program.
		String osName = System.getProperty("os.name");
	                
		try {
			if (osName.equals("Linux")) { // JSplitPane bars look weird (invisible) on Linux...
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}
			else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
				
		 
		View mainWindow = new View();
		mainWindow.setVisible(true);
	}
}
