package puzzlemaker.tools;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import puzzlemaker.Constants;
import puzzlemaker.model.Model;
import puzzlemaker.puzzles.Crossword;
import puzzlemaker.puzzles.Puzzle;
import puzzlemaker.puzzles.Word;
import puzzlemaker.puzzles.WordSearch;
import puzzlemaker.tools.grid.Grid;

public class PuzzleGenerator {
	private Model m_model;
	
	private byte m_puzzleType;
	private int[] m_validDirections;

	private ForkJoinPool m_threadPool;
	private ConcurrentSkipListSet<Puzzle> m_newSolutions;
	private ConcurrentSkipListSet<Grid> m_inProgressGrids;
	
	private boolean m_debugBool;
	
	private SyncObj m_findLock = new SyncObj(true);
	private long m_totalFind = 0;
	private SyncObj m_addLock = new SyncObj(true);
	private long m_totalAdd = 0;
	private long m_debugStart, m_debugEnd;
	
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
				numProcessorsToUse = Runtime.getRuntime().availableProcessors() - 2;
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
	
	public boolean start(ConcurrentSkipListSet<Puzzle> solutionsList) {
		if (m_validDirections == null) {
			return false;
		}

		m_newSolutions = solutionsList;
		m_inProgressGrids = new ConcurrentSkipListSet<Grid>();
		
		ArrayList<Word> wordList = new ArrayList<Word>(m_model.getWordList().size());
		for (String s : m_model.getWordList()) {
			wordList.add(new Word(s));
		}

		PuzzleTask tmpTask = new PuzzleTask(new Grid(1, 1), wordList);
//		long start = System.currentTimeMillis();
		m_debugBool = true;
		m_debugStart = System.currentTimeMillis();
		tmpTask.quietlyInvoke();
		
//		m_threadPool.shutdown();
//		try {
//			m_threadPool.awaitTermination(5, TimeUnit.SECONDS);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
//		long end = System.currentTimeMillis();
		// Only makes sense to print this stuff if you're running in serial.. otherwise the original task returns pretty quickly.
//		System.err.println("Original task returned.");
//		System.err.println("Unique solutions found: " + m_newSolutions.size());
//		System.err.println("Time elapsed: " + (end - start) + "ms");
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (!m_threadPool.isQuiescent()) {
			m_findLock.doAcquire();
			m_addLock.doAcquire();
			System.err.println("Total find: " + (m_totalFind / 1000) + "s; Total add: " + (m_totalAdd / 1000) + "s; Unique solutions: " + m_newSolutions.size() + "; In progress grids: " + m_inProgressGrids.size() + "; Queued tasks: " + m_threadPool.getQueuedTaskCount());
			m_findLock.doRelease();
			m_addLock.doRelease();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.err.println("Final count: " + m_newSolutions.size() + " unique, " + m_inProgressGrids.size() + " in progress.");
		m_inProgressGrids.clear();
		System.err.println("In-progress grids cleared.");

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
		
//		private long startAddSoln = 0, endAddSoln = 0, startFindValid = 0, endFindValid = 0, totalFind = 0;
		
		public PuzzleTask(Grid grid, ArrayList<Word> wordList) {
			m_grid = grid;

			m_wordList = new ArrayList<Word>(wordList.size());			
			for (Word w : wordList) {
				m_wordList.add(new Word(w));
			}
		}

		
		@Override
		protected Void compute() {
			//Limit ourselves to 1,000 solutions.
			if(m_newSolutions.size()>1000) { return null; }
			
			if (m_wordList.isEmpty()) {
				m_grid.trim();
				if(m_puzzleType==Constants.TYPE_CROSSWORD) {
					Puzzle puzzle = gridToPuzzleIfLegal(m_grid);
					
					if (puzzle != null) {
	//					startAddSoln = System.currentTimeMillis();
						addSolutionWithoutDuplicates(puzzle);
	//					endAddSoln = System.currentTimeMillis();
					}
				}
				else {
					if(m_grid.getWidth()>5 && m_grid.getHeight()>5)
					{
						ArrayList<Word> words=new ArrayList<Word>();
						for(String s : m_model.getWordList()) {
							words.add(new Word(s));
						}
						//TODO: Have to actually find words in WordSearch 
						Puzzle puzzle=new WordSearch(m_grid, words);
						addSolutionWithoutDuplicates(puzzle);
					}
				}
			}
			else {
//				Controls the size of the "in progress" grids.
				if (m_wordList.size() >= (m_model.getWordList().size() / 2)) {
					if (!m_inProgressGrids.add(m_grid)) {
						return null;
					}
				}
				
				int wordIndex = 0;
				while (wordIndex < m_wordList.size()) {
//					startFindValid = System.currentTimeMillis();
					m_validPlacements = findValidPlacements(m_grid, m_wordList.get(wordIndex));
//					endFindValid = System.currentTimeMillis();
					
//					totalFind += endFindValid - startFindValid;
					
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
//							System.err.println("Spawning new task with \nword list: {" + tmpList.toString() + "} \nand Grid:\n" + tmpGrid.toString());

//							In Parallel:
//							Might be useful here to check the threadPool's queued task size.. 
//							because if that number is sufficiently large, we could do something like:
//							while (m_threadPool.getQueuedSubmissionCount() > 200) {
//								helpQuiesce();
//							}
//							This would help us start getting solutions for larger word sets faster (I think.. but I'm not sure) -SBW
							m_threadPool.execute(new PuzzleTask(tmpGrid, tmpList));

//							To serialize for debugging:
//							PuzzleTask tmpTask = new PuzzleTask(tmpGrid, tmpList);
//							tmpTask.quietlyInvoke();
						}
						wordIndex++;
					}
				}
			}
			
//			Useful for seeing if a given section is taking up a lot of time.
//			if (totalFind > 1) {
//				m_findLock.doAcquire();
//				m_totalFind += (totalFind - 1);
//				m_findLock.doRelease();
//			}
//			
//			if ((endAddSoln - startAddSoln) > 1) {
//				m_addLock.doAcquire();
//				m_totalAdd += ((endAddSoln - startAddSoln) - 1);
//				m_addLock.doRelease();
//			}
			
//			if (!m_threadPool.hasQueuedSubmissions()) {
//				if (m_debugBool) {
//					m_debugBool = false;
//					m_debugEnd = System.currentTimeMillis();
//					System.err.println("No queued submissions.");
//					System.err.println("Active thread count: " + m_threadPool.getActiveThreadCount() + " (might be overestimate)");
//					System.err.println("Pool size: " + m_threadPool.getPoolSize());
//					System.err.println("Queued task count: " + m_threadPool.getQueuedTaskCount());
//					System.err.println("Elapsed time: " + ((m_debugEnd - m_debugStart) / 1000) + "s");
//				}
//			}
			return null;
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
					if (grid.isEmpty()) {
						for (int direction : m_validDirections) {
							validGrid = new Grid(grid);
							placeWordInGrid(validGrid, word, 0, 0, direction, 0);
							validGrids.ensureCapacity(validGrids.size() + 1);
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
					if (grid.isEmpty()) {
						for (int direction : m_validDirections) {
							validGrid = new Grid(10,10);
							placeWordInGrid(validGrid, word, 5, 5, direction, 0);
							validGrids.ensureCapacity(validGrids.size() + 1);
							validGrids.add(validGrid);
						}
						return validGrids;
					}
					Random rand=new Random();
					for(int points=0; points<5; points++) {
						int x=rand.nextInt(grid.getWidth()+2)-1;
						int y=rand.nextInt(grid.getHeight()+2)-1;
						int direction=rand.nextInt(m_validDirections.length-1);
						if (!hasIllegalWordSearchIntersections(grid, word, x, y, m_validDirections[direction], 0)) {
							validGrid = new Grid(grid);
							placeWordInGrid(validGrid, word, x, y, m_validDirections[direction], 0);
							validGrids.add(validGrid);
						}
					}
					break;
			}
			
			return validGrids;
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
									currentWord = "";
								}
								else
								{
									return null;
								}
							}
						}
						else {
							currentWord += grid.getCharAt(x, y);
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
							currentWord = "";
						}
						else
						{
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
						
						System.err.println("PuzzleGenerator: case 3");
						foundWordList.add(new Word(currentWord, wordStartX, wordStartY, direction));
						currentWord = "";
					}
					else
					{
						return null;
					}
				}
			}
			
			switch (m_puzzleType) {
			case Constants.TYPE_CROSSWORD:
//				System.err.println("foundWordList: ");
//				for (Word w : foundWordList) {
//					System.err.println(w.toStringDetailed());
//				}
				return new Crossword(grid, foundWordList);
			case Constants.TYPE_WORDSEARCH:
				return new WordSearch(grid, foundWordList);
				
			default:
				System.err.println("Unrecognized puzzle type: " + m_puzzleType);
				System.err.println(Thread.currentThread().getStackTrace());
				return null;
			}		
		}
	}
	
	
	public boolean hasIllegalCrosswordIntersections(Grid grid, Word word, int x, int y, int direction, int offset) {
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
	
	public boolean hasIllegalWordSearchIntersections(Grid grid, Word word, int x, int y, int direction, int offset) {
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
	
	
	
	private void addSolutionWithoutDuplicates (Puzzle newPuzzle) {

//		************** CONCURRENT SKIP LIST SET *************************
		m_newSolutions.add(newPuzzle);
		
		
////		************** UNRESTRICTED SIZE ******************
//		int newPuzzleSize = newPuzzle.getGrid().getWidth() + newPuzzle.getGrid().getHeight();
//		
//		m_solutionLock.doAcquire();
//		
//		
//		if (m_solutions.isEmpty()) {
//			m_solutions.ensureCapacity(m_solutions.size() + 1);
//			m_solutions.add(newPuzzle);
//			m_solutionLock.doRelease();
//			return;
//		}
//		else {
//			int tmpSize;
//			for (int index = m_solutions.size() - 1; index >= 0; index--) {
//				tmpSize = m_solutions.get(index).getGrid().getWidth() + m_solutions.get(index).getGrid().getHeight();
//				if (newPuzzleSize > tmpSize) {
//					continue;
//				}
//				else if (newPuzzleSize == tmpSize) {
//					if (newPuzzle.getGrid().equals(m_solutions.get(index).getGrid())) {
//						m_solutionLock.doRelease();
//						return;
//					}
//				}
//				else {
//					m_solutions.ensureCapacity(m_solutions.size() + 1);
//					m_solutions.add(index, newPuzzle);
//					m_solutionLock.doRelease();
//					return;
//				}
//			}
//			
//			m_solutions.ensureCapacity(m_solutions.size() + 1);
//			m_solutions.add(0, newPuzzle);
//			m_solutionLock.doRelease();
//			return;
//		}
		
//		*********** RESTRICTING TO 50 PUZZLES *************
//		int newPuzzleSize = newPuzzle.getGrid().getWidth() + newPuzzle.getGrid().getHeight();
//		
//		m_solutionLock.doAcquire();
//		if (m_solutions.isEmpty()) {
//			m_solutions.add(newPuzzle);
//			m_solutionLock.doRelease();
//			return;
//		}
//		else if (m_solutions.size() == 50 && newPuzzleSize >= (m_solutions.get(49).getGrid().getWidth() + m_solutions.get(49).getGrid().getHeight())) {
//			m_solutionLock.doRelease();
//			return;
//		}
//		else {	
//			int index, tmpPuzzleSize = 0;
//			for (index = m_solutions.size() - 1; index >= 0; index--) {
//				tmpPuzzleSize = m_solutions.get(index).getGrid().getWidth() + m_solutions.get(index).getGrid().getHeight();				
//				if (newPuzzleSize > tmpPuzzleSize) {
//					m_solutions.ensureCapacity(m_solutions.size() + 1);
//					m_solutions.add(newPuzzle);
//					m_solutionLock.doRelease();
//					return;
//				}
//				else if (newPuzzleSize == tmpPuzzleSize) {
//					if (newPuzzle.getGrid().equals(m_solutions.get(index).getGrid())) {
//						m_solutionLock.doRelease();
//						return;
//					}
//				}
//				else {
//					if (m_solutions.size() == 50) {
//						m_solutions.remove(49);
//					}
//					
//					m_solutions.ensureCapacity(m_solutions.size() + 1);
//					m_solutions.add(index, newPuzzle);
////					System.err.println("Found a puzzle! (" + (m_solutions.size()) + ")");
//					m_solutionLock.doRelease();
//					return;
//				}
//			}
//			if (m_solutions.size() == 50) {
//				m_solutions.remove(49);
//			}
//			m_solutions.ensureCapacity(m_solutions.size() + 1);
//			m_solutions.add(0, newPuzzle);
////			System.err.println("Found a puzzle! (" + (m_solutions.size()) + ")");
//			m_solutionLock.doRelease();
//			return;
//		}
		
	}
}
