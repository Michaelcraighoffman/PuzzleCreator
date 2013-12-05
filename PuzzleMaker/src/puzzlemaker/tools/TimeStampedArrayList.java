package puzzlemaker.tools;

import java.util.ArrayList;

/** An ArrayList coupled with a time stamp (System.currentTimeMillis() at instantiation).
 * The time stamp is used for {@link java.lang.Comparable comparison}.
 * 
 * @author Sam */
public class TimeStampedArrayList<E> extends ArrayList<E> implements Comparable<TimeStampedArrayList<E>> {
	private static final long serialVersionUID = 7599564760278487793L;
	
	private long m_timeStamp;
	
	public TimeStampedArrayList () {
		super();
		m_timeStamp = System.currentTimeMillis();
	}
	
	public TimeStampedArrayList(int capacity) {
		super(capacity);
		m_timeStamp = System.currentTimeMillis();
	}
	
	public long getTimeStamp() {
		return m_timeStamp;
	}

	@Override
	public boolean equals (Object o) {
		if (o instanceof TimeStampedArrayList<?>) {
			if (m_timeStamp == ((TimeStampedArrayList<?>)o).getTimeStamp()) {
				return true;
			}
		}

		return false;
	}
	
	@Override
	public int compareTo(TimeStampedArrayList<E> o) {
		return (int) (m_timeStamp - o.getTimeStamp());
	}

	@Override
	public String toString() {
		return super.toString() + ((m_timeStamp / 1000) % 10000);
	}
}
