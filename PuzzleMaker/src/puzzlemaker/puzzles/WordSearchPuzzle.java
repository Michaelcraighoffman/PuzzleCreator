package puzzlemaker.puzzles;

import java.util.Random;

public class WordSearchPuzzle extends Puzzle {

	public WordSearchPuzzle(String[] wordList) {
		super();
		
		// Find out how big the puzzle has to be
		//TODO: This starting value may not be necessary with a sophisticated generate() algorithm.
		int minSize = wordList.length;
		for (String word : wordList) {
			if (word.length() > minSize) {
				minSize = word.length();
			}
		}
		System.err.println("Wordsearch minsize = " + minSize);
		
		// "New" all of the CharacterFields of m_grid
		instantiatePuzzle(minSize);
		
		generate(wordList);
	}

	@Override
	public void generate(String[] wordList) {
		Random rand = new Random(System.currentTimeMillis());
		
		for (int i = 0; i < m_grid.size(); i++) {
			for (int j = 0; j < m_grid.get(0).size(); j++) {
				if (i < wordList.length) {
					if (j < wordList[i].length()) {
						m_grid.get(i).get(j).setText(wordList[i].charAt(j));
					}
					else {
						m_grid.get(i).get(j).setText((char) (rand.nextInt(26) + 65));
					}
				}
				else
				{
					m_grid.get(i).get(j).setText((char) (rand.nextInt(26) + 65));
				}
			}
		}
	}

	@Override
	public void showSolution() {
		
	}

	@Override
	public void hideSolution() {
		
	}
}
