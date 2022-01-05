package edu.uwm.cs351.util;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public class Utilities {
	/**
	 * Perform an action on the Swing UI thread.
	 * We return when the code has been executed. 
	 * @param r action to run, must not be null
	 */
	public static void invokeUI(Runnable r) {
		if (SwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (InvocationTargetException e) {
				Throwable t = e.getCause();
				if (t instanceof Error) throw (Error)t;
				throw (RuntimeException)t;
			}
		}
	}
	
	/**
	 * Perform a computation on the Swing UI thread.
	 * @param <T> type returned by the computation
	 * @param c computation to perform, must not be null
	 * @return value returned when the computation is done.
	 */
	public static <T> T computeUI(final Computation<T> c) {
		if (SwingUtilities.isEventDispatchThread()) {
			return c.run();
		} else {
			final Cell<T> result = new Cell<T>(null);
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						result.set(c.run());
					}
				});
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return null;
			} catch (InvocationTargetException e) {
				Throwable t = e.getCause();
				if (t instanceof Error) throw (Error)t;
				throw (RuntimeException)t;
			}
			return result.get();
		}
	}
	
	/**
	 * Mutable cell class.
	 * A cell holds a value which can be modified.
	 * @param T type of value in cell.
	 */
	public static class Cell<T> {
		private T value;
		public Cell(T x) { value = x; }
		public T get() { return value; }
		public void set(T x) { value = x; }
	}
	
	/**
	 * Encapsulate a computation to perform, perhaps in a different thread.
	 * @param <T>
	 */
	public interface Computation<T> {
		public T run();
	}
}
