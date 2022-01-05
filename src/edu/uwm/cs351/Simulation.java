package edu.uwm.cs351;

import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import edu.uwm.cs351.util.Statistics;

public class Simulation implements Runnable {

	public static void main(String[] args) {
		for (int i=0; i < args.length-1; i+=2) {
			setParameter(args[i],Integer.parseInt(args[i+1]));
		}
		new Simulation(); // for side-effect
	}
	
	private final Collection<CashRegister> registers;
	private final Thread thread;
	private final Random random = new Random();
	private final Panel panel = new Panel();
	
	public Simulation() {
		registers = createRegisters();
		Clock.start();
		for (CashRegister r : registers) {
			Thread t = new Thread(r);
			t.start();
		}
		thread = new Thread(this);
		thread.start();
		SwingUtilities.invokeLater(() -> {
				final JFrame frame = new JFrame("Checkout Simulation");		
				frame.setSize(CashRegister.XSIZE+300*Shopper.ITEM_WIDTH,(CashRegister.YSIZE + Panel.SEP)*(registers.size()+1)+2*Panel.SEP);
				frame.setContentPane(panel);
				frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						stop();
					}
				});
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
		});
	}
	
	public void run() {
		while (!Thread.interrupted()) {
			Shopper s;
			if (random.nextInt(100) < percentSmart) 
				s = new SmartShopper(random);
			else
				s = new Shopper(random);
			s.checkout(registers);
			panel.repaint();
			Clock.pause(arrivalSeparation);
		}
	}
	
	public void stop() {
		thread.interrupt(); // not really necessary
		printFinalStats();
		System.exit(0);
	}
	
	private static Statistics allShoppers = new Statistics();
	private static Statistics smartStats = new Statistics();
	private static Statistics regularStats = new Statistics();
	
	public static void recordComplete(final Shopper s) {
		allShoppers.add(s.getNumItems());
		long waitTime = Clock.elapsedTime()-s.getCreationTime();
		if (s instanceof SmartShopper) smartStats.add(waitTime);
		else regularStats.add(waitTime);		
	}
	
	private static void printFinalStats() {
		System.out.println("\n\n\nTotal run time = " + Clock.elapsedTime() + " seconds.\n");
		System.out.println("Basket size: " +  allShoppers);
		System.out.println("-Waiting time-\n Smart:   " + smartStats);
		System.out.println(" Regular: " + regularStats);
		// This final stat shows how well dilation is working. Ideally
		// everything would be 1.000, but there may be variance:
		System.out.println("\nPause length (vs. requested): " + Clock.pauseStats);
	}
	
	private static int arrivalSeparation = 22; // how long between arrivals?
	private static int percentSmart = 50;
	private static int numRegisters = 4;
	private static int numLowItemRegisters = 2;
	private static int itemLimit = 10;
	
	private static void setParameter(String name, int value) {
		if (Shopper.setParameter(name, value)) return;
		if (CashRegister.setParameter(name, value)) return;
		if (Clock.setParameter(name, value)) return;
		if ("Simulation.arrivalSeparation".equals(name)) {
			arrivalSeparation = value;
		} else if ("Simulation.percentSmart".equals(name)) {
			percentSmart = value;
		} else if ("Simulation.numRegisters".equals(name)) {
			numRegisters = value;
		} else if ("Simulation.numLowItemRegisters".equals(name)) {
			numLowItemRegisters = value;
		} else if ("Simulation.itemLimit".equals(name)) {
			itemLimit = value;
		} else {
			System.err.println("unknown parameter: " + name);
		}
	}
	
	/**
	 * Create the low-item-limit registers and also the regular registers.
	 * It uses {@link #numLowItemRegisters} for the first and {@link #numRegisters}
	 * for the second, returning a collection with size equal to the sum of those numbers.
	 * @return collection of registers.
	 */
	private static Collection<CashRegister> createRegisters() {
		Collection<CashRegister> result = new ArrayList<CashRegister>();
		for (int i=0; i < numLowItemRegisters; ++i) {
			result.add(new CashRegister(itemLimit)); // express checkout lanes
		}
		for (int i=0; i < numRegisters; ++i) {
			result.add(new CashRegister(Integer.MAX_VALUE));
		}
		return result;
	}
	
	private class Panel extends JPanel {
		private static final long serialVersionUID = 1L;
		private static final int SEP = 10;
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			int y=SEP;
			for (CashRegister r : registers) {
				r.draw(g, SEP, y);
				y += CashRegister.YSIZE + SEP;
			}
		}
	}
}
