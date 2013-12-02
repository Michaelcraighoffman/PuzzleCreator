package puzzlemaker.puzzles;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;

import puzzlemaker.Constants;
import puzzlemaker.tools.grid.Grid;

public class Crossword extends Puzzle {
	
	private static final Border m_border = BorderFactory.createLineBorder(Color.black, 2);
	
	LinkedBlockingQueue<Grid> m_solutions;
	ForkJoinPool m_threadPool;
	
	public Crossword(Grid grid, ArrayList<Word> wordList) {
		m_grid = grid;
		m_wordList = wordList;
	}

	@Override
	public void applyCellStyle(JTextField cell, boolean showSolution) {
		cell.setHorizontalAlignment(JTextField.CENTER);
		cell.setBorder(m_border);
		cell.setDisabledTextColor(Color.black);
		cell.setForeground(Color.black);
		cell.setEnabled(false);
			
		
		if (cell.getText().equals(Character.toString(Constants.EMPTY_CELL_CHARACTER))) {
			cell.setBackground(Color.black);
		}
		else {
			cell.setBackground(Color.white);
		}
		
		if(showSolution) {
			cell.setFont(cell.getFont().deriveFont(Font.BOLD));
		}
		else {
			cell.setText("");
		}
		
	}
}
