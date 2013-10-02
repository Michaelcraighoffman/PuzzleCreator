package puzzlemaker.puzzles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;

import puzzlemaker.Constants;
import puzzlemaker.tools.grid.Grid;
import puzzlemaker.tools.grid.GridIterator;

public class Crossword extends Puzzle {
	
//	private static final Border m_border = BorderFactory.createEmptyBorder(0, 0, 0, 0);
	private static final Border m_border = BorderFactory.createLineBorder(Color.black, 2);

	LinkedBlockingQueue<Grid> m_solutions;
	ForkJoinPool m_threadPool;
	
	public Crossword(ArrayList<String> wordList) {
		m_grid = new Grid(8, 8);
		m_wordList = new ArrayList<Word>();
		for (String s : wordList) {
			m_wordList.add(new Word(s));
		}
		m_validDirections = new int[]{Constants.LEFT_TO_RIGHT, Constants.TOP_TO_BOTTOM};

		m_solutions = new LinkedBlockingQueue<Grid>();
		m_threadPool = new ForkJoinPool();
		
		System.err.println("Word list: " + m_wordList.toString());
		
		System.err.println("Working...");
		generate();		
		
//		m_threadPool.shutdown();
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
	public void generate() {
		GridSolver gs = new GridSolver(m_grid, m_wordList);
		m_threadPool.execute(gs);
	}

	@Override
	public void showSolution() {
		
	}

	@Override
	public void hideSolution() {
		
	}

 
	class GridSolver extends RecursiveTask<Void> {
		private static final long serialVersionUID = 8591295584899382555L;
		private Grid t_grid;
		private ArrayList<Word> t_wordList;
		
		public GridSolver(Grid grid, ArrayList<Word> wordList) {
			t_grid = grid;

			// Deep copy the word list.
			t_wordList = new ArrayList<Word>(wordList.size());
			
			for (Word w : wordList) {
				t_wordList.add(new Word(w.toString()));
			}
		}
		
		
		/** 
		 * @return The number of valid solutions produced by this task.
		 */
		@Override
		protected Void compute() {
			// If we received a grid with all words already added, then check the grid for legality.
			if (t_wordList.isEmpty()) {
				t_grid.trim();
				
				if (gridIsLegal(t_grid, m_wordList)) { // note that we reference the untouched m_wordList
//					System.err.print("Found a legal grid; checking if unique... ");
					addWithoutDuplicates(t_grid, m_solutions);
					disposeWordList();
					this.complete(null);
					return null;
				}
			}
			// Otherwise, take the next word from the list and fork new solve threads for each legal placement of that word.
			else {
				int wordIndex = 0;
				while (wordIndex < t_wordList.size()) {
					Word currentWord = t_wordList.get(wordIndex);				
					currentWord.findValidPlacements(t_grid);
					
					if (currentWord.hasValidPlacements()) {
						t_wordList.remove(currentWord);
						
	//					System.err.println("Word " + currentWord + " has " + currentWord.getValidPlacements().size() + " valid placements");
	//					int total = 0;
						GridSolver tmpSolver;
						Grid tmpGrid;
						
						for (int i = 0; i < currentWord.getValidPlacements().size(); i++) {
							tmpGrid = new Grid(t_grid);
							currentWord.placeInGrid(tmpGrid, i);
							tmpSolver = new GridSolver(tmpGrid, t_wordList);
							m_threadPool.execute(tmpSolver);
	//						try {
	//							m_threadPool.submit(tmpSolver).get();
	//						} catch (InterruptedException e) {
	//							e.printStackTrace();
	//						} catch (ExecutionException e) {
	//							e.printStackTrace();
	//						}
						}
						disposeWordList();
						this.complete(null);
						return null;
					}
					else {
						wordIndex++;
					}
				}
				
			}
			disposeAll();
			this.complete(null);
			return null;
		}
		
		private void disposeAll() {
			
//			System.err.println(Integer.toHexString(this.hashCode()) + " entering dispose.");
//			System.err.println(Integer.toHexString(this.hashCode()) + " EXITING dispose.");
			t_grid.dispose();
			t_grid = null;

			disposeWordList();
		}
		
		private void disposeWordList() {
			for (Word w : t_wordList) {
				w.clearValidPlacements();
				w = null;
			}
			t_wordList = null;
		}
	}
	
	// TODO: legality check needs to be more thorough.. not checking for accidentally created words.
	//    example: word list: ONE, TWO, THREE, EVEN, EDEN, REED, WOE
	//    produces the word "HONE" in addition to a legal placement of "ONE"... 
//	      "HONE" being a contiguous string of characters but not being a word on the word list should flag the puzzle as illegal.
//		  Currently don't have a mechanism to check for contiguous strings of letters that are not words.
	//     
	public static boolean gridIsLegal(Grid grid, ArrayList<Word> wordList) {
//		System.err.println("Word list: " + wordList.toString());
//		printGrid(grid);
		
	nextWord:
		for (Word w : wordList) {			
			for (int x = 0; x < grid.getWidth(); x++) {
				for (int y = 0; y < grid.getHeight(); y++) {
					if (gridLegallyContainsWord(grid, w.toString(), x, y)) {
						continue nextWord;
					}
				}
			}
			return false;
		}
//		System.err.println("The grid has been determined to be legal:");
		return true;
	}
	
	public static boolean gridLegallyContainsWord(Grid grid, String word, int x, int y) {
		// Is the word's first letter here?
		if (grid.getCharAt(x, y) != word.charAt(0)) {
//			System.err.println("Comparing " + grid.get(col).get(row).getChar() + " to " + word.charAt(0));
			return false;
		}
		

		GridIterator gridIterator = new GridIterator(grid);
		
		nextDirection:
		for (int direction : m_validDirections) { 
			gridIterator.setX(x);
			gridIterator.setY(y);
			gridIterator.setDirection(direction);
			
			for (int charIndex = 1; charIndex < word.length(); charIndex++) {
				// If we are at the edge of the grid, try the next direction.
				if (!gridIterator.hasNext()) {
					continue nextDirection;
				}

				// If the letter in the grid doesn't match the letter in the word, try the next direction.
				if (gridIterator.next() != word.charAt(charIndex)) {
					continue nextDirection;
				}
			}
			
			// Okay, so we found all of the letters in a contiguous line on the grid.
			// We still need to be sure that there are no letters before the start of the word or after the end of the word.
			//   For example, if we are looking for NET we should not return true for SONNET.
			
			// If there is room on the grid after our word, make sure it's blank.
			if (gridIterator.hasNext()) {
				if (gridIterator.next() != Constants.EMPTY_CELL_CHARACTER) {
					continue nextDirection;
				}
			}
			// Same deal for before our word.
			gridIterator.setX(x);
			gridIterator.setY(y);
			if (gridIterator.hasPrevious()) {
				if (gridIterator.previous() != Constants.EMPTY_CELL_CHARACTER) {
					continue nextDirection;
				}
			}
			// If we pass all these checks, we're fine.
			gridIterator.dispose();
			return true;
		}

		gridIterator.dispose();
		return false;
	}
	
	public static void addWithoutDuplicates(Grid grid, LinkedBlockingQueue<Grid> container) {
		for (Grid otherGrid : container) {
			if (grid.equals(otherGrid)) {
//				System.err.println("not unique. Continuing...");
				return;
			}
		}

		container.add(grid);
		System.err.println("New solution found. (" + container.size() + ") unique solutions found so far.");
		System.err.println(grid);
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
