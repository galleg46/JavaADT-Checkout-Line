package edu.uwm.cs351;

import java.awt.Color;
import java.awt.Graphics;
import java.util.RandomAccess;

import edu.uwm.cs351.util.Utilities;
import edu.uwm.cs351.util.Utilities.Computation;
import edu.uwm.cs351.util.Queue;

/**
 * A simulation of a cash register.
 * It has a queue and a thread for operating on the queue.
 * The queue itself is "owned" by the Swing thread:
 * it should only be accessed inside the UI thread.
 * If code needs to access it (to run a method or to copy it),
 * the code should do that inside of a {@link edu.uwm.cs351.util.Computation}
 * or {@link Runnable} passed to {@link Utilities#computeUI(Computation)
 * or {@link Utilities#invokeUI(Runnable)} respectively.
 * @author boyland
 *
 */
public class CashRegister implements Runnable {
	private final int maximumItems;	
	Queue<Shopper> queue = new Queue<Shopper>();
	
	public CashRegister(int max) { maximumItems = max; }
	
	public void enqueue(final Shopper shopper) {
		// Add a new shopper to the queue. Remember that the
		// queue can only be accessed inside the UI thread.
		Utilities.invokeUI(() -> queue.enqueue(shopper));
	}

	public int getMaximum() { return maximumItems; }

	public int getLength() {
		// Return the number of shoppers in the queue.  Remember that the
		// queue can only be accessed inside the UI thread.
		int length = Utilities.computeUI(() -> queue.size());
		return length;
	}

	public int getTotalItems() {
		// Make a copy of the queue and then count up the total number of
		// items of all the shoppers in the queue.  Remember that the
		// queue can only be accessed inside the UI thread.
		
		Queue<Shopper> copy = Utilities.computeUI(() -> queue.clone());
		if(copy.isEmpty()) return 0;
		
		int lineSize = copy.size();
		int totalItems = 0;
		for(int i  = 0; i < lineSize; ++i) {
			// get items from queue front.
			// add values to total
		}
		
		return totalItems;
		
	}

	private static int secondsPerItem = 10;
	private static int secondsPerRest = 10;

	public static boolean setParameter(String name, int value) {
		if ("CashRegister.secondsPerItem".equals(name)) {
			secondsPerItem = value;
			return true;
		} else if ("CashRegister.secondsPerRest".equals(name)) {
			secondsPerRest = value;
			return true;
		}
		return false;
	}
	
	private int workingTime;
	private int restingTime;
	
	public void run() {
		while (!Thread.interrupted()) {
			// Figure out if there is a shopper in this queue.
			// If not, rest for {@link #secondsPerRest} seconds,
			// using {@link Clock#pause} and adding to the total
			// {@link #restingTime}.
			// If there is a shopper, figure out how long it
			// takes to empty their baskets (using the number of items they
			// have and the parameter {@link #secondsPerItem}), 
			// pause for that time,
			// and add that time to the total {@link #workingTime}.
			// Finally remove them from the queue, and 
			// record that the shopper is done, using
			// {@link Simulation#recordComplete}.
			// NB: The queue can only be accessed inside of 
			// computations/runnables passed to CS351Utilities static functions.
			
			
		}
	}
	
	public String getProductivity() {
		if (workingTime == 0) return "0%";
		return (int)(100.0*workingTime/((double)workingTime+restingTime)) + "%";
	}

	public static final int XSIZE = 50;
	public static final int YSIZE = 20;
	private static final int SEP = 10;
	
	public void draw(Graphics g, int x, int y) {
		g.setColor(maximumItems < 100 ? Color.RED : Color.BLACK);
		g.drawRect(x, y, XSIZE,YSIZE);
		g.drawString(getProductivity(), x+1, y+YSIZE-1);
		Queue<Shopper> q = queue.clone(); // doesn't need to be computeUI because in Event thread already
		x += SEP + XSIZE;
		while (!q.isEmpty()) {
			x += SEP + q.front().draw(g, x, y);
			q.dequeue();
		}
	}
}
