package puzzlemaker.model;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.swing.JOptionPane;

import puzzlemaker.Constants;
import puzzlemaker.puzzles.Puzzle;
import puzzlemaker.tools.PuzzleGenerator;
import puzzlemaker.tools.TimeStampArrayList;

public class Model {
	private TreeMap<TimeStampArrayList<String>, ConcurrentSkipListSet<Puzzle>> m_data;
	private TimeStampArrayList<String> m_selectedWordList;
	private Puzzle m_selectedPuzzle;
	
	private PuzzleGenerator m_generator;
	
	public Model() {
		m_data = new TreeMap<TimeStampArrayList<String>, ConcurrentSkipListSet<Puzzle>>();
		m_selectedWordList = new TimeStampArrayList<String>();
		m_data.put(m_selectedWordList, new ConcurrentSkipListSet<Puzzle>());
		
		m_generator = new PuzzleGenerator(this);
	}
	
	/** Begins generating puzzles using {@linkplain PuzzleGenerator}.
	 * 
	 * @see Constants#TYPE_CROSSWORD
	 * @see Constants#TYPE_WORDSEARCH
	 */
	public int getNumPuzzles(){
		return m_data.get(m_selectedWordList).size();
	}
	
	public void startPuzzleGenerator(byte puzzleType) {
		if (m_selectedWordList.size() > 0) {
			m_generator.setPuzzleType(puzzleType);
//			System.err.println("Starting puzzle generator.");
			 m_generator.start(m_data.get(m_selectedWordList));
			
		}
		else {
			System.err.println("Invalid word list size.");
			JOptionPane.showMessageDialog(null, "Invalid word list size.");
		}
		
//		m_selectedPuzzle = m_data.get(m_selectedWordList).first(); // This threw a NoSuchElementException for me. -Sam
//		m_puzzleIter = m_data.get(m_selectedWordList).iterator();
//		m_puzzlePrev = m_data.get(m_selectedWordList).descendingIterator();
	}
	
	public void stopPuzzleGenerator() {
		m_generator.stop();
	}
	
	public boolean isPuzzleGeneratorRunning() {
		return m_generator.isRunning();
	}
	
	public boolean addWord(String word) {
		word = Constants.filterWord(word);
		
		if (word == null) {
			return false;
		}
		
		return m_selectedWordList.add(word);
	}
	
	public boolean removeWord(String word) {
		return m_selectedWordList.remove(word);
	}
	
	public ArrayList<String> getWordList() {
		return (ArrayList<String>) m_selectedWordList;
	}
	
	/**
	 * Moves the selected puzzle up the tree
	 * @return m_selectedPuzzle
	 */
	public Puzzle getNextPuzzle() 
	{	
		m_selectedPuzzle = m_data.get(m_selectedWordList).higher(m_selectedPuzzle);
		if (m_selectedPuzzle == null) {
			m_selectedPuzzle = m_data.get(m_selectedWordList).first();
		}
		return m_selectedPuzzle;
	}
	/**
	 * Moves the selected Puzzle down the tree
	 * @return m_selectedPuzzle
	 */
	public Puzzle getPreviousPuzzle() 
	{
		m_selectedPuzzle = m_data.get(m_selectedWordList).lower(m_selectedPuzzle);
		if (m_selectedPuzzle == null) {
			m_selectedPuzzle = m_data.get(m_selectedWordList).last();
		}
		return m_selectedPuzzle;
	}
	
	/**
	 * Obtains the first puzzle of the current wordList
	 * @return m_selectedPuzzle
	 */
	public Puzzle getCurrentWordPuzzle(){
		if (!m_data.get(m_selectedWordList).isEmpty()) {
			m_selectedPuzzle = m_data.get(m_selectedWordList).first();
			return m_selectedPuzzle;
		}
		else {
			return null;
		}
	}
	
	public ArrayList<String> getNextWordList() {
		m_selectedWordList = m_data.higherKey(m_selectedWordList);
		if (m_selectedWordList == null) {
			m_selectedWordList = m_data.firstKey();
		}
//		System.err.println(m_selectedWordList.toString());
		return m_selectedWordList;
	}
	/**
	 * This and a few other things not sure if I'm keeping, still wrapping 
	 * my head around things
	*/
	public boolean hasNext(){
		if(m_data.higherKey(m_selectedWordList) == null)
			return false;
		else 
			return true;
	}
	public ArrayList<String> getPreviousWordList() {
		m_selectedWordList = m_data.lowerKey(m_selectedWordList);
		if (m_selectedWordList == null) {
			m_selectedWordList = m_data.lastKey();
		}
//		System.err.println(m_selectedWordList.toString());
		return m_selectedWordList;
	}
	
	public ArrayList<String> getNewWordList() {
		m_selectedWordList = new TimeStampArrayList<String>();
		m_data.put(m_selectedWordList, new ConcurrentSkipListSet<Puzzle>());
		return m_selectedWordList;
	}
	
	public void setMinPuzzleSize(boolean enabled, int x, int y) {
		m_generator.setMinPuzzleSize(enabled, x, y);
	}
	
	public void setMaxPuzzleSize(boolean enabled, int x, int y) {
		m_generator.setMaxPuzzleSize(enabled, x, y);
	}

	public void setExactlPuzzleSize(boolean enabled, int x, int y) {
		m_generator.setExactlPuzzleSize(enabled, x, y);
	}
	
	/**
	 * I'm just using this right now for the Export function (which prints all of the solutions to a text file).
	 * Feel free to repurpose it, but it would be nice not to have to clone a solution list with (potentially)
	 * tens of thousands of objects.
	 * 
	 * @author szeren
	 */
	public ArrayList<Puzzle> getSolutions() {
		return new ArrayList<Puzzle>(m_data.get(m_selectedWordList));
	}
	
	public void printTreeMap() {
		System.err.println(m_data.toString());
	}

	public Puzzle getPuzzle() {
		return m_selectedPuzzle;
	}

	public void clearSelectedPuzzle() {
		m_selectedPuzzle=null;		
	}

	public void setAllowNonSquare(boolean allowed) {
		m_generator.setAllowNonSquare(allowed);
	}
}
