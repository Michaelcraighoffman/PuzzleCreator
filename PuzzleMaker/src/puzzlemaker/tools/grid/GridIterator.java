package puzzlemaker.tools.grid;

import puzzlemaker.Constants;

public class GridIterator {

	private Grid m_grid;
	protected int m_x, m_y, m_direction;
	
	public GridIterator (Grid grid) {
		m_grid = grid;
	}
	
	public GridIterator(Grid grid, int startX, int startY, int direction) {
		m_grid = grid;
		m_x = startX;
		m_y = startY;
		m_direction = direction;
	}
	
	public GridIterator(GridIterator iter) {
		m_grid = iter.getGrid();
		m_x = iter.m_x;
		m_y = iter.m_y;
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
				m_x++;
				break;
				
			case Constants.TOPLEFT_TO_BOTTOMRIGHT:
				m_x++;
				m_y++;
				break;

			case Constants.TOP_TO_BOTTOM:
				m_y++;
				break;

			case Constants.TOPRIGHT_TO_BOTTOMLEFT:
				m_x--;
				m_y++;
				break;

			case Constants.RIGHT_TO_LEFT:
				m_x--;
				break;

			case Constants.BOTTOMRIGHT_TO_TOPLEFT:
				m_x--;
				m_y--;
				break;

			case Constants.BOTTOM_TO_TOP:
				m_y--;
				break;

			case Constants.BOTTOMLEFT_TO_TOPRIGHT:
				m_x++;
				m_y--;
				break;

			default:
				System.err.println("invalid direction: " + m_direction);
				return '?';
		}
		
		return m_grid.getCharAt(m_x, m_y);
	}
	
	public boolean hasNext() {
		switch (m_direction) {
		case Constants.LEFT_TO_RIGHT:
			if (m_x + 1 >= m_grid.getWidth()) {
				return false;
			}
			break;

		case Constants.TOPLEFT_TO_BOTTOMRIGHT:
			if (m_x + 1 >= m_grid.getWidth() || m_y + 1 >= m_grid.getHeight()) {
				return false;
			}
			break;

		case Constants.TOP_TO_BOTTOM:
			if (m_y + 1 >= m_grid.getHeight()) {
				return false;
			}
			break;

		case Constants.TOPRIGHT_TO_BOTTOMLEFT:
			if (m_y + 1 >= m_grid.getHeight() || m_x - 1 < 0) {
				return false;
			}
			break;

		case Constants.RIGHT_TO_LEFT:
			if (m_x - 1 < 0) {
				return false;
			}
			break;

		case Constants.BOTTOMRIGHT_TO_TOPLEFT:
			if (m_y - 1 < 0 || m_x - 1 < 0) {
				return false;
			}
			break;

		case Constants.BOTTOM_TO_TOP:
			if (m_y - 1 < 0) {
				return false;
			}
			break;
			
		case Constants.BOTTOMLEFT_TO_TOPRIGHT:
			if (m_y - 1 < 0 || m_x + 1 >= m_grid.getWidth()) {
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
		return m_x;
	}
	
	public int getY() {
		return m_y;
	}
	
	public int getDirection() {
		return m_direction;
	}
	
	public void setX(int newX) {
		m_x = newX;
	}
	
	public void setY(int newY) {
		m_y = newY;
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
		return "GridIterator:  row = " + m_y + ", col = " + m_x + ", dir = " + m_direction;
	}
}
