package edu.bu.ec504.spr19.Brain;

import edu.bu.ec504.spr19.sameGame.GUI;


/*
 * The Brain is the artificial intelligence that tries to come up with the
 * best possible moves for the current position.
 * 
 * It typically runs in its own thread so that it will not interfere with other processing.
 */
public class simpleBrain extends Brain {
	
	// fields
private volatile boolean allDone = false; // when set to true, the Brain should stop what it's doing and exit (at an appropriate time)
	/* Instantiates a Brain based on a given GUI sg
 * @param sg The GUI that will be instantiating the Brain
 */
	public simpleBrain(GUI myGUI) {
		super(myGUI);
	}
	
	public void allDone() {
		allDone = true;
	}
	
	public String myName() {
		return "Simple Brain";
	}
	
	public void run() {
		while (!allDone && !myGUI.gameOverQ())
				myGUI.makeMove(0, myGUI.boardHeight()-1); // i.e. click on the lower left corner
	}
}
	