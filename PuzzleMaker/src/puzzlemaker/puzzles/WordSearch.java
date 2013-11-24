package puzzlemaker.puzzles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JTextField;

import puzzlemaker.tools.grid.Grid;

public class WordSearch extends Puzzle {
	
	LinkedBlockingQueue<Grid> m_solutions;
	Random rand = new Random();
	
	public WordSearch(Grid grid, ArrayList<Word> wordList) {
		m_grid = grid;
		m_wordList = wordList;
	}

	public WordSearch(ArrayList<String> wordList) {
		// Find out how big the puzzle has to be
		// TODO: This starting value may not be necessary with a sophisticated
		// generate() algorithm.
		int minSize = wordList.size();
		for (String word : wordList) {
			if (word.length() > minSize) {
				minSize = word.length();
			}
		}
		System.err.println("Wordsearch minsize = " + minSize);

		// "New" all of the CharacterFields of m_grid
		minSize = minSize + 2;
		//m_grid = new Grid(minSize, minSize);
		
		updateDisplayPanel();
	}
	
	
	
	//private void eraseBoundaries() {
		//m_grid.removeRow(1);
		//m_grid.removeRow(m_grid.size());
	//}
	
	/** Fill in remaining grid spaces with random letters. */
//	private void fillIn(){
//		
//		
//		for(int x = 0; x < m_grid.getWidth(); ++x){
//			for(int y = 0; y < m_grid.getHeight(); ++y){
//				if (m_grid.getCharAt(x, y) == ' '){
//					m_grid.setCharAt(x, y, (char) ();
//				}
//			}
//		}
//	}
	
	
	private void placeWord(String word , int caseDirection, int x , int y){
		
		for(int i = 0; i < word.length(); ++i){
		switch(caseDirection){
		
		case 0:
			m_grid.setCharAt(x - i , y , word.charAt(i));
			break;
		
		case 1:
			m_grid.setCharAt(x - i , y+i , word.charAt(i));
			break;
		
		case 2:
			m_grid.setCharAt(x , y+i , word.charAt(i));
			break;
			
		case 3:
			m_grid.setCharAt(x + i , y+i , word.charAt(i));
			break;
			
		case 4:
			m_grid.setCharAt(x + i , y , word.charAt(i));
			break;
			
		case 5:
			m_grid.setCharAt(x + i , y-i , word.charAt(i));
			break;
			
		case 6:
			m_grid.setCharAt(x , y-i , word.charAt(i));
			break;
			
		case 7:
			m_grid.setCharAt(x - i , y-i , word.charAt(i));
			break;
		}
		}
	}

	@Override
	public void makeSquare() {
		int difference = m_grid.getWidth()-m_grid.getHeight();
		if(difference==0) return;
		while(difference > 0) {
			switch(difference%2){
				case 0:
					m_grid.addRowOnBottom();
					break;
				case 1:
					m_grid.addRowOnTop();
					break;
			}
			difference--;
		}
		if(difference==0) {
			return;
		}
		while(difference < 0) {
			switch(Math.abs(difference%2)){
				case 0:
					m_grid.addColumnOnLeft();
					break;
				case 1:
					m_grid.addColumnOnRight();
					break;
			}
			difference++;
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
