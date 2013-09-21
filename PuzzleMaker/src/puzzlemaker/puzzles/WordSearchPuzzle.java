package puzzlemaker.puzzles;

import java.util.ArrayList;
import java.util.Random;

public class WordSearchPuzzle extends Puzzle {

	public WordSearchPuzzle(ArrayList<String> wordList) {
		super();

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
		instantiatePuzzle(minSize);

		generate(wordList);
		
		updateDisplayPanel();
	}

	@Override
	public void generate(ArrayList<String> wordList) {

		// Set boundaries to '0'
		for (int i = 0; i < m_grid.get(0).size(); ++i) {
			m_grid.get(0).get(i).setText('0');
			m_grid.get((m_grid.size() - 1)).get(i).setText('0');
			m_grid.get(i).get(0).setText('0');
			m_grid.get(i).get(m_grid.size() - 1).setText('0');
		}
		for(int i = 1; i < m_grid.size() - 1; ++i){
			for(int j = 1; j < m_grid.get(0).size() - 1; ++j){
				m_grid.get(i).get(j).setText(' ');
			}
		}

		Random randomNumber = new Random();
		int x = 0;
		int y = 0;
		String currentWord;
		boolean pass;
		
		int tries = 0;

		for (int i = 0; i < wordList.size(); ++i) {

			if (++tries == 300000) {
				System.err.println("tries = max"); // probably stuck.
				return;
			}
			
			pass = true;
			x = randomNumber.nextInt((m_grid.size() - 1) + 1);
			y = randomNumber.nextInt((m_grid.get(0).size() - 1) + 1);
			currentWord = wordList.get(i);
			
			System.err.println("word: " + currentWord + "; X: " + x + " Y: " + y);
		
			if (m_grid.get(x).get(y).getText().equals("0")) {
				--i;
				pass = false;
				
			} else {

			   /** 0 = left to right<br>
				 * 1 = top left to botom right<br>
				 * 2 = top to bottom<br>
				 * etc, etc (clockwise rotation) */
				int setDirection = randomNumber.nextInt(8);

				for (int j = 0; j < currentWord.length(); ++j) {
					
					switch (setDirection) {

					case 0:
						if (m_grid.get(x - j).get(y).getText().equals("0") || (!(m_grid.get(x-j).get(y).getText().equals(" ")) && !(m_grid.get(x-j).get(y).getText().equals(currentWord.substring(j,(j+1)))))) {
							pass = false;
							break;
						}
						
						break;

					case 1:
						if (m_grid.get(x - j).get(y + j).getText().equals("0")|| (!(m_grid.get(x-j).get(y+j).getText().equals(" ")) && !(m_grid.get(x-j).get(y+j).getText().equals(currentWord.substring(j,(j+1)))))) {
							pass = false;
							
							break;
						}
						
						break;

					case 2:
						if (m_grid.get(x).get(y + j).getText().equals("0")|| (!(m_grid.get(x).get(y+j).getText().equals(" ")) && !(m_grid.get(x).get(y+j).getText().equals(currentWord.substring(j,(j+1)))))) {
							pass = false;
						
							break;
						}
						
						break;

					case 3:
						if (m_grid.get(x + j).get(y + j).getText().equals("0")|| (!(m_grid.get(x+j).get(y+j).getText().equals(" ")) && !(m_grid.get(x+j).get(y+j).getText().equals(currentWord.substring(j,(j+1)))))) {
							pass = false;
							
							break;
						}
						
						break;

					case 4:
						if (m_grid.get(x + j).get(y).getText().equals("0")|| (!(m_grid.get(x+j).get(y).getText().equals(" ")) && !(m_grid.get(x+j).get(y).getText().equals(currentWord.substring(j,(j+1)))))) {
							pass = false;
							
							break;
						}
						
						break;

					case 5:
						if (m_grid.get(x + j).get(y - j).getText().equals("0")|| (!(m_grid.get(x+j).get(y-j).getText().equals(" ")) && !(m_grid.get(x+j).get(y-j).getText().equals(currentWord.substring(j,(j+1)))))) {
							pass = false;
						
							break;
						}
						
						break;

					case 6:
						if (m_grid.get(x).get(y - j).getText().equals("0") || (!(m_grid.get(x).get(y-j).getText().equals(" ")) && !(m_grid.get(x).get(y-j).getText().equals(currentWord.substring(j,(j+1)))))) {
							pass = false;
							
							break;
						}
						
						break;

					case 7:
						if (m_grid.get(x - j).get(y - j).getText().equals("0") || (!(m_grid.get(x-j).get(y-j).getText().equals(" ")) && !(m_grid.get(x-j).get(y-j).getText().equals(currentWord.substring(j,(j+1)))))) {
							pass = false;
							break;
						}
						
						break;

					}
					if (pass == false) {
						--i;
						break;
					}
					if(j == currentWord.length()-1){
						placeWord(currentWord,setDirection , x , y);
					}
					
				}
			}
		}
		
		eraseBoundaries(); // since they currently contain '0'
		
		fillIn();		
	}
	
	private void eraseBoundaries() {
		m_grid.remove(0);
		m_grid.remove(m_grid.size() - 1);
		
		for (int i = 0; i < m_grid.size(); i++) {
			m_grid.get(i).remove(0);
			m_grid.get(i).remove(m_grid.get(i).size() - 1);
		}
	}
	
	/** Fill in remaining grid spaces with random letters. */
	private void fillIn(){
		Random rand = new Random();
		
		for(int i = 0; i < m_grid.size(); ++i){
			for(int j = 0; j < m_grid.get(0).size(); ++j){
				if(m_grid.get(i).get(j).getText().equals(" ")){
					m_grid.get(i).get(j).setText((char) (rand.nextInt(26) + 65));
				}
			}
		}
	}
	
	
	private void placeWord(String word , int caseDirection, int x , int y){
		
		for(int i = 0; i < word.length(); ++i){
		switch(caseDirection){
		
		case 0:
			m_grid.get(x - i).get(y).setText(word.charAt(i));
			break;
		
		case 1:
			m_grid.get(x - i).get(y + i).setText(word.charAt(i));
			break;
		
		case 2:
			m_grid.get(x).get(y + i).setText(word.charAt(i));
			break;
			
		case 3:
			m_grid.get(x + i).get(y + i).setText(word.charAt(i));
			break;
			
		case 4:
			m_grid.get(x + i).get(y).setText(word.charAt(i));
			break;
			
		case 5:
			m_grid.get(x + i).get(y - i).setText(word.charAt(i));
			break;
			
		case 6:
			m_grid.get(x).get(y - i).setText(word.charAt(i));
			break;
			
		case 7:
			m_grid.get(x - i).get(y - i).setText(word.charAt(i));
			break;
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
