package edu.uwm.cs351.util;

/**
 * Collect statistics for a changing value.
 * This class is thread safe.
 */
public class Statistics {
	private int count;
	private double min, max;
	private double sum;
	
	/**
	 * Collect another value for this statistic.
	 * @param value current value of the statistic
	 */
	public synchronized void add(double value) {
		if (count == 0) {
			min = max = sum = value;
		} else {
			if (min > value) min = value;
			if (max < value) max = value;
			sum += value;
		}
		++count;
	}
	
	/**
	 * Get the number of data points collected so far.
	 * @return number of data points collection so far.
	 */
	public synchronized int count() { return count; }
	
	/**
	 * Get the minimum value seen so far.
	 * @return minimum value seen so far.
	 */
	public synchronized double getMin() { return min; }
	
	/**
	 * Get the maximum value seen so far.
	 * @return maximum value seen so far
	 */
	public synchronized double getMax() { return max; }
	
	/**
	 * Get the arithmetic mean of values presented so far.
	 * @return arithmetic mean of values so far
	 */
	public synchronized double getAverage() {
		return (count == 0) ? 0.0 : sum/count;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public synchronized String toString() {
		if (count == 0) return "NA";
		return "["+String.format( "%.2f", min )+", "+
				   String.format( "%.2f", max )+
				"]  count["+count+"]  avg[" + String.format( "%.3f", getAverage())+"]";
	}
}
