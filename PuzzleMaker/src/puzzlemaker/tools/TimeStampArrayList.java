package puzzlemaker.tools;

import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public class TimeStampArrayList<E> extends ArrayList<E> implements Comparable<TimeStampArrayList> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7599564760278487793L;
	private long m_timeStamp;
	
	public TimeStampArrayList () {
		super();
		m_timeStamp = System.currentTimeMillis();
	}
	
	public TimeStampArrayList(int capacity) {
		super(capacity);
		m_timeStamp = System.currentTimeMillis();
	}
	
	public long getTimeStamp() {
		return m_timeStamp;
	}

	@Override
	public boolean equals (Object o) {
		if (o instanceof TimeStampArrayList) {
			TimeStampArrayList t = (TimeStampArrayList) o;
			if (m_timeStamp == t.getTimeStamp()) {
				return true;
			}
		}

		return false;
		// Always return false since we never want to collide with another word list in the model's tree.
	}
	
	@Override
	public int compareTo(TimeStampArrayList o) {
		return (int) (m_timeStamp - o.getTimeStamp());
	}

	@Override
	public String toString() {
		return super.toString() + ((m_timeStamp / 1000) % 10000);
	}
}
