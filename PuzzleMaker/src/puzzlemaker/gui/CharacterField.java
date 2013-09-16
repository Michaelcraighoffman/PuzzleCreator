package puzzlemaker.gui;

import javax.swing.JTextField;

public class CharacterField extends JTextField {
	private static final long serialVersionUID = 1L;
	
	public CharacterField(char chr) {
		super (Character.toString(Character.toUpperCase(chr)), 1);
		this.setHorizontalAlignment(JTextField.CENTER);
	}
	
	public void setText(char chr) {
		this.setText(Character.toString(chr));
	}
	
}
