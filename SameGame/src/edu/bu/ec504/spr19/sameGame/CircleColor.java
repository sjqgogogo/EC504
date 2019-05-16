package edu.bu.ec504.spr19.sameGame;

import java.awt.Color;
import java.util.Random;

/*
 * Records the color of one circle
 */

public enum CircleColor {
	NONE(new Color(0, 0, 0)),
	Red(new Color(255, 0, 0)),
    Green(new Color(0, 255, 0)),
    Blue(new Color(0, 0, 255)),
    Pink(new Color(255,0,255));

	// fields
	private final Color myColor;
	private static final Random randGen = new Random();        // allocate Random number generator

	// Constructor
	CircleColor(Color myColor) {
		this.myColor = myColor;
	}

	// Accessors
	Color getColor() {
		return myColor;
	}

	/*
	 * Generate a random color from the first num possible colors (excluding NONE)
	 * @param num The number of colors from which to choose; must be at most 1 + # of enums in circleColor
	 */
	static CircleColor randColor(int num) {
		if (num < 1 || num >= CircleColor.values().length)
			throw new IndexOutOfBoundsException("Only " + CircleColor.values().length + " colors are available.  You requested choosing one of " + num + " colors.");

		int randNum = 1 + Math.abs(randGen.nextInt()) % num;
		return CircleColor.values()[randNum];

	}
}
