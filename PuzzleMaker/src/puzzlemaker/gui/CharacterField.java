package puzzlemaker.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class CharacterField extends JTextField {
	private static final long serialVersionUID = 1L;
	
	public CharacterField(char chr) {
		super (Character.toString(Character.toUpperCase(chr)), 1);
		this.setHorizontalAlignment(JTextField.CENTER);
		this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.setDisabledTextColor(Color.black);
		
		this.setForeground(Color.black);
//		this.set
		
		this.setEnabled(false);
	}
	
	public void setText(char chr) {
		this.setText(Character.toString(chr));
	}
	
}
