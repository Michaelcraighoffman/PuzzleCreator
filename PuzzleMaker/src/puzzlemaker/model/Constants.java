package puzzlemaker.model;

public class Constants {
	public static final int LEFT_TO_RIGHT = 0;
	public static final int TOPLEFT_TO_BOTTOMRIGHT = 1;
	public static final int TOP_TO_BOTTOM = 2;
	public static final int TOPRIGHT_TO_BOTTOMLEFT = 3;
	public static final int RIGHT_TO_LEFT = 4;
	public static final int BOTTOMRIGHT_TO_TOPLEFT = 5;
	public static final int BOTTOM_TO_TOP = 6;
	public static final int BOTTOMLEFT_TO_TOPRIGHT = 7;
	
	public static final byte TYPE_CROSSWORD = 0;
	public static final byte TYPE_WORDSEARCH = 1;
	
	public static final int[] CROSSWORD_DIRECTIONS = new int[] {LEFT_TO_RIGHT,
																TOP_TO_BOTTOM};

	public static final int[] WORDSEARCH_DIRECTIONS = new int[] {LEFT_TO_RIGHT,
																TOPLEFT_TO_BOTTOMRIGHT,
																TOP_TO_BOTTOM,
																TOPRIGHT_TO_BOTTOMLEFT,
																RIGHT_TO_LEFT,
																BOTTOMRIGHT_TO_TOPLEFT,
																BOTTOM_TO_TOP,
																BOTTOMLEFT_TO_TOPRIGHT};
	
	public static final char EMPTY_CELL_CHARACTER = ' ';
	
	public static final String DELETE_WORD_LABEL = "DELETE_WORD_LABEL";
	public static final String IMPORT = "MENU_IMPORT";
	public static final String SAVE_WORDLIST = "MENU_EXPORT_WORDLIST";
	public static final String EXIT = "EXIT";
	public static final String ABOUT = "ABOUT";
	
	public static String filterWord(String word) {
		word = filterToLetters(word.trim().toUpperCase());
		
		if (word.isEmpty()) {
			return null;
		}

		return word;
	}
	
	/** @returns The string with all non-letters removed. */
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
