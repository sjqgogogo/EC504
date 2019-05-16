package edu.bu.ec504.spr19.sameGame;

import java.util.EventObject;

/*
 * An event that is broadcast by one button to other buttons when the mouse rolls over it
 */
class CircleRolloverEvent extends EventObject {

	// Fields
	private static final long serialVersionUID = 1L;
	/*
	 * the location of the button broadcasting the event
	 */
	private final int xx;
	private final int yy;
	private final CircleColor clr;
	private final SelfAwareCircle.Visibility visibility;
	
	// Constructor(s)
	/*
	 * Records a rollover event on a specific button
	 * @param xx The x coordinate (in the gridlayout of buttons) of the button that was rolled over
	 * @param yy The y coordinate (in the gridlayout of buttons) of the button that was rolled over
	 * @param clr The color of the button that initiated the event
	 * @param visibility Should circles be highlighted or unhighlighted
	 * @param source I'm not sure why I need this ... (used in the super class constructor)
	 */
	public CircleRolloverEvent(Object source, int xx, int yy, CircleColor clr, SelfAwareCircle.Visibility toDo) {
		super(source); // I'm not sure why I need to do this ... but it seems to be important
		this.xx=xx; this.yy=yy; this.clr=clr; this.visibility = toDo;
	}
	
	// Methods
	// ... accessor
	int getXX() { return xx; }
	int getYY() { return yy; }
	SelfAwareCircle.Visibility getAction() { return visibility; }
	CircleColor getColor() { return clr; }


}
