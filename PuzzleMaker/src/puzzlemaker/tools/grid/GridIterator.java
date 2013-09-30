package puzzlemaker.tools.grid;

import puzzlemaker.Constants;

// TODO: make this a class contained by a Grid class to facilitate object disposal, making memory leak management easier
public class GridIterator {

	private Grid m_grid;
	protected int x, y, m_direction;
	
	public GridIterator (Grid grid) {
		m_grid = grid;
	}
	
	public GridIterator(Grid grid, int startX, int startY, int direction) {
		m_grid = grid;
		x = startX;
		y = startY;
		m_direction = direction;
	}
	
	public GridIterator(GridIterator iter) {
		m_grid = iter.getGrid();
		x = iter.x;
		y = iter.y;
		m_direction = iter.m_direction;
	}
	
	/** Copy constructor helper. */
	protected Grid getGrid() {
		return m_grid;
	}
	
	/** Note that "next" is relative to this iterator's direction.
	 * @param <T>
	 * 
	 * @return <b>true</b> if movement was successful.<br>
	 * <b>false</b> if movement would have exceeded grid boundaries. 
	 */
	public char next() {
		switch (m_direction) {
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

			default:
				System.err.println("invalid direction: " + m_direction);
				return '?';
		}
		
		return m_grid.getCharAt(x, y);
	}
	
	public boolean hasNext() {
		switch (m_direction) {
		case Constants.LEFT_TO_RIGHT:
			if (x + 1 >= m_grid.getWidth()) {
				return false;
			}
			break;

		case Constants.TOPLEFT_TO_BOTTOMRIGHT:
			if (x + 1 >= m_grid.getWidth() || y + 1 >= m_grid.getHeight()) {
				return false;
			}
			break;

		case Constants.TOP_TO_BOTTOM:
			if (y + 1 >= m_grid.getHeight()) {
				return false;
			}
			break;

		case Constants.TOPRIGHT_TO_BOTTOMLEFT:
			if (y + 1 >= m_grid.getHeight() || x - 1 < 0) {
				return false;
			}
			break;

		case Constants.RIGHT_TO_LEFT:
			if (x - 1 < 0) {
				return false;
			}
			break;

		case Constants.BOTTOMRIGHT_TO_TOPLEFT:
			if (y - 1 < 0 || x - 1 < 0) {
				return false;
			}
			break;

		case Constants.BOTTOM_TO_TOP:
			if (y - 1 < 0) {
				return false;
			}
			break;
			
		case Constants.BOTTOMLEFT_TO_TOPRIGHT:
			if (y - 1 < 0 || x + 1 >= m_grid.getWidth()) {
				return false;
			}
			break;

		default:
			System.err.println("invalid direction: " + m_direction);
			return false;
		}
		return true;
	}
	
	public char previous() {
		turnAround();
		char result = next();
		turnAround();
		return result;
	}
	
	public boolean hasPrevious() {
		turnAround();
		boolean result = hasNext();
		turnAround();
		return result;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getDirection() {
		return m_direction;
	}
	
	public void setX(int newX) {
		x = newX;
	}
	
	public void setY(int newY) {
		y = newY;
	}
	
	public void setDirection(int dir) {
		m_direction = dir;
	}
	
	private void turnAround() {
		m_direction = (m_direction + 4) % 8;
	}
	
	/** Rotates left 45 degrees.
	 * @return The new direction. */
	public int turnLeft() {
		m_direction = (m_direction + 7) % 8;
		return m_direction;
	}
	
	/** Rotates right 45 degrees.
	 * @return The new direction. */
	public int turnRight() {
		m_direction = (m_direction + 1) % 8;
		return m_direction;
	}
	
	public void dispose() {
		m_grid = null;
	}
	
	@Override
	public String toString() {
		return "GridIterator:  row = " + y + ", col = " + x + ", dir = " + m_direction;
	}
}
