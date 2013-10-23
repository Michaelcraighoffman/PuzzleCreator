package puzzlemaker.puzzles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
	
	public Crossword(ArrayList<String> wordList) {
		m_grid = new Grid(8, 8);
		m_wordList = new ArrayList<Word>();
		for (String s : wordList) {
			m_wordList.add(new Word(s));
		}

		m_solutions = new LinkedBlockingQueue<Grid>();
		m_threadPool = new ForkJoinPool();
		
		System.err.println("Word list: " + m_wordList.toString());
		
		System.err.println("Working...");
		
		try {
			m_threadPool.awaitTermination(3, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.err.println("End Output");
		
		m_grid = m_solutions.peek();
		
		// Add m_grid's CharacterFields to m_displayPanel
		updateDisplayPanel();
	}

	@Override
	public void showSolution() {
		
	}

	@Override
	public void hideSolution() {
		
	}

	@Override
	public void applyCellStyle(JTextField cell) {
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
	}
}
