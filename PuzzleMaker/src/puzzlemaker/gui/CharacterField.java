package puzzlemaker.gui;

import javax.swing.JTextField;

public class CharacterField extends JTextField {
	private static final long serialVersionUID = 1L;
	
	public CharacterField() {
		super("W", 1);
		this.setSize(20, 20);
	}
	
	/**
	 * Enforce only a single character being entered.
	 */
	
}
