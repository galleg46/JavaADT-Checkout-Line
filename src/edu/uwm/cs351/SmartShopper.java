package edu.uwm.cs351;

import java.awt.Color;
import java.util.Random;

public class SmartShopper extends Shopper {

	public SmartShopper(Random r) { super(r); }

	@Override
	protected int evaluate(CashRegister r) { return r.getTotalItems(); }

	@Override
	protected Color getColor() { return Color.GREEN; }
}
