package puzzlemaker.model;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class PModel {
	private PModel() {}
	
	
	public static boolean filterToValidWord(String word) {
		
		
		return true;
	}
	
	public static JMenu createTopLevelMenu(String label, int mnemonic, String description) {
		JMenu menu = new JMenu(label);
		menu.setMnemonic(mnemonic);
		menu.getAccessibleContext().setAccessibleDescription(description);
		return menu;
	}

	public static JMenuItem createSubMenuItem(String label, int mnemonic, String description, KeyStroke shortcut) {
		JMenuItem menuItem = new JMenuItem(label, mnemonic);
		menuItem.setAccelerator(shortcut);
		menuItem.getAccessibleContext().setAccessibleDescription(description);
		return menuItem;
	}

	public static void addToWordList(JTextField field, JPanel panel) {
		String word = field.getText();
		field.setText("");
		
		word = word.trim().toUpperCase();
		word = filterToLetters(word);
		
		if (word.length() == 0) {
			return;
		}
		else {
			JLabel wordLabel = new JLabel(word);
			panel.add(wordLabel);
			panel.validate();
		}
	}
	
 	private static String filterToLetters(String word) {
		char[] input = word.toCharArray();
		String output = "";
		
		for (char c : input) {
			if (Character.isLetter(c)) {
				output = output + c;
			}
		}
		return output;
	}
}
