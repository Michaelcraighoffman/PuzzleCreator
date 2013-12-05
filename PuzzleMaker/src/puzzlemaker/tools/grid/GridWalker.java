package puzzlemaker.tools.grid;

import puzzlemaker.Constants;
import puzzlemaker.puzzles.Word;

/** 
 * Similar to GridIterator, except doesn't hold a pointer to its Grid and doesn't return Grid elements on next().
 * Iteration pattern is left to right then top to bottom, rotated as appropriate.
 * @author Samuel Wiley
 *
 */
public class GridWalker {
	
	private static final int[][] m_offset = new int[][] {{1,0}, {1,1}, {0,1}, {-1,1}, {-1,0}, {-1,-1}, {0,-1}, {1,-1}};
	
	private int m_width, m_height, lineStartX, lineStartY;
	/** For diagonal traversal, when starting new lines, whether or not
	 * we have passed the corner, thus changing our "direction" to find new lines in.*/
	private boolean diagonal_flag = false;
	
	public int x, y, dir;
	
	public GridWalker(Grid grid) {
		m_width = grid.getWidth();
		m_height = grid.getHeight();
	}
	
	public GridWalker(Grid grid, int startX, int startY, int startDir) {
		m_width = grid.getWidth();
		m_height = grid.getHeight();
		x = startX;
		y = startY;
		dir = startDir;
	}
	
	//Movement
	public void moveForward(int amount) {
		x += m_offset[dir][0] * amount;
		y += m_offset[dir][1] * amount;
	}
	
	public boolean tryNextCell() {
		x += m_offset[dir][0];
		y += m_offset[dir][1];
		
		if (!isInBounds()) {
			return false;
		}
		return true;
	}
	
	public boolean tryNextLine() {
	// Jump to the start of the "line" we just traversed
		x = lineStartX;
		y = lineStartY;
		
		
	// Now we need to shift to the side
		// If we're not moving diagonally, then...
		if ((dir & 1) == 0) {
			x += m_offset[(dir + 2) & 7][0];
			y += m_offset[(dir + 2) & 7][1];
			
			if (!isInBounds()) {
				return false;
			}
		}
		// But if we ARE moving diagonally...
		else {
			if (diagonal_flag == false) {
				x += m_offset[(dir + 3) & 7][0];
				y += m_offset[(dir + 3) & 7][1];
				
				if (!isInBounds()) {
					diagonal_flag = true;
					// This actually jumps back in bounds to the correct spot.
					x += m_offset[dir][0];
					y += m_offset[dir][1];
					
					// Y'know, unless it doesn't because our puzzle's width or height is only 1.
					if (!isInBounds()) {
						return false;
					}
				}
			}
			else {
				x += m_offset[(dir + 1) & 7][0];
				y += m_offset[(dir + 1) & 7][1];

				if (!isInBounds()) {
//					System.out.println(output + " OB");
					return false;
				}
			}
		}
		
		lineStartX = x;
		lineStartY = y;
		return true;
	}
	
	/** "Start" here refers to where, on the grid, we should start iterating based on our direction. */
	public void jumpToStart() {
		diagonal_flag = false;
		switch (dir) {
			case Constants.BOTTOMLEFT_TO_TOPRIGHT:
			case Constants.LEFT_TO_RIGHT:
				x = 0;
				y = 0;
				break;
			case Constants.TOPLEFT_TO_BOTTOMRIGHT:
			case Constants.TOP_TO_BOTTOM:
				x = m_width - 1;
				y = 0;
				break;
			case Constants.TOPRIGHT_TO_BOTTOMLEFT:
			case Constants.RIGHT_TO_LEFT:
				x = m_width - 1;
				y = m_height - 1;
				break;
			case Constants.BOTTOMRIGHT_TO_TOPLEFT:
			case Constants.BOTTOM_TO_TOP:
				x = 0;
				y = m_height - 1;
				break;
		}
		lineStartX = x;
		lineStartY = y;
	}
	
	public boolean isInBounds() {
		if (x < 0 || x >= m_width || y < 0 || y >= m_height) {
			return false;
		}
		return true;
	}
	
	public void rotate(int amount) {
		dir = (dir + amount) & 7;
	}
	
	/**
	 * Assumes that we are now at a cell immediately after the contiguous string of letters "word."
	 * @param startOffset How far into the specified "word" our real word actually is. For examaple, "ONE" is offset by 2 into the word "QKONEF"
	 * @param endOffset How far from the end of the specified "word" our real word ends. For example, "ONE" is offset 1 from the end of the word "QKONEF"
	 */
	public void setWordData(Word word, int startOffset, int endOffset) {
		final int shiftLength = word.toString().length();
		x -= m_offset[dir][0] * shiftLength;
		y -= m_offset[dir][1] * shiftLength;
		
		word.setX(x);
		word.setY(y);
		word.setDirection(dir);
		
		x += m_offset[dir][0] * shiftLength;
		y += m_offset[dir][1] * shiftLength; 
	}
	
}
