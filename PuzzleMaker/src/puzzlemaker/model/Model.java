package puzzlemaker.model;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.swing.JOptionPane;

import puzzlemaker.Constants;
import puzzlemaker.puzzles.Puzzle;
import puzzlemaker.tools.PuzzleGenerator;
import puzzlemaker.tools.TimeStampedArrayList;
import puzzlemaker.tools.WordCluePair;

public class Model {
	private TreeMap<TimeStampedArrayList<WordCluePair>, ConcurrentSkipListSet<Puzzle>> m_data;
	private TimeStampedArrayList<WordCluePair> m_selectedWordList;
	private Puzzle m_selectedPuzzle;
	
	private PuzzleGenerator m_generator;
	
	public Model() {
		m_data = new TreeMap<TimeStampedArrayList<WordCluePair>, ConcurrentSkipListSet<Puzzle>>();
		m_selectedWordList = new TimeStampedArrayList<WordCluePair>();
		m_data.put(m_selectedWordList, new ConcurrentSkipListSet<Puzzle>());
		
		m_generator = new PuzzleGenerator(this);
	}
	
	// PuzzleGenerator related methods. *******************
	
	/** Begins generating puzzles using {@linkplain PuzzleGenerator}.
	 * 
	 * @see Constants#TYPE_CROSSWORD
	 * @see Constants#TYPE_WORDSEARCH
	 * 
	 * @author Samuel Wiley
	 */
	public void startPuzzleGenerator(byte puzzleType) {
		if (m_selectedWordList.size() > 0) {
			m_generator.setPuzzleType(puzzleType);
			m_generator.start(m_data.get(m_selectedWordList));
			
		}
		else {
			System.err.println("Invalid word list size.");
			JOptionPane.showMessageDialog(null, "Invalid word list size.");
		}
	}
	
	public void stopPuzzleGenerator() {
		m_generator.stop();
	}
	
	public boolean isPuzzleGeneratorRunning() {
		return m_generator.isRunning();
	}
		
	public void printTreeMap() {
		System.err.println(m_data.toString());
	}
	
	public void setAllowNonSquare(boolean allowed) {
		m_generator.setAllowNonSquare(allowed);
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
	
	// Word list related methods. ************************
	
	public boolean addWord(String word) {
		word = Constants.filterWord(word);
		
		if (word == null) {
			return false;
		}
		
		return m_selectedWordList.add(new WordCluePair(word));
	}
	
	public boolean removeWord(String word) {
		for (WordCluePair w : m_selectedWordList) {
			if (w.getWord().equals(word)) {
				m_selectedWordList.remove(w);
				return true;
			}
		}
		return false;
//		return m_selectedWordList.remove(word);
	}
	
	public ArrayList<WordCluePair> getWordCluePairList() {
		return m_selectedWordList;
	}
	
	public ArrayList<String> getWordList() {
		// Instead of making the whole program conform to the new class this
		// late in the game, we'll just sacrifice some memory. -Sam
		return convertToStringList(m_selectedWordList);
	}
	
	public ArrayList<String> getNewWordList() {
		m_selectedWordList = new TimeStampedArrayList<WordCluePair>();
		m_data.put(m_selectedWordList, new ConcurrentSkipListSet<Puzzle>());

		return convertToStringList(m_selectedWordList);
	}
	
	public ArrayList<String> getNextWordList() {
		m_selectedWordList = m_data.higherKey(m_selectedWordList);
		if (m_selectedWordList == null) {
			m_selectedWordList = m_data.firstKey();
		}
//		System.out.println(m_selectedWordList.toString());
		return convertToStringList(m_selectedWordList);
	}
	
	public ArrayList<String> getPreviousWordList() {
		m_selectedWordList = m_data.lowerKey(m_selectedWordList);
		if (m_selectedWordList == null) {
			m_selectedWordList = m_data.lastKey();
		}
//		System.out.println(m_selectedWordList.toString());
		return convertToStringList(m_selectedWordList);
	}
	
	public ArrayList<String> convertToStringList(ArrayList<WordCluePair> wordCluePairList) {
		ArrayList<String> returnValue = new ArrayList<String>(wordCluePairList.size());
		for (WordCluePair w : wordCluePairList) {
			returnValue.add(w.getWord());
		}
		return returnValue;
	}
	
	// Puzzle related methods. ***************************
	
	public Puzzle getPuzzle() {
		return m_selectedPuzzle;
	}
	
	/** Obtains the first puzzle of the current wordList. 
	 * @author Samuel Wiley*/
	public Puzzle getFirstWordPuzzle(){
		if (!m_data.get(m_selectedWordList).isEmpty()) {
			m_selectedPuzzle = m_data.get(m_selectedWordList).first();
			return m_selectedPuzzle;
		}
		else {
			return null;
		}
	}
	
	/** Selects the next (higher value) puzzle. 
	 * @author Samuel Wiley*/
	public Puzzle getNextPuzzle() 
	{	
		m_selectedPuzzle = m_data.get(m_selectedWordList).higher(m_selectedPuzzle);
		if (m_selectedPuzzle == null) {
			m_selectedPuzzle = m_data.get(m_selectedWordList).first();
		}
		return m_selectedPuzzle;
	}
	
	/** Selects the previous (lower value) puzzle.
	 * @author Samuel Wiley */
	public Puzzle getPreviousPuzzle() 
	{
		m_selectedPuzzle = m_data.get(m_selectedWordList).lower(m_selectedPuzzle);
		if (m_selectedPuzzle == null) {
			m_selectedPuzzle = m_data.get(m_selectedWordList).last();
		}
		return m_selectedPuzzle;
	}
	
	public void clearSelectedPuzzle() {
		m_selectedPuzzle=null;		
	}
	
	public int getNumPuzzles(){
		return m_data.get(m_selectedWordList).size();
	}
	
	/**
	 * I'm just using this right now for the Export function (which prints all of the solutions to a text file).
	 * Feel free to repurpose it, but it would be nice not to have to clone a solution list with (potentially)
	 * tens of thousands of objects.
	 * 
	 * @author Samuel Wiley
	 */
	public ArrayList<Puzzle> getSolutions() {
		return new ArrayList<Puzzle>(m_data.get(m_selectedWordList));
	}
	

	/**
	 * This and a few other things not sure if I'm keeping, still wrapping 
	 * my head around things
	*/
	public boolean hasNext(){
		if(m_data.higherKey(m_selectedWordList) == null)
			return true;
		else 
			return false;
	}	
}
