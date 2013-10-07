package puzzlemaker.tools;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import puzzlemaker.model.Constants;
import puzzlemaker.model.Model;
import puzzlemaker.puzzles.Crossword;
import puzzlemaker.puzzles.Puzzle;
import puzzlemaker.puzzles.Word;
import puzzlemaker.tools.grid.Grid;

public class PuzzleGenerator {
	private Model m_model;
	
	private byte m_puzzleType;
	private int[] m_validDirections;

	private ForkJoinPool m_threadPool;
	private ArrayList<Puzzle> m_solutions;
	private SyncObj m_solutionLock;
	
	
	public PuzzleGenerator(Model model) {
		m_model = model;
		m_solutionLock = new SyncObj(true);
		
		int numProcessorsToUse;
		switch (Runtime.getRuntime().availableProcessors()) {
			case 1:
			case 2:
			case 3:
				numProcessorsToUse = 1;
				break;
			default:
				numProcessorsToUse = Runtime.getRuntime().availableProcessors() - 2;
				break;
		}
		System.err.println("Using " + numProcessorsToUse + " processors.");
		m_threadPool = new ForkJoinPool(numProcessorsToUse);
	}

	public void setPuzzleType(byte puzzleType) {
		switch (puzzleType) {
			case Constants.TYPE_CROSSWORD:
				m_validDirections = Constants.CROSSWORD_DIRECTIONS;
				break;
			case Constants.TYPE_WORDSEARCH:
				m_validDirections = Constants.WORDSEARCH_DIRECTIONS;
				break;
			default:
				System.err.println("Unrecognized puzzle type: " + m_puzzleType);
				System.err.println(Thread.currentThread().getStackTrace());
				break;
		}
	}
	
	public boolean start(ArrayList<Puzzle> solutionsList) {
		if (m_validDirections == null) {
			return false;
		}
		
		m_solutions = solutionsList;
		
		ArrayList<Word> wordList = new ArrayList<Word>(m_model.getWordList().size());
		for (String s : m_model.getWordList()) {
			wordList.add(new Word(s));
		}

		PuzzleTask tmpTask = new PuzzleTask(new Grid(1, 1), wordList);
		tmpTask.quietlyInvoke();
//		System.err.println("Original task returned.");

		return true;
	}
	
	public boolean stop() {
		// TODO: needs actual implementing
		m_threadPool.shutdown();
		return true;
	}
	
	
	class PuzzleTask extends RecursiveTask<Void> {

		private static final long serialVersionUID = -4811564285255816109L;
		private Grid m_grid;
		private ArrayList<Word> m_wordList;
		private ArrayList<Grid> m_validPlacements;
		
		public PuzzleTask(Grid grid, ArrayList<Word> wordList) {
			m_grid = grid;

			// Deep copy the word list.
			m_wordList = new ArrayList<Word>(wordList.size());
			
			for (Word w : wordList) {
				m_wordList.add(new Word(w));
			}
		}

		
		@Override
		protected Void compute() {
			if (m_wordList.isEmpty()) {
				m_grid.trim();
				Puzzle puzzle = gridToPuzzleIfLegal(m_grid);
				
				if (puzzle != null) {
					addSolutionWithoutDuplicates(puzzle);
				}
			}
			else {
				int wordIndex = 0;
				while (wordIndex < m_wordList.size()) {
					m_validPlacements = findValidPlacements(m_grid, m_wordList.get(wordIndex));

					if (m_validPlacements.isEmpty()) {
						wordIndex++;
						continue;
					}
					else {
//						Regular implementation:
//						m_wordList.remove(wordIndex);
						
//						Alternate implementation: VERY SCARY
						@SuppressWarnings("unchecked")
						ArrayList<Word> tmpList = (ArrayList<Word>) m_wordList.clone();
						tmpList.remove(wordIndex);
						
						while (!m_validPlacements.isEmpty()) {
							Grid tmpGrid = m_validPlacements.remove(0);
//							System.err.println("Spawning new task with \nword list: {" + tmpList.toString() + "} \nand Grid:\n" + tmpGrid.toString());
//							System.err.println("spawning new puzzletask");

//							In Parallel:
							m_threadPool.execute(new PuzzleTask(tmpGrid, tmpList));

//							To serialize for debugging:
//							PuzzleTask tmpTask = new PuzzleTask(tmpGrid, tmpList);
//							tmpTask.quietlyInvoke();
						}
						

//						Regular implementation:
//						this.complete(null);
//						return null;
						
//						Alternate implementation: VERY SCARY
						wordIndex++;
					}
				}
			}
			complete(null);
			return null;
		}	
	}
	
	public ArrayList<Grid> findValidPlacements(Grid grid, Word word) {
		ArrayList<Grid> validGrids = new ArrayList<Grid>(0);
		Grid validGrid;
		if (grid.isEmpty()) {
			for (int direction : m_validDirections) {
				validGrid = new Grid(grid);
				placeWordInGrid(validGrid, word, 0, 0, direction, 0);
				validGrids.ensureCapacity(validGrids.size() + 1);
				validGrids.add(validGrid);
			}
			return validGrids;
		}
		
		switch (m_puzzleType) {
			case Constants.TYPE_CROSSWORD:
				for (int x = 0; x < grid.getWidth(); x++) {
					for (int y = 0; y < grid.getHeight(); y++) {
						if (word.containsChar(grid.getCharAt(x, y))) {
							for (int intersection : word.getIntersectionIndices(grid.getCharAt(x, y))) {
								for (int direction : m_validDirections) {
									if (!hasIllegalIntersections(grid, word, x, y, direction, intersection)) {
										validGrid = new Grid(grid);
										placeWordInGrid(validGrid, word, x, y, direction, intersection);
										validGrids.add(validGrid);
									}
								}
							}							
						}
					}
				}
				
				
				
				break;
			case Constants.TYPE_WORDSEARCH:
				break;
		}
		
		return validGrids;
	}
	
	public boolean hasIllegalIntersections(Grid grid, Word word, int x, int y, int direction, int offset) {
		while (offset > 0) {
			switch (direction) {
				case Constants.LEFT_TO_RIGHT:
					x--;
					break;
				case Constants.TOPLEFT_TO_BOTTOMRIGHT:
					x--;
					y--;
					break;
				case Constants.TOP_TO_BOTTOM:
					y--;
					break;
				case Constants.TOPRIGHT_TO_BOTTOMLEFT:
					x++;
					y--;
					break;
				case Constants.RIGHT_TO_LEFT:
					x++;
					break;
				case Constants.BOTTOMRIGHT_TO_TOPLEFT:
					x++;
					y++;
					break;
				case Constants.BOTTOM_TO_TOP:
					y++;
					break;
				case Constants.BOTTOMLEFT_TO_TOPRIGHT:
					x--;
					y++;
					break;
			}
			offset--;
		}
		
		// Check behind the first letter
		switch (direction) {
			case Constants.LEFT_TO_RIGHT:
				x--;
				break;
			case Constants.TOPLEFT_TO_BOTTOMRIGHT:
				x--;
				y--;
				break;
			case Constants.TOP_TO_BOTTOM:
				y--;
				break;
			case Constants.TOPRIGHT_TO_BOTTOMLEFT:
				x++;
				y--;
				break;
			case Constants.RIGHT_TO_LEFT:
				x++;
				break;
			case Constants.BOTTOMRIGHT_TO_TOPLEFT:
				x++;
				y++;
				break;
			case Constants.BOTTOM_TO_TOP:
				y++;
				break;
			case Constants.BOTTOMLEFT_TO_TOPRIGHT:
				x--;
				y++;
				break;
		}
		
		if (x >= 0 && x < grid.getWidth() && y >= 0 && y < grid.getHeight()) {
			if (grid.getCharAt(x, y) != Constants.EMPTY_CELL_CHARACTER) {
				return true;
			}
		}
		
		switch (direction) {
			case Constants.LEFT_TO_RIGHT:
				x++;
				break;
			case Constants.TOPLEFT_TO_BOTTOMRIGHT:
				x++;
				y++;
				break;
			case Constants.TOP_TO_BOTTOM:
				y++;
				break;
			case Constants.TOPRIGHT_TO_BOTTOMLEFT:
				x--;
				y++;
				break;
			case Constants.RIGHT_TO_LEFT:
				x--;
				break;
			case Constants.BOTTOMRIGHT_TO_TOPLEFT:
				x--;
				y--;
				break;
			case Constants.BOTTOM_TO_TOP:
				y--;
				break;
			case Constants.BOTTOMLEFT_TO_TOPRIGHT:
				x++;
				y--;
				break;
		}
		
		// Check the word's placement
		for (int i = 0; i < word.toString().length(); i++) {
			if (x >= 0 && x < grid.getWidth() && y >= 0 && y < grid.getHeight()) {
				if (grid.getCharAt(x, y) != Constants.EMPTY_CELL_CHARACTER && grid.getCharAt(x, y) != word.toString().charAt(i)) {
					return true;
				}
			}
			
			switch (direction) {
				case Constants.LEFT_TO_RIGHT:
					x++;
					break;
				case Constants.TOPLEFT_TO_BOTTOMRIGHT:
					x++;
					y++;
					break;
				case Constants.TOP_TO_BOTTOM:
					y++;
					break;
				case Constants.TOPRIGHT_TO_BOTTOMLEFT:
					x--;
					y++;
					break;
				case Constants.RIGHT_TO_LEFT:
					x--;
					break;
				case Constants.BOTTOMRIGHT_TO_TOPLEFT:
					x--;
					y--;
					break;
				case Constants.BOTTOM_TO_TOP:
					y--;
					break;
				case Constants.BOTTOMLEFT_TO_TOPRIGHT:
					x++;
					y--;
					break;
			}
		}
		
		// And check after the word
		if (x >= 0 && x < grid.getWidth() && y >= 0 && y < grid.getHeight()) {
			if (grid.getCharAt(x, y) != Constants.EMPTY_CELL_CHARACTER) {
				return true;
			}
		}
		
		return false;
	}
	
	/** Places the word in the grid at the given starting point in the given direction.<br>
	 * Resizes the grid if necessary. */
	public void placeWordInGrid(Grid grid, Word word, int x, int y, int direction, int offset) {
		while (offset > 0) {
			switch (direction) {
			case Constants.LEFT_TO_RIGHT:
				x--;
				break;
			case Constants.TOPLEFT_TO_BOTTOMRIGHT:
				x--;
				y--;
				break;
			case Constants.TOP_TO_BOTTOM:
				y--;
				break;
			case Constants.TOPRIGHT_TO_BOTTOMLEFT:
				x++;
				y--;
				break;
			case Constants.RIGHT_TO_LEFT:
				x++;
				break;
			case Constants.BOTTOMRIGHT_TO_TOPLEFT:
				x++;
				y++;
				break;
			case Constants.BOTTOM_TO_TOP:
				y++;
				break;
			case Constants.BOTTOMLEFT_TO_TOPRIGHT:
				x--;
				y++;
				break;
			}
			offset--;
		}
		
		while (x >= grid.getWidth()) {
			grid.addColumnOnRight();
		}
		while (y >= grid.getHeight()) {
			grid.addRowOnBottom();
		}
		while (x < 0) {
			grid.addColumnOnLeft();
			x++;
		}
		while (y < 0) {
			grid.addRowOnTop();
			y++;
		}

		
		for (int i = 0; i < word.toString().length(); i++) {
			// Expand grid if necessary
			if (x < 0) {
				grid.addColumnOnLeft();
				x++;
			}
			else if (x >= grid.getWidth()) {
				grid.addColumnOnRight();
			}
			if (y < 0) {
				grid.addRowOnTop();
				y++;
			}
			else if (y >= grid.getHeight()) {
				grid.addRowOnBottom();
			}
			
			// Set grid cell to character
			grid.setCharAt(x, y, word.toString().charAt(i));
			
			// Move the (x, y) coordinate based on direction
			switch (direction) {
				case Constants.LEFT_TO_RIGHT:
					x++;
					break;
				case Constants.TOPLEFT_TO_BOTTOMRIGHT:
					x++;
					y++;
					break;
				case Constants.TOP_TO_BOTTOM:
					y++;
					break;
				case Constants.TOPRIGHT_TO_BOTTOMLEFT:
					x--;
					y++;
					break;
				case Constants.RIGHT_TO_LEFT:
					x--;
					break;
				case Constants.BOTTOMRIGHT_TO_TOPLEFT:
					x--;
					y--;
					break;
				case Constants.BOTTOM_TO_TOP:
					y--;
					break;
				case Constants.BOTTOMLEFT_TO_TOPRIGHT:
					x++;
					y--;
					break;
			}
		}
	}	
	
	/** Ensures that the grid contains exactly the word list.<br>
	 * @return <b>null</b> if the grid is illegal.*/
	private Puzzle gridToPuzzleIfLegal(Grid grid) {
		ArrayList<String> unfoundWordList = new ArrayList<String>(m_model.getWordList().size());
		for (String s : m_model.getWordList()) {
			unfoundWordList.add(new String(s));
		}
		ArrayList<Word> foundWordList = new ArrayList<Word>(unfoundWordList.size());
		
		String currentWord = "";
		int x, y, startX = -1, startY = -1;

		for (int direction : m_validDirections) {
			// Set start and end points.
			switch (direction) {
				case Constants.LEFT_TO_RIGHT: // then top to bottom
				case Constants.TOP_TO_BOTTOM: // then left to right
				case Constants.TOPRIGHT_TO_BOTTOMLEFT: // from top-left corner to bot-right corner 
				case Constants.BOTTOMLEFT_TO_TOPRIGHT: // from top-left corner to bot-right corner
					startX = 0;
					startY = 0;
					break;
				case Constants.TOPLEFT_TO_BOTTOMRIGHT: // from bot-left corner to top-right corner
				case Constants.BOTTOM_TO_TOP: // then left to right
					startX = 0;
					startY = grid.getHeight() - 1;
					break;
				case Constants.RIGHT_TO_LEFT: // then top to bottom
				case Constants.BOTTOMRIGHT_TO_TOPLEFT: // from top-right corner to bot-left corner
					startX = grid.getWidth() - 1;
					startY = 0;
					break;
			}
			
			x = startX;
			y = startY;
			
			while (startX >= 0 && startX < grid.getWidth() && startY >= 0 && startY < grid.getHeight()) {
				while (x >= 0 && x < grid.getWidth() && y >= 0 && y < grid.getHeight()) {
					if (grid.getCharAt(x, y) == Constants.EMPTY_CELL_CHARACTER) {
						if (currentWord.length() < 2) {
							currentWord = "";
						}
						else {
							if (unfoundWordList.remove(currentWord)) {
								int wordStartX = -1, wordStartY = -1;
								switch (direction) {
									case Constants.LEFT_TO_RIGHT:
										wordStartX = x - currentWord.length();
										wordStartY = y;
										break;
									case Constants.TOPLEFT_TO_BOTTOMRIGHT:
										wordStartX = x - currentWord.length();
										wordStartY = y - currentWord.length();
										break;
									case Constants.TOP_TO_BOTTOM:
										wordStartX = x;
										wordStartY = y - currentWord.length();
										break;
									case Constants.TOPRIGHT_TO_BOTTOMLEFT:
										wordStartX = x + currentWord.length();
										wordStartY = y - currentWord.length();
										break;
									case Constants.RIGHT_TO_LEFT:
										wordStartX = x + currentWord.length();
										wordStartY = y;
										break;
									case Constants.BOTTOMRIGHT_TO_TOPLEFT:
										wordStartX = x + currentWord.length();
										wordStartY = y + currentWord.length();
										break;
									case Constants.BOTTOM_TO_TOP:
										wordStartX = x;
										wordStartY = y + currentWord.length();
										break;
									case Constants.BOTTOMLEFT_TO_TOPRIGHT:
										wordStartX = x - currentWord.length();
										wordStartY = y + currentWord.length();
										break;
								}
								
								foundWordList.add(new Word(currentWord, wordStartX, wordStartY, direction));
//								System.err.println(Integer.toHexString(this.hashCode()) + "GRID LEGALITY: Word found (" + currentWord + ")");
								currentWord = "";
							}
							else
							{
//								System.err.println(Integer.toHexString(this.hashCode()) + "GRID LEGALITY: Word NOT found (" + currentWord + ")");
								return null;
							}
						}
					}
					else {
						currentWord += grid.getCharAt(x, y);
//						System.err.println("currentWord = " + currentWord);
					}
					
					switch (direction) {
						case Constants.LEFT_TO_RIGHT:
							x++;
							break;
						case Constants.TOPLEFT_TO_BOTTOMRIGHT:
							x++;
							y++;
							break;
						case Constants.TOP_TO_BOTTOM:
							y++;
							break;
						case Constants.TOPRIGHT_TO_BOTTOMLEFT:
							x--;
							y++;
							break;
						case Constants.RIGHT_TO_LEFT:
							x--;
							break;
						case Constants.BOTTOMRIGHT_TO_TOPLEFT:
							x--;
							y--;
							break;
						case Constants.BOTTOM_TO_TOP:
							y--;
							break;
						case Constants.BOTTOMLEFT_TO_TOPRIGHT:
							x++;
							y--;
							break;
					}
				}
				
				
				if (currentWord.length() < 2) {
					currentWord = "";
				}
				else {
					if (unfoundWordList.remove(currentWord)) {
						int wordStartX = -1, wordStartY = -1;
						switch (direction) {
							case Constants.LEFT_TO_RIGHT:
								wordStartX = x - currentWord.length();
								wordStartY = y;
								break;
							case Constants.TOPLEFT_TO_BOTTOMRIGHT:
								wordStartX = x - currentWord.length();
								wordStartY = y - currentWord.length();
								break;
							case Constants.TOP_TO_BOTTOM:
								wordStartX = x;
								wordStartY = y - currentWord.length();
								break;
							case Constants.TOPRIGHT_TO_BOTTOMLEFT:
								wordStartX = x + currentWord.length();
								wordStartY = y - currentWord.length();
								break;
							case Constants.RIGHT_TO_LEFT:
								wordStartX = x + currentWord.length();
								wordStartY = y;
								break;
							case Constants.BOTTOMRIGHT_TO_TOPLEFT:
								wordStartX = x + currentWord.length();
								wordStartY = y + currentWord.length();
								break;
							case Constants.BOTTOM_TO_TOP:
								wordStartX = x;
								wordStartY = y + currentWord.length();
								break;
							case Constants.BOTTOMLEFT_TO_TOPRIGHT:
								wordStartX = x - currentWord.length();
								wordStartY = y + currentWord.length();
								break;
						}
						
						foundWordList.add(new Word(currentWord, wordStartX, wordStartY, direction));
//						System.err.println(Integer.toHexString(this.hashCode()) + "GRID LEGALITY: Word found (" + currentWord + ")");
						currentWord = "";
					}
					else
					{
//						System.err.println(Integer.toHexString(this.hashCode()) + "GRID LEGALITY: Word NOT found (" + currentWord + ")");
						return null;
					}
				}
				
				switch (direction) {
					case Constants.LEFT_TO_RIGHT:
					case Constants.RIGHT_TO_LEFT:
						startY++;
						break;
					case Constants.TOPLEFT_TO_BOTTOMRIGHT:
						if (startY > 0) {
							startY--;
						}
						else {
							startX++;
						}
						break;
					case Constants.TOP_TO_BOTTOM:
					case Constants.BOTTOM_TO_TOP:
						startX++;
						break;
					case Constants.TOPRIGHT_TO_BOTTOMLEFT:
						if (startX < grid.getWidth() - 1) {
							startX++;
						}
						else {
							startY++;
						}
						break;
					case Constants.BOTTOMRIGHT_TO_TOPLEFT:
						if (startY < grid.getHeight() - 1) {
							startY++;
						}
						else {
							startX--;
						}
						break;
					case Constants.BOTTOMLEFT_TO_TOPRIGHT:
						if (startY < grid.getHeight() - 1) {
							startY++;
						}
						else {
							startX++;
						}
						break;
				}
				
				x = startX;
				y = startY;			
			}
			
			if (currentWord.length() < 2) {
				currentWord = "";
			}
			else {
				if (unfoundWordList.remove(currentWord)) {
					int wordStartX = -1, wordStartY = -1;
					switch (direction) {
						case Constants.LEFT_TO_RIGHT:
							wordStartX = x - currentWord.length();
							wordStartY = y;
							break;
						case Constants.TOPLEFT_TO_BOTTOMRIGHT:
							wordStartX = x - currentWord.length();
							wordStartY = y - currentWord.length();
							break;
						case Constants.TOP_TO_BOTTOM:
							wordStartX = x;
							wordStartY = y - currentWord.length();
							break;
						case Constants.TOPRIGHT_TO_BOTTOMLEFT:
							wordStartX = x + currentWord.length();
							wordStartY = y - currentWord.length();
							break;
						case Constants.RIGHT_TO_LEFT:
							wordStartX = x + currentWord.length();
							wordStartY = y;
							break;
						case Constants.BOTTOMRIGHT_TO_TOPLEFT:
							wordStartX = x + currentWord.length();
							wordStartY = y + currentWord.length();
							break;
						case Constants.BOTTOM_TO_TOP:
							wordStartX = x;
							wordStartY = y + currentWord.length();
							break;
						case Constants.BOTTOMLEFT_TO_TOPRIGHT:
							wordStartX = x - currentWord.length();
							wordStartY = y + currentWord.length();
							break;
					}
					
					System.err.println("add 3");
					foundWordList.add(new Word(currentWord, wordStartX, wordStartY, direction));
//					System.err.println(Integer.toHexString(this.hashCode()) + "GRID LEGALITY: Word found (" + currentWord + ")");
					currentWord = "";
				}
				else
				{
//					System.err.println(Integer.toHexString(this.hashCode()) + "GRID LEGALITY: Word NOT found (" + currentWord + ")");
					return null;
				}
			}
		}
		
		switch (m_puzzleType) {
		case Constants.TYPE_CROSSWORD:
//			System.err.println("foundWordList: ");
//			for (Word w : foundWordList) {
//				System.err.println(w.toStringDetailed());
//			}
			return new Crossword(grid, foundWordList);
			
		default:
			System.err.println("Unrecognized puzzle type: " + m_puzzleType);
			System.err.println(Thread.currentThread().getStackTrace());
			return null;
		}		
	}
	
	private void addSolutionWithoutDuplicates (Puzzle newPuzzle) {
		m_solutionLock.doAcquire();
		
		for (int i = 0; i < m_solutions.size(); i++) {
			if (m_solutions.get(i).getGrid().equals(newPuzzle.getGrid())) {
//				System.err.println("non-unique (" + (i + 1) + ")");
//				System.err.println(newPuzzle.getGrid().toString());
				m_solutionLock.doRelease();
				return;
			}
		}
		
		m_solutions.add(newPuzzle);
//		System.err.println("^ unique! (" + (m_solutions.size()) + ")");
		System.err.println("Found a puzzle! (" + (m_solutions.size()) + ")");
		System.err.println(newPuzzle.toString());
				
		m_solutionLock.doRelease();
	}
}
