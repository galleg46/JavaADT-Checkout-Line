package edu.uwm.cs351;

import edu.uwm.cs351.util.Statistics;

/**
 * A class representing simulation time.
 * We have a parameter (Time.dilation) that defines how
 * much faster simulation time goes than real time.
 */
public class Clock {

	private static long initialTime = System.currentTimeMillis();
	private static int dilation = 600;
	
	public static boolean setParameter(String name, int value) {
		boolean result = false;
		if ("Time.dilation".equals(name)) {
			dilation = value;
			result = true;
		}
		return result;
	}
	
	public static void start() {
		initialTime = System.currentTimeMillis();
	}
	
	public static Statistics pauseStats = new Statistics();
	
	public static void pause(int seconds) {
		if (seconds == 0) return;
		long before = elapsedTime();
		try {
			Thread.sleep((seconds*1000)/dilation);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		long after = elapsedTime();
		pauseStats.add((after-before)/(double)seconds);
		/*if (seconds + before > after) {
			System.out.println("Wanted delay of " + seconds + ", and got delay of " + (after-before));
		}*/
	}
	
	public static long elapsedTime() {
		return dilation*(System.currentTimeMillis()-initialTime)/1000;
	}
}
