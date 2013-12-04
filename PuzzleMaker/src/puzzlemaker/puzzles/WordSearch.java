package puzzlemaker.puzzles;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import puzzlemaker.Constants;
import puzzlemaker.tools.grid.Grid;

public class WordSearch extends Puzzle {
	
	static final int[] m_validDirections = Constants.WORDSEARCH_DIRECTIONS;
	LinkedBlockingQueue<Grid> m_solutions;
	
	public WordSearch(Grid grid, ArrayList<Word> wordList) {
		m_grid = grid;
		m_wordList = wordList;
	}
	
	/** Fill in remaining grid spaces with random letters. */
	public void fillIn() {	
		Random r = new Random();
		for(int x = 0; x < m_grid.getWidth(); ++x){
			for(int y = 0; y < m_grid.getHeight(); ++y){
				if (m_grid.getCharAt(x, y) == Constants.EMPTY_CELL_CHARACTER){
					// By setting these to lower case, we can easily tell which letters are part of the solution.
					m_grid.setCharAt(x, y, (char) (r.nextInt(26) + 97));
				}
			}
		}
	}

	@Override
	public void applyCellStyle(JTextField cell, boolean showSolution) {
		cell.setHorizontalAlignment(JTextField.CENTER);
		cell.setEnabled(false);
		cell.setBorder(LineBorder.createBlackLineBorder());
		
		
		if (!showSolution) {
			cell.setDisabledTextColor(Color.black);
			cell.setFont(cell.getFont().deriveFont(Font.PLAIN));
		}
		else {
			if (cell.getText().charAt(0) < 97) {
				cell.setDisabledTextColor(Color.black);
				cell.setFont(cell.getFont().deriveFont(Font.BOLD));
			}
		}
		cell.setText(cell.getText().toUpperCase());
	}
}
