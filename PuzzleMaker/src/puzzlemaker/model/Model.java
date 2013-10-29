package puzzlemaker.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.swing.JFrame;

import puzzlemaker.Constants;
import puzzlemaker.puzzles.Puzzle;
import puzzlemaker.puzzles.Word;
import puzzlemaker.tools.PuzzleGenerator;
import puzzlemaker.tools.TimeStampArrayList;

public class Model {
	private TreeMap<TimeStampArrayList<String>, ConcurrentSkipListSet<Puzzle>> m_data;
	private TimeStampArrayList<String> m_selectedWordList;
	private Puzzle m_selectedPuzzle;
	private Iterator<TimeStampArrayList<String>>  m_wordIter;
	private Iterator<Puzzle> m_puzzleIter;
	private Iterator<Puzzle> m_puzzlePrev;
	private TimeStampArrayList<String> m_wordPrev;
	//private <TimeStampArrayList<String>> m_wordElement;
	private ArrayList<JFrame> m_views;
	
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
	public void generatePuzzles(byte puzzleType) {
		if (m_selectedWordList.size() > 0) {
			m_generator.setPuzzleType(puzzleType);
			System.err.println("Starting puzzle generator.");
			 m_generator.start(m_data.get(m_selectedWordList));
			
		}
		else {
			System.err.println("Invalid word list size.");
		}
		 m_selectedPuzzle = m_data.get(m_selectedWordList).first();
		 m_puzzleIter = m_data.get(m_selectedWordList).iterator();
		 m_puzzlePrev = m_puzzleIter;
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
	public Puzzle getNextPuzzle(){
		if(m_puzzleIter.hasNext())
			m_selectedPuzzle = m_puzzleIter.next();
		else{
			m_selectedPuzzle = m_data.get(m_selectedWordList).first();
			m_puzzleIter = m_data.get(m_selectedWordList).iterator();
		}
		return m_selectedPuzzle;
	}
	
	// TODO: Do I have to update the word list
	public ArrayList<String> getNextWordList() {
		m_selectedWordList = m_data.higherKey(m_selectedWordList);
		if (m_selectedWordList == null) {
			m_selectedWordList = m_data.firstKey();
		}
		System.err.println(m_selectedWordList.toString());
		return m_selectedWordList;
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
	public ArrayList<String> getPreviousWordList() {
		m_selectedWordList = m_data.lowerKey(m_selectedWordList);
		if (m_selectedWordList == null) {
			m_selectedWordList = m_data.lastKey();
		}
		System.err.println(m_selectedWordList.toString());
		return m_selectedWordList;
	}
	
	public ArrayList<String> getNewWordList() {
		m_selectedWordList = new TimeStampArrayList<String>();
		m_data.put(m_selectedWordList, new ConcurrentSkipListSet<Puzzle>());
		return m_selectedWordList;
	}
	
	
	
	@Deprecated
	/**
	 * I'm just using this right now for the Export function (which prints all of the solutions to a text file).
	 * Feel free to repurpose it, but it would be nice not to have to clone a solution list with (potentially)
	 * tens of thousands of objects.
	 * 
	 * @author Sam
	 * @return
	 */
	public ArrayList<Puzzle> getSolutions() {
		return new ArrayList<Puzzle>(m_data.get(m_selectedWordList));
	}
	
	
	public void printTreeMap() {
		System.err.println(m_data.toString());
	}

	public Puzzle getPuzzle() {
		// TODO Auto-generated method stub
		return m_selectedPuzzle;
	}
}
