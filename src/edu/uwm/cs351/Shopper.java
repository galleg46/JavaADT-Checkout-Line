package edu.uwm.cs351;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Random;

public class Shopper {

	private static int averageItems = 10;
	private static int deviation = 10;
	
	public static boolean setParameter(String name, int value) {
		boolean result = false;
		if (name.equals("Shopper.averageItems")) {
			averageItems = value;
			result = true;
		}
		else if (name.equals("Shopper.deviation")) {
			deviation = value;
			result = true;
		}
		return result;
	}
	
	private final int numItems;
	private final long creationTime = Clock.elapsedTime();
	
	public Shopper(Random r) {
		double t = r.nextGaussian()*deviation + averageItems;
		if (t < 0) t = -t;
		numItems = 1+(int)t;
	}
	
	public int getNumItems() { return numItems; }
	public long getCreationTime() { return creationTime; }
	protected Color getColor() { return Color.GRAY; }
	
	public void checkout(Collection<CashRegister> registers) {
		CashRegister choice = null;
		for (CashRegister cr : registers) {
			if (choice == null || better(cr,choice)) {
				choice = cr;
			}
		}
		choice.enqueue(this);
	}
	
	protected boolean better(CashRegister cr1, CashRegister cr2) {
		if (cr2.getMaximum() < numItems) return true;
		if (cr1.getMaximum() < numItems) return false;
		int count1 = evaluate(cr1);
		int count2 = evaluate(cr2);
		return (count1 < count2);
	}
	
	protected int evaluate(CashRegister r) {
		return r.getLength();
	}

	public static final int ITEM_WIDTH = 1;
	public int draw(Graphics g, int x, int y) {
		g.setColor(getColor());
		int width = numItems * ITEM_WIDTH;
		int height = CashRegister.YSIZE;
		g.fillRect(x, y+(CashRegister.YSIZE-height), width, height);
		return width;
	}
}
