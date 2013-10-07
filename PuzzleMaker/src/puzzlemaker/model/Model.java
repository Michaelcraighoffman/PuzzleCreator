package puzzlemaker.model;

import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JFrame;

import puzzlemaker.puzzles.Puzzle;
import puzzlemaker.puzzles.Word;
import puzzlemaker.tools.PuzzleGenerator;

public class Model {
	private ArrayList<ArrayList<String>> m_wordLists;
	private int m_wordListsIndex;
	private ArrayList<String> m_wordList;
	 
	private ArrayList<ArrayList<Puzzle>> m_puzzleLists;
	private ArrayList<Puzzle> m_puzzleList;
	private int m_puzzleListIndex;
	private Puzzle m_puzzle;


	
	
	private ArrayList<JFrame> m_views;
	
	private PuzzleGenerator m_generator;
	
	
	
	public Model() {
		m_puzzleLists = new ArrayList<ArrayList<Puzzle>>(1);
		m_puzzleList = new ArrayList<Puzzle>(0);
		m_puzzleLists.add(m_puzzleList);
		m_puzzleListIndex = 0;
		
		m_wordLists = new ArrayList<ArrayList<String>>(1);		
		m_wordList = new ArrayList<String>(0);
		m_wordLists.add(m_wordList);
		m_wordListsIndex = 0;
		
		m_generator = new PuzzleGenerator(this);
	}
	
	/** Begins generating puzzles using {@linkplain PuzzleGenerator}.
	 * 
	 * @see Constants#TYPE_CROSSWORD
	 * @see Constants#TYPE_WORDSEARCH
	 */
	public void generatePuzzles(byte puzzleType) {
		if (m_wordList.size() > 0) {
			m_generator.setPuzzleType(puzzleType);
			System.err.println("Starting puzzle generator.");
			m_generator.start(m_puzzleLists.get(m_wordListsIndex));
		}
		else {
			System.err.println("Invalid word list size.");
		}
	}
	
	
	public boolean addWord(String word) {
		word = Constants.filterWord(word);
		
		if (word == null) {
			return false;
		}
		
		m_wordList.ensureCapacity(m_wordList.size() + 1);
		return m_wordList.add(word);
	}
	
	public boolean removeWord(String word) {
		boolean result = m_wordList.remove(new Word(word));
		m_wordList.trimToSize();
		return result;
	}
	
	public ArrayList<String> getWordList() {
		return m_wordList;
	}
	
	/** Increments {@code m_wordListIndex} and returns the corresponding item from {@code m_wordLists}.
	 * If we are at the end of the list, return a new, empty word list if the current word list is not already empty,
	 * or if the current word list is empty, return to the beginning of {@code m_wordLists}.
	 * @return An updated pointer to {@code m_wordList}.
	 */
	public ArrayList<String> getNextWordList() {
		if (m_wordListsIndex == m_wordLists.size() - 1) {
			System.err.println("case 1");
			if (m_wordList.size() > 0) {
				System.err.println("case 2");
				m_wordList = new ArrayList<String>(0);
				m_wordLists.ensureCapacity(m_wordLists.size() + 1);
				m_wordLists.add(m_wordList);
				m_wordListsIndex = m_wordLists.size() - 1;
			}
			else {
				System.err.println("case 3");
				m_wordListsIndex = 0;
				m_wordList = m_wordLists.get(m_wordListsIndex);
			}
		}
		else {
			System.err.println("case 4");
			m_wordListsIndex++;
			m_wordList = m_wordLists.get(m_wordListsIndex);
		}
		
		System.err.println("word list index: " + m_wordListsIndex);
		return m_wordList;
	}
	
	/** Decrements {@code m_wordListIndex} and returns the corresponding item from {@code m_wordLists}.
	 * If we are at the beginning of the list, return the empty word list at the end of {@code m_wordLists},
	 * or if the word list at the end of {@code m_wordLists} is not empty, create one and return it.
	 * @return An updated pointer to {@code m_wordList}.
	 */
	public ArrayList<String> getPreviousWordList() {
		if (m_wordListsIndex > 0) {
			m_wordListsIndex--;
			m_wordList = m_wordLists.get(m_wordListsIndex);
		}
		else {
			if (m_wordLists.get(m_wordLists.size() - 1).size() == 0) {
				m_wordListsIndex = m_wordLists.size() - 1;
				m_wordList = m_wordLists.get(m_wordListsIndex);
			}
			else {
				m_wordList = new ArrayList<String>(0);
				m_wordLists.ensureCapacity(m_wordLists.size() + 1);
				m_wordLists.add(m_wordList);
				m_wordListsIndex = m_wordLists.size() - 1;
			}
		}
		
		System.err.println("word list index: " + m_wordListsIndex);
		return m_wordList;
	}
	
	
	
	
//	public void addPuzzleWithoutDuplicates(Puzzle newPuzzle) {
//		ArrayList<Word> newWordList = newPuzzle.getWordList();
//		for (ArrayList<Word> wordList : m_wordLists) {
//			if (wordListsAreEqual(wordList, newWordList)) {
//				return;
//			}
//			else {
//				//is grid already in current word list?
//				for (Puzzle puzzle : m_puzzles.get(m_wordLists.indexOf(wordList))) {
//					if (p)
//				}
//			}
//		}
//	}
	
	private boolean wordListsAreEqual(ArrayList<Word> listOne, ArrayList<Word> listTwo) {
		if (listOne.size() == listTwo.size()) {
		nextWord:
			for (Word wordOne : listOne) {
				for (Word wordTwo : listTwo) {
					if (wordOne.toString().equals(wordTwo.toString())) {
						continue nextWord;
					}
				}
				return false;
			}
		}
		
		return true;
	}
	

	
	public Puzzle getPuzzle() {
		return m_puzzle;
	}


	
	static class WordListComparator implements Comparator<ArrayList<Word>> {

		@Override
		public int compare(ArrayList<Word> o1, ArrayList<Word> o2) {
			System.err.println("Comparing: ");
			System.err.println("List 1: " + o1.toString());
			System.err.println("List 2: " + o2.toString());
			
			if (o1.size() == o2.size()) {
				nextWord:
				for (Word wordOne : o1) {
					for (Word wordTwo : o2) {
						if (wordOne.toString().equals(wordTwo.toString())) {
							continue nextWord;
						}
					}
					System.err.println("case 0");
					return 1;
				}
				System.err.println("case 1");
				return 0;
			}
			else {
				System.err.println("case 2");
				return 1;
			}
		}
	}
}
