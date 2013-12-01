package puzzlemaker.puzzles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JTextField;

import puzzlemaker.Constants;
import puzzlemaker.tools.grid.Grid;

public class WordSearch extends Puzzle {
	
	LinkedBlockingQueue<Grid> m_solutions;
	Random rand = new Random();
	
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
					m_grid.setCharAt(x, y, (char) (r.nextInt(26) + 65));
				}
			}
		}
	}

	@Override
	public void applyCellStyle(JTextField cell, Boolean showSolution) {
		cell.setHorizontalAlignment(JTextField.CENTER);
		if(showSolution) {
			if(!cell.getText().equals(" "))
			{
				cell.setBackground(Color.yellow);
			}
		}
		if(cell.getText().equals(" "))
		{
			cell.setText(Character.toString((char)(rand.nextInt(26) + 65)));
		}
		
	}
}
