package edu.bu.ec504.spr19.Brain;

import edu.bu.ec504.spr19.sameGame.GUI;

/*
 * The Brain is the artificial intelligence that tries to come up with the
 * best possible moves for the current position.
 * 
 * It typically runs in its own thread so that it will not interfere with other processing.
 */
public abstract class Brain implements Runnable {
	// fields
	protected final GUI myGUI; // the GUI class attached to this Brain
	
	// methods
	/*
	 * Constructor - records the GUI attached to this brain
	 */
	Brain(GUI myGUI) { this.myGUI = myGUI; }
	
	/*
	 * Signals that the brain should close down
	 */
	public abstract void allDone();
	
	/*
	 * Returns the name of the brain
	 */
	public abstract String myName();
	
	/* 
	 * Starts up the thread
	 * @see java.lang.Runnable#run()
	 */
	public abstract void run();

}
