package puzzlemaker;

import puzzlemaker.tools.grid.GridCell;

public class Constants {
	public static final int LEFT_TO_RIGHT = 0;
	public static final int TOPLEFT_TO_BOTTOMRIGHT = 1;
	public static final int TOP_TO_BOTTOM = 2;
	public static final int TOPRIGHT_TO_BOTTOMLEFT = 3;
	public static final int RIGHT_TO_LEFT = 4;
	public static final int BOTTOMRIGHT_TO_TOPLEFT = 5;
	public static final int BOTTOM_TO_TOP = 6;
	public static final int BOTTOMLEFT_TO_TOPRIGHT = 7;
	
	public static final char EMPTY_CELL_CHARACTER = ' ';
	public static final GridCell EMPTY_CELL = new GridCell(EMPTY_CELL_CHARACTER);
	
	public static final String DELETE_WORD_LABEL = "DELETE_WORD_LABEL";
	public static final String IMPORT = "MENU_IMPORT";
	public static final String SAVE_WORDLIST = "MENU_EXPORT_WORDLIST";
	public static final String EXIT = "EXIT";
	public static final String ABOUT = "ABOUT";

}
