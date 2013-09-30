package puzzlemaker.tools;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import puzzlemaker.Constants;

public class Model {
	private Model() {}
	
	/** Instantiates and initializes the window's menu bar. */
	public static JMenuBar initMenuBar(MouseListener mouseListener) {
		JMenuBar menuBar = new JMenuBar();		
		JMenu menu;
		
		// "File" 
		menu = createTopLevelMenu("File", KeyEvent.VK_F, "This contains basic functions for the project");
		menu.addMouseListener(mouseListener);
		menu.add(createMenuItem("Open", KeyEvent.VK_O, "Open a preexisting project", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK), Constants.IMPORT));
		menu.add(createMenuItem("Save Puzzle", KeyEvent.VK_U, "Save the current puzzle", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), null));
		menu.add(createMenuItem("Save Word List", KeyEvent.VK_L, "Save the current word list", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK), Constants.SAVE_WORDLIST));
		menu.add(createMenuItem("Export...", KeyEvent.VK_E, "Export puzzle or word list to...", KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK), null));
		menu.add(createMenuItem("Print", KeyEvent.VK_P, "Print current puzzle view", KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK), null));
		menu.add(createMenuItem("Exit", KeyEvent.VK_X, "Exit", KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK), Constants.EXIT));	
		menuBar.add(menu);
		
		// "Puzzle"
		menu = createTopLevelMenu("Puzzle", KeyEvent.VK_Z, "This contains functions to alter the current puzzle");
		menu.addMouseListener(mouseListener);
		menu.add(createMenuItem("Randomize", KeyEvent.VK_R, "Reorder the current puzzle", KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK), null));
		menu.add(createRadioButtonMenuItem("Show Solution", KeyEvent.VK_K, "Show or hide the puzzle key", false));
		menuBar.add(menu);

		// "Help"
		menu = createTopLevelMenu("Help", KeyEvent.VK_H, "Learn about the program");
		menu.addMouseListener(mouseListener);
		menu.add(createMenuItem("How to Use", KeyEvent.VK_W, "Get help how to use the program", KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK), null));
		menu.add(createMenuItem("About", KeyEvent.VK_A, "Get the current version of the program", KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK), Constants.ABOUT));
		menuBar.add(menu);
		
		return menuBar;
	}
	
	private static JMenu createTopLevelMenu(String label, int mnemonic, String description) {
		JMenu menu = new JMenu(label);
		menu.setMnemonic(mnemonic);
		menu.getAccessibleContext().setAccessibleDescription(description);
		return menu;
	}

	private static JMenuItem createMenuItem(String label, int mnemonic, String description, KeyStroke shortcut, String actionCommand) {
		JMenuItem menuItem = new JMenuItem(label, mnemonic);
		menuItem.setAccelerator(shortcut);
		menuItem.getAccessibleContext().setAccessibleDescription(description);
		if (actionCommand != null) {
			menuItem.setActionCommand(actionCommand);
		}
		return menuItem;
	}
	
	private static JRadioButtonMenuItem createRadioButtonMenuItem(String label, int mnemonic, String description, boolean selected) {
		JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem(label);
		rbMenuItem.setMnemonic(mnemonic);
		rbMenuItem.getAccessibleContext().setAccessibleDescription(description);
		rbMenuItem.setSelected(selected);
		return rbMenuItem;
	}

	/** Creates a JPanel with the specified sizes and {@linkplain MouseListener}.
	  * 
	  * @param mouseListener Pass {@code null} to not add a {@code MouseListener}. */
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
	
	//TODO: how to add a "tab" for jdoc?
	/** Creates a {@linkplain JPopupMenu}. Sets the {@code Action Command} of each
	 * specified {@code menuItem} to a capitalized string equal to the following: NAME_MENUITEM<br>
	 * Example: name = "WordList", menuItem = "Delete", ActionCommand = "WORDLIST_DELETE"
	 * 
	 * @param name The name of the popup menu.
	 * @param menuItems The menu items to add to the popup menu.
	 * @param actionListener The action listener.
	 */
	public static JPopupMenu createPopupMenu(String name, String[] menuItems, ActionListener actionListener) {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem menuItem;
		for (String menuName : menuItems) {
			menuItem = new JMenuItem(menuName);
			menuItem.setActionCommand ((name + "_" + menuName).toUpperCase());
			menuItem.addActionListener(actionListener);
			popupMenu.add(menuItem);
		}
		
		return popupMenu;
	}
	
	/** Sets the {@link Component#setMinimumSize(Dimension) minimum}, 
	 * {@link Component#setPreferredSize(Dimension) preferred}, and 
	 * {@link Component#setMaximumSize(Dimension) maximum} sizes of the given {@link Component component}. */
	public static void setComponentSizes (Component c, int minWidth, int minHeight, int prefWidth, int prefHeight, int maxWidth, int maxHeight) {
		c.setMinimumSize(new Dimension(minWidth, minHeight));
		c.setPreferredSize(new Dimension(prefWidth, prefHeight));
		c.setMaximumSize(new Dimension(maxWidth, maxHeight));
	}
	
	public static JDialog showAboutDialog(JFrame owner, String message){
		JDialog dialog = new JDialog(owner, "About");
		dialog.setSize(100, 100);
		dialog.add(new JLabel(message));
		return dialog;
	}
	
}
