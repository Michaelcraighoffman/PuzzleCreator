package puzzlemaker;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import puzzlemaker.gui.View;

public class Controller {
	
	public static void main(String[] args) {
		// Set the look and feel of the program to the OS defaults.
		// TODO: if (os == linux) { getCrossPlatform...} else {systemlookandfeel}
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
				
		 
		View mainWindow = new View();
		mainWindow.setVisible(true);
	}
}
