package edu.bu.ec504.spr19.sameGame;

import javax.swing.*;

public abstract class GUI extends JFrame {
	private static final long serialVersionUID = 1L;

	GUI(String s) {
		super(s);
	}

	/*
	 * Returns the color of the circle at location [xx][yy], or NONE if the circle has been cleared
	 * @param xx must be between 0 and width
	 * @param yy must be between 0 and height
	 */
	public abstract CircleColor colorAt(int xx, int yy);

	/*
	 * Returns the width of the current board
	 */
	public abstract int boardWidth();

	/*
	 * Returns the height of the current board
	 */
	public abstract int boardHeight();

	/*
	 * Returns true iff the game is over
	 * (i.e. every circle is surrounded by cleared circles or circles of a different color)
	 */
	public abstract boolean gameOverQ();

	/*
	 * "Clicks" on the circle at location (xx,yy)
	 */
	public abstract void makeMove(int xx, int yy);

	/**
	 * @return the score achieved from clicking on a region of length #regionLen of color #col
	 */
	public abstract int score(int regionLen, CircleColor col);
}
