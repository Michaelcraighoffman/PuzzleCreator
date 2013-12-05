package puzzlemaker.tools;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import puzzlemaker.Constants;
import puzzlemaker.Constants.DefaultOptions;
import puzzlemaker.model.Model;
import puzzlemaker.puzzles.Crossword;
import puzzlemaker.puzzles.Puzzle;
import puzzlemaker.puzzles.Word;
import puzzlemaker.puzzles.WordSearch;
import puzzlemaker.tools.grid.Grid;
import puzzlemaker.tools.grid.GridWalker;

public class PuzzleGenerator {
	private Model m_model;
	
	private byte m_puzzleType;
	private int[] m_validDirections;

	private ForkJoinPool m_threadPool;
	private ConcurrentSkipListSet<Puzzle> m_newSolutions;
	private ConcurrentSkipListSet<Grid> m_inProgressGrids;

	private long m_debugStart, m_debugEnd;
	
	// Size constraints
	private boolean m_hasMinimumSize = DefaultOptions.PUZZLE_SIZE_MIN_CONSTRAINED, m_hasMaximumSize = DefaultOptions.PUZZLE_SIZE_MAX_CONSTRAINED, m_hasExactSize = DefaultOptions.PUZZLE_SIZE_EXACT_CONSTRAINED;
	private int m_minSizeX = DefaultOptions.PUZZLE_SIZE_MIN_X, m_minSizeY = DefaultOptions.PUZZLE_SIZE_MIN_Y, m_maxSizeX = DefaultOptions.PUZZLE_SIZE_MAX_X, m_maxSizeY = DefaultOptions.PUZZLE_SIZE_MAX_Y, m_exactSizeX = DefaultOptions.PUZZLE_SIZE_EXACT_X, m_exactSizeY = DefaultOptions.PUZZLE_SIZE_EXACT_Y;
	private boolean m_allowNonSquare = DefaultOptions.PUZZLE_ALLOW_NON_SQUARE;
	
	public PuzzleGenerator(Model model) {
		m_model = model;
		
		int numProcessorsToUse;
		switch (Runtime.getRuntime().availableProcessors()) {
			case 1:
			case 2:
			case 3:
				numProcessorsToUse = 1;
				break;
			default:
				numProcessorsToUse = Runtime.getRuntime().availableProcessors() - 1;
				break;
		}
		System.err.println("Using " + numProcessorsToUse + " processors.");
		m_threadPool = new ForkJoinPool(numProcessorsToUse);		
	}

	public void setPuzzleType(byte puzzleType) {
		m_puzzleType=puzzleType;
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
	
	public void setMinPuzzleSize(boolean enabled, int x, int y) {
		m_hasMinimumSize = enabled;
		
		if (m_hasMinimumSize) {
			m_minSizeX = x;
			m_minSizeY = y;
		}
	}
	
	public void setMaxPuzzleSize(boolean enabled, int x, int y) {
		m_hasMaximumSize = enabled;
		
		if (m_hasMaximumSize) {
			m_maxSizeX = x;
			m_maxSizeY = y;
		}
	}

	public void setExactlPuzzleSize(boolean enabled, int x, int y) {
		m_hasExactSize = enabled;
		
		if (m_hasExactSize) {
			m_exactSizeX = x;
			m_exactSizeY = y;
		}
	}
	
	public boolean start(ConcurrentSkipListSet<Puzzle> solutionsList) {
		if (m_validDirections == null) {
			return false;
		}

		m_newSolutions = solutionsList;
		m_inProgressGrids = new ConcurrentSkipListSet<Grid>();
		
		// Sorting by length in descending order is justified concretely in Puzzle.isLegal()'s WordSearch case for
		//    checking if the variable "letters" contains (instead of equals [in the Crossword case]) a word in the word list.
		ArrayList<Word> wordList = new ArrayList<Word>(m_model.getWordList().size());
		int insertIndex;
		for (String s : m_model.getWordList()) {
			for (insertIndex = 0; insertIndex < wordList.size(); insertIndex++) {
				if (s.length() >= wordList.get(insertIndex).toString().length()) {
					break;
				}
			}
			wordList.add(insertIndex, new Word(s));
		}

		
		
		// By doing quietlyInvoke (which blocks) instead of threadPool.execute(...) (which doesn't),
		//    we give statusReporter the time it needs for m_threadPool to not be Quiescent.
		PuzzleTask tmpTask = new PuzzleTask(new Grid(1, 1), wordList);
		m_debugStart = System.currentTimeMillis();
		tmpTask.quietlyInvoke();
		
		
		
		final Runtime runtime = Runtime.getRuntime();
//		System.err.println("Memory usage: " + ((runtime.maxMemory() - runtime.freeMemory()) / 1048576) + "/" + (runtime.maxMemory() / 1048576) + "MB; Time elapsed: " + ((System.currentTimeMillis() - m_debugStart) / 1000) + "s");

		Thread statusReporter = new Thread() {
			
			@Override
			public void run() {
				int reportNumber = 1;
				while (!m_threadPool.isQuiescent()) {
					System.err.println("Unique solutions: " + m_newSolutions.size() + "; In progress grids: " + m_inProgressGrids.size() + "; Queued tasks: " + m_threadPool.getQueuedTaskCount() + "; Report #" + reportNumber++);
//					System.err.println("Memory usage: " + ((runtime.maxMemory() - runtime.freeMemory()) / 1048576) + "/" + (runtime.maxMemory() / 1048576) + "MB; Time elapsed: " + ((System.currentTimeMillis() - m_debugStart) / 1000) + "s");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				m_debugEnd = System.currentTimeMillis();
				System.err.println("Final count: " + m_newSolutions.size() + " unique, " + m_inProgressGrids.size() + " in progress.  Time elapsed: " + ((m_debugEnd - m_debugStart) / 1000) + " seconds.");
//				System.err.println("Memory usage: " + ((runtime.maxMemory() - runtime.freeMemory()) / 1048576) + "/" + (runtime.maxMemory() / 1048576) + "MB; Time elapsed: " + ((System.currentTimeMillis() - m_debugStart) / 1000) + "s");
				m_inProgressGrids.clear();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
//				System.err.println("Memory usage: " + ((runtime.maxMemory() - runtime.freeMemory()) / 1048576) + "/" + (runtime.maxMemory() / 1048576) + "MB; Time elapsed: " + ((System.currentTimeMillis() - m_debugStart) / 1000) + "s");
//				System.err.println("Calling garbage collector...");
				runtime.gc();
//				System.err.println("Returned from calling the garbage collector.");
//				System.err.println("Memory usage: " + ((runtime.maxMemory() - runtime.freeMemory()) / 1048576) + "/" + (runtime.maxMemory() / 1048576) + "MB; Time elapsed: " + ((System.currentTimeMillis() - m_debugStart) / 1000) + "s");
			}
		};

		statusReporter.start();
		
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

			m_wordList = new ArrayList<Word>(wordList.size());			
			for (Word w : wordList) {
				m_wordList.add(new Word(w));
			}
		}

		
		@Override
		protected Void compute() {
			
			if (m_wordList.isEmpty()) {
				m_grid.trim();
				if (m_puzzleType==Constants.TYPE_CROSSWORD) {
					if (!m_allowNonSquare) {
						if (m_grid.getWidth() != m_grid.getHeight()) {
							return null;
						}
					}

					ArrayList<Word> wordList = new ArrayList<Word>(m_model.getWordList().size());
					for (String s : m_model.getWordList()) {
						wordList.add(new Word(s));
					}

					Puzzle puzzle = new Crossword(m_grid, wordList);
					if (((Crossword)puzzle).isLegal()) {
						addSolution(puzzle);
					}
				}
				else if (m_puzzleType == Constants.TYPE_WORDSEARCH) {
					if (!m_allowNonSquare) {
						if (Math.abs(m_grid.getWidth() - m_grid.getHeight()) > 2) {
							return null;
						}
					}
					
					ArrayList<Word> wordList = new ArrayList<Word>(m_model.getWordList().size());
					for (String s : m_model.getWordList()) {
						wordList.add(new Word(s));
					}

					Puzzle puzzle = new WordSearch(m_grid, wordList);
					if (puzzle.isLegal()) {
						if (!m_allowNonSquare) {
							if (Math.abs(m_grid.getWidth() - m_grid.getHeight()) <= 2) {
								puzzle.getGrid().makeSquare();
							}
							else {
								return null;
							}
						}
						// We're going to ignore the fact that using fillIn() could, in some very rare cases,
						//   allow for puzzles which have identical solutions (and just different fills).
						((WordSearch)puzzle).fillIn();
						addSolution(puzzle);
					}
				}
			}
			else {
//				Limits the size of the "in progress" grids, which otherwise can get out of control.
				if (m_wordList.size() >= (m_model.getWordList().size() / 2)) {
					if (m_puzzleType == Constants.TYPE_CROSSWORD) {
						if (!m_inProgressGrids.add(m_grid)) {
							return null;
						}
					}
				}
				
				int wordIndex = 0;
				while (wordIndex < m_wordList.size()) {
					m_validPlacements = findValidPlacements(m_grid, m_wordList.get(wordIndex));
					
					if (m_validPlacements.isEmpty()) {
						wordIndex++;
						continue;
					}
					else {
						@SuppressWarnings("unchecked")
						ArrayList<Word> tmpList = (ArrayList<Word>) m_wordList.clone();
						tmpList.remove(wordIndex);
						
						while (!m_validPlacements.isEmpty()) {
							Grid tmpGrid = m_validPlacements.remove(0);

//							In Parallel:							
							m_threadPool.execute(new PuzzleTask(tmpGrid, tmpList));

//							In Serial (for debugging):
//							PuzzleTask tmpTask = new PuzzleTask(tmpGrid, tmpList);
//							tmpTask.quietlyInvoke();
						}
						wordIndex++;
					}
				}
			}
			return null;
		}	
	
		public ArrayList<Grid> findValidPlacements(Grid grid, Word word) {
			ArrayList<Grid> validGrids = new ArrayList<Grid>(3);
			Grid validGrid;
			
			switch (m_puzzleType) {
				case Constants.TYPE_CROSSWORD:
					if (grid.isEmpty()) {
						for (int direction : m_validDirections) {
							validGrid = new Grid(grid);
							placeWordInGrid(validGrid, word, 0, 0, direction, 0);
							validGrids.add(validGrid);
						}
						return validGrids;
					}
					
					for (int x = 0; x < grid.getWidth(); x++) {
						for (int y = 0; y < grid.getHeight(); y++) {
							if (word.containsChar(grid.getCharAt(x, y))) {
								for (int intersection : word.getIntersectionIndices(grid.getCharAt(x, y))) {
									for (int direction : m_validDirections) {
										if (!hasIllegalCrosswordIntersections(grid, word, x, y, direction, intersection)) {
											validGrid = new Grid(grid);
											if (placeWordInGrid(validGrid, word, x, y, direction, intersection)) {
												validGrids.add(validGrid);
											}
										}
									}
								}							
							}
						}
					}
					break;
				case Constants.TYPE_WORDSEARCH:
					Random r = new Random();
					if (grid.isEmpty()) {
						int direction = -1;
						for (int i = 0; i < 4; i++) {
							direction += r.nextInt(2) + 1;
							validGrid = new Grid(1,1);
							placeWordInGrid(validGrid, word, 0, 0, direction, 0);
							validGrids.add(validGrid);
						}
						return validGrids;
					}
					
					// Let's just get 3 placements. We'll try for at most two intersections and one non-intersection.
					
					findIntersections:
					for (int x = 0; x < grid.getWidth(); x++) {
						for (int y = 0; y < grid.getHeight(); y++) {
							if (word.containsChar(grid.getCharAt(x, y))) {
								for (int intersection : word.getIntersectionIndices(grid.getCharAt(x, y))) {
									int direction = r.nextInt(8);
									for (int i = 0; i < 8; i++) {
										
										if (!hasIllegalWordSearchIntersections(grid, word, x, y, direction, intersection)) {
											validGrid = new Grid(grid);
											if (placeWordInGrid(validGrid, word, x, y, direction, intersection)) {
												validGrids.add(validGrid);
												break findIntersections;
											}
										}
										direction = (direction + 1) & 7;
									}
								}							
							}
						}
					}
					
					// Now let's find non-intersecting places to put the word until we have 3 total valid grids.
					
					// We'll pick a direction and a random location, see if we can place it there, 
					//    and if not, spiral outwards using the same direction.
					int direction, spiralMoveAmount;
					boolean increaseSpiral;
					GridWalker walker;

					while (validGrids.size() < 3) {
						direction = r.nextInt(8);
						spiralMoveAmount = 1;
						increaseSpiral = false;
						walker = new GridWalker(grid, r.nextInt(grid.getWidth()), r.nextInt(grid.getHeight()), r.nextInt(4) * 2);
						
						while (hasIllegalWordSearchIntersections(grid, word, walker.x, walker.y, direction, 0)) {
							walker.moveForward(spiralMoveAmount);
							if (increaseSpiral) {
								spiralMoveAmount++;
							}
							increaseSpiral = !increaseSpiral;
							walker.rotate(2);
						}
						validGrid = new Grid(grid);
						if (placeWordInGrid(validGrid, word, walker.x, walker.y, direction, 0)) {
							validGrids.add(validGrid);
						}
					}
					break;
			}
			return validGrids;
		}
	}
	
	public boolean hasIllegalCrosswordIntersections(Grid grid, Word word, int x, int y, int direction, int offset) {
		GridWalker walker = new GridWalker(grid, x, y, direction);
		
		walker.moveForward(-offset);
		
		// Start by checking behind the letter.
		walker.moveForward(-1);
		
		if (walker.isInBounds()) {
			if (grid.getCharAt(walker.x, walker.y) != Constants.EMPTY_CELL_CHARACTER) {
				return true;
			}
		}
		
		// Go back to the start of the word.
		walker.moveForward(1);
		
		// Check the word's placement
		for (int i = 0; i < word.toString().length(); i++) {
			if (walker.isInBounds()) {
				if (grid.getCharAt(walker.x, walker.y) != Constants.EMPTY_CELL_CHARACTER && grid.getCharAt(walker.x, walker.y) != word.toString().charAt(i)) {
					return true;
				}
			}
			
			walker.moveForward(1);
		}
		
		// And check after the word
		if (walker.isInBounds()) {
			if (grid.getCharAt(walker.x, walker.y) != Constants.EMPTY_CELL_CHARACTER) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasIllegalWordSearchIntersections(Grid grid, Word word, int x, int y, int direction, int offset) {
		GridWalker walker = new GridWalker(grid, x, y, direction);
		
		walker.moveForward(-offset);
		
		// Check the word's placement
		for (int i = 0; i < word.toString().length(); i++) {
			if (walker.isInBounds()) {
				if (grid.getCharAt(walker.x, walker.y) != Constants.EMPTY_CELL_CHARACTER && grid.getCharAt(walker.x, walker.y) != word.toString().charAt(i)) {
					return true;
				}
			}
			
			walker.moveForward(1);
		}
		
		return false;
	}
	
	/** Places the word in the grid at the given starting point in the given direction.<br>
	 * Resizes the grid if necessary. 
	 * Assumes placement is legal (will not illegally change letters already on grid).
	 * 
	 * @param grid The grid to be modified.
	 * @param word The word to be placed in the grid.
	 * @param x The word's starting position's "x" value.
	 * @param y The word's starting position's "y" value.
	 * @param direction The direction that the word will be placed in.
	 * @param offset How much to adjust the starting x and y in the direction that is the opposite of the "direction" parameter.
	 * 
	 * @return {@code true} if the puzzle is a legal size after placement.
	 * 
	 * @author Samuel Wiley
	 */
	public boolean placeWordInGrid(Grid grid, Word word, int x, int y, int direction, int offset) {
		GridWalker walker = new GridWalker(grid, x, y, direction);
		
		walker.moveForward(-offset);
		
		// Expand grid to accommodate walker's current position.
		if (!walker.isInBounds()) {
			// Check to see if the additions will exceed size restrictions. (Expanding is costly, cycle-wise.)
			if (m_hasMaximumSize && (walker.x > m_maxSizeX || walker.y > m_maxSizeY || (grid.getWidth() - walker.x) > m_maxSizeX || (grid.getHeight() - walker.y) > m_maxSizeY)) {
				return false;
			}
			if (m_hasExactSize && (walker.x > m_exactSizeX || walker.y > m_exactSizeY || (grid.getWidth() - walker.x) > m_exactSizeX || (grid.getHeight() - walker.y) > m_exactSizeY)) {
				return false;
			}
			
			while (walker.x >= grid.getWidth()) {
				grid.addColumnOnRight();
			}
			while (walker.x < 0) {
				grid.addColumnOnLeft();
				walker.x++;
			}
			while (walker.y >= grid.getHeight()) {
				grid.addRowOnBottom();
			}
			while (walker.y < 0) {
				grid.addRowOnTop();
				walker.y++;
			}
		}

		
		for (int i = 0; i < word.toString().length(); i++) {
			// Expand grid if necessary
			if (walker.x >= grid.getWidth()) {
				grid.addColumnOnRight();
			}
			else if (walker.x < 0) {
				grid.addColumnOnLeft();
				walker.x++;
			}
			if (walker.y >= grid.getHeight()) {
				grid.addRowOnBottom();
			}
			else if (walker.y < 0) {
				grid.addRowOnTop();
				walker.y++;
			}
			
			// Set grid cell's value to character of our word
			grid.setCharAt(walker.x, walker.y, word.toString().charAt(i));
			
			walker.moveForward(1);
		}
		
		if (m_hasMaximumSize && (walker.x > m_maxSizeX || walker.y > m_maxSizeY || (grid.getWidth() - walker.x) > m_maxSizeX || (grid.getHeight() - walker.y) > m_maxSizeY)) {
			return false;
		}
		if (m_hasExactSize && (walker.x > m_exactSizeX || walker.y > m_exactSizeY || (grid.getWidth() - walker.x) > m_exactSizeX || (grid.getHeight() - walker.y) > m_exactSizeY)) {
			return false;
		}
		
		return true;
	}	
	
	/** Adds {@code newPuzzle} to the set of generated solutions if its size is legal.
	 * 
	 * @see #setMinPuzzleSize(boolean, int, int)
	 * @see #setMaxPuzzleSize(boolean, int, int)
	 * @see #setExactlPuzzleSize(boolean, int, int)
	 * 
	 * @author Samuel Wiley
	 */
	private void addSolution (Puzzle newPuzzle) {
		if (m_hasExactSize) {
			if (newPuzzle.getGrid().getWidth() != m_exactSizeX || newPuzzle.getGrid().getHeight() != m_exactSizeY) {
				return;
			}
		}
		
		if (m_hasMinimumSize) {
			if (newPuzzle.getGrid().getWidth() < m_minSizeX || newPuzzle.getGrid().getHeight() != m_minSizeY) {
				return;
			}
		}
		
		// Note: maximum size gets checked as the puzzle gets resized in placeWordInGrid.
		
		m_newSolutions.add(newPuzzle);
	}

	public void setAllowNonSquare(boolean allowed) {
		m_allowNonSquare = allowed;
	}

	public boolean isRunning() {
		return !(m_threadPool.isQuiescent() || m_threadPool.isShutdown());
	}
	
}
