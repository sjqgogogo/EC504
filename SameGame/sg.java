package edu.bu.ec504.spr19.sameGame;

/* An implementation of the "samegnome" game, possibly with a computer-aided solver.
   Written by Prof. Ari Trachtenberg for EC504 at Boston University.
*/


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import edu.bu.ec504.spr19.highScores.highScore;
import edu.bu.ec504.spr19.Brain.Brain;
import edu.bu.ec504.spr19.Brain.lazyBrain;
import edu.bu.ec504.spr19.Brain.simpleBrain;

class sg extends GUI implements ActionListener,ItemListener {

	// constants
	static public final int defaultWidth=15, defaultHeight=10; // sizes in terms of numbers of circles
	static public final int defaultWindowWidth=500, defaultWindowHeight=300; // default window size, if run as a standalone application
	static public final int numColors = 3; // the number of colors available for circles
	static public final int displayTime = 1000; // number of milliseconds to display (highlight) a move before actually making it
	static public final String highScoreFile=".highscores.db"; // where high scores are kept
	private static final long serialVersionUID = 1L; // required for serializability

	// fields (all static - only one instance of the game should be running at a time)
	final JLabel regionPoints = new JLabel("0"); // keeps track of the number of selected points
	final JLabel totalPoints = new JLabel("0");  // keeps track of the total number of points so far
	private int width = defaultWidth, height=defaultHeight; // initial width and height of the array of circles
	private SelfAwareCircle circles[][];
	private String highScoreName = null;    // the name to use for the high score

	// ... GUI elements
	private JPanel circlePanel; // the panel containing the circles
	private JPanel textPanel;   // the panel containing scoring information
	// ... ... menu items
	private JMenuItem changeBoard;
	private JMenuItem quitGame;
	private JRadioButtonMenuItem noPlayerMenuItem,simplePlayerMenuItem, lazyPlayerMenuItem, // various automatic player options
		smarterPlayerMenuItem;

	// ... Brain elements
	Brain theBrain;                   // the Brain (automated player) for the game
	Thread brainThread;               // the thread that will run the Brain

	// Public access methods

	/*
	 * Default no-args constructor
	 */
	public sg() {
        super("Ari's samegame");
		try {
			SwingUtilities.invokeAndWait(
					new Runnable() {
						public void run() {
							setupGUI();
						}
					}
			);
		}
		catch (Exception e) { System.out.println("Saw exception "+e); }
	}

	/*
	 * Returns the color of the circle at location [xx][yy], or NONE if the circle has been cleared
	 * @param xx must be between 0 and width
	 * @param yy must be between 0 and height
	 */
	public CircleColor colorAt(int xx, int yy) {
		if (circles[xx][yy].isCleared())
			return CircleColor.NONE;
		else
			return circles[xx][yy].clr;
	}

	/*
	 * Returns the width of the current board
	 */
	public int boardWidth() {
		return width;
	}

	/*
	 * Returns the height of the current board
	 */
	public int boardHeight() {
		return height;
	}

	/*
	 * Returns true iff the game is over
	 * (i.e. every circle is surrounded by cleared circles or circles of a different color)
	 */
	public boolean gameOverQ() {
		for (int xx=0; xx<width; xx++)
			for (int yy=0; yy<height; yy++) {
				if (!circles[xx][yy].isCleared()) {
					CircleColor myColor = circles[xx][yy].getColor();
					// check its neighbors
					if (sameColor(myColor,xx-1,yy) ||
							sameColor(myColor,xx+1,yy) ||
							sameColor(myColor,xx,yy-1) ||
							sameColor(myColor,xx,yy+1))
						return false; // the game has not ended
				}
			}

		return true; // there are no viable moves
	}

	/*
	 * "Clicks" on the circle at location (xx,yy)
	 */
	public synchronized void makeMove(int xx, int yy) {

		circles[xx][yy].mouseEntered(null);  // pretend the mouse was pressed at location (xx,yy)
		try {
			Thread.sleep(displayTime);          // wait a bit for the user to register the move
		} catch (InterruptedException e) { }
		circles[xx][yy].mouseExited(null);
		circles[xx][yy].mousePressed(null);
		circles[xx][yy].mouseReleased(null); // pretend that the mouse button was released at the location
	}

	/**
	 * @return the score achieved from clicking on a region of length <b>level</b>
	 */
	final public int score(int level, CircleColor theColor) {
	    int tmp;

        if (level==1)
            return -10;
        else
            tmp = (int) (level * Math.log(level));
        switch (theColor) {
            case Red:
                return -tmp;
            case Green: case Blue:
                return tmp;
            default:
                return 0;
        }
	}


	/*
	 * setupGUI helper
	 *   - adds to circle[ii][jj] a SelfAwareListener of circle[xx][yy] as 0<=xx<width and 0<=yy<height
	 * @param ii = x coordinate of the circle to whom we will be listening
	 * @param jj = y coordinate of the circle to whom we will be listening
	 * @param xx = x coordinate of the listener circle
	 * @param yy = y coordinate of the listener circle
	 */

	private void addNeighbor(int ii, int jj, int xx, int yy) {
		if ((0<=xx && xx<width) &&
				(0<=yy && yy<height))
			circles[ii][jj].addSelfAwareListener(circles[xx][yy]);
	}

	/**
	 * Setup the initial screen items
	 * NOTE:  must perform cleanUp() first if there is already a GUI set up on the screen
	 */
	void setupGUI() {

		// set the layout style for the whole app
		setLayout(new BorderLayout());

		// ... set up the menus
		JMenuBar menuBar = new JMenuBar();

		// ...... File Menu
		JMenu menu = new JMenu("File");

		changeBoard = new JMenuItem("Change board");
		changeBoard.addActionListener(this);
		menu.add(changeBoard);

		quitGame = new JMenuItem("Quit");
		quitGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.META_MASK));
		quitGame.setMnemonic('Q');
		quitGame.addActionListener(this);
		menu.add(quitGame);

		menuBar.add(menu);

		// ...... Automatic play Menu
		menu = new JMenu("Automatic play");
		ButtonGroup group = new ButtonGroup();
		
		noPlayerMenuItem = new JRadioButtonMenuItem("none");
		noPlayerMenuItem.addItemListener(this);
		noPlayerMenuItem.setSelected(true); // i.e. the default item
		group.add(noPlayerMenuItem);
		menu.add(noPlayerMenuItem);
		menu.addSeparator();
		
		simplePlayerMenuItem = new JRadioButtonMenuItem("Simple player");
		simplePlayerMenuItem.addItemListener(this);
		group.add(simplePlayerMenuItem);
		menu.add(simplePlayerMenuItem);

		lazyPlayerMenuItem = new JRadioButtonMenuItem("Lazy player");
		lazyPlayerMenuItem.addItemListener(this);
		group.add(lazyPlayerMenuItem);
		menu.add(lazyPlayerMenuItem);

		smarterPlayerMenuItem = new JRadioButtonMenuItem("Smarter player");
		smarterPlayerMenuItem.setEnabled(false); // disabled for this version
		smarterPlayerMenuItem.addItemListener(this);
		group.add(smarterPlayerMenuItem);
		menu.add(smarterPlayerMenuItem);
		menuBar.add(menu);

		this.setJMenuBar(menuBar);

		// ... set up the circle panel
		circlePanel = new JPanel(new GridLayout(height,width));
		// allocate circles
		circles = new SelfAwareCircle[width][height];

		// ..... set up some circles
		for (int jj=0; jj<height; jj++) 
			for (int ii=0; ii<width; ii++) 
			{
				CircleColor foreColor = CircleColor.randColor(numColors);

				// establish the button
				circles[ii][jj] = new SelfAwareCircle(foreColor,ii,jj,this);
				circlePanel.add(circles[ii][jj]);
			}
		// set up listeners in the area
		for (int xx=0; xx<width; xx++)
			for (int yy=0; yy<height; yy++) {
				// add all neighbors
				addNeighbor(xx,yy,xx,yy-1);
				addNeighbor(xx,yy,xx,yy+1);
				addNeighbor(xx,yy,xx-1,yy);
				addNeighbor(xx,yy,xx+1,yy);
			}

		add(circlePanel,BorderLayout.CENTER);

		// ... set up the text panel
		textPanel = new JPanel( new FlowLayout());
		textPanel.add(new JLabel("Selected len: "));
		textPanel.add(regionPoints);
		textPanel.add(new JLabel("")); // i.e. blank
		textPanel.add(new JLabel("Total pts: "));
		textPanel.add(totalPoints);
		add(textPanel,BorderLayout.SOUTH);

		validate();
		repaint();
	}

	/*
	 * Performs any clean up actions needed before setting up a new GUI
	 */
	void cleanUp() {
		// Removes elements for garbage collection

		// close down the brain
		highScoreName=null;
		if (theBrain != null)
			theBrain.allDone();

		// score panel
		totalPoints.setText("0");
		textPanel.setVisible(false);
		textPanel=null;

		// circles
		for (int ii=0; ii<width; ii++)
			for (int jj=0; jj<height; jj++) {
				circles[ii][jj].removeMouseListener(circles[ii][jj]);
				circles[ii][jj]=null;
			}

		// circle panel
		circlePanel.setVisible(false);
		circlePanel=null;

	}

	/*
	 * Returns true iff circles[xx][yy] has color theColor, is not cleared, AND (xx,yy) is within the range of the board
	 */
	private boolean sameColor(CircleColor theColor, int xx, int yy) {
		if (xx<0 || yy<0 || xx>=width || yy>=height || circles[xx][yy].isCleared())
			return false;
		else
			return circles[xx][yy].getColor().equals(theColor);
	}

	/*
	 * Moves circle c1 into location c2, leaving c1 as a clear circle that
	 * does not receive mouse events
	 */
	private void moveCircle(SelfAwareCircle orig, SelfAwareCircle dest) {
		// copy the immutable, position independent values
		orig.copy(dest);

		// clear the top item
		orig.setClear();
	}

	/*
	 * Called to request a reshifting of the board (as necessary).
	 *    This should happen if some circles are rendered "clear"ed
	 *    @return true if game continues; false otherwise
	 */

	final boolean shiftCircles() {
		/* start at the bottom and move up ... all cleared circles are
		 *    removed, with upper circles falling into their positions;
		 *    if a column is totally empty, then its rightmost columns
		 *    shift into it
		 */

		// 1.  SHIFT VERTICALLY
		for (int xx=0; xx<width; xx++) {
			int firstClr = height-1;  // the lowest cleared entry in the column
			int firstFull = height-1; // the lowest uncleared entry in the column that has not yet been processed
			boolean moveOn = false;      // set to true in order to move on to the next column
			while (!moveOn) {
				// find the lowest clear entry in the column (if it exists)
				try {
					while (!circles[xx][firstClr].isCleared())
						firstClr--;
				} catch (ArrayIndexOutOfBoundsException e) {
					moveOn = true;
					continue;  // i.e. no cleared circle found in this column --- go to the next column
				}

				if (firstFull > firstClr)
					firstFull = firstClr; // only move items "down" the column

				// find the lowest non-cleared entry in the column (if it exists)
				try {
					while (circles[xx][firstFull].isCleared())
						firstFull--;
				} catch (ArrayIndexOutOfBoundsException e) {
					moveOn = true;
					continue;  // i.e. the whole column is clear --- for now, go to the next column
				}

				moveCircle(circles[xx][firstFull],circles[xx][firstClr]);

				firstFull--; firstClr--; // iterate
			}
		}

		// 2.  SHIFT HORIZONTALLY
		// Check to see if any column is now empty
		// ... this could have been done within the loop above, but it would detract from readability of the code
		boolean emptySoFar=true;       // remains true if all columns seen so far have only cleared circles
		for (int xx=width-1; xx>=0; xx--) {
			boolean allCleared=true;   // remains true if all circles in column xx have been cleared
			for (int yy=0; yy<height; yy++) {
				try {
					if (!circles[xx][yy].isCleared())
						throw new InterruptedException();
				} catch (InterruptedException e) { allCleared=false; }
			}

			if (allCleared) {
				if (!emptySoFar) { // i.e. do not do anything with empty columns on the right of the screen
					// move other columns into this empty column
					for (int ii=xx+1; ii<width; ii++)
						for (int jj=0; jj<height; jj++)
							moveCircle(circles[ii][jj],circles[ii-1][jj]);
				}
			}
			else
				emptySoFar=false;
		}

		// 3.  (LAST ITEM) CHECK IF THE GAME HAS ENDED
		// This happens if every circle is surrounded by cleared circles or circles of a different color
		if (!gameOverQ())
			return true; // the game has not ended ... go on

		// if we've gotten here, it means that the game has ended
		int score = new Integer(totalPoints.getText());

		highScore hs = new highScore(highScoreFile);
		hs.loadScores();

		// check the high score
		if (hs.newRecordQ(score)) { // i.e. a new record
			JOptionPane.showMessageDialog(null, "Game Over - You got a new high score of "+score+" points!\n"+
					"Your weighted score for this sized board was "+ highScore.hsComp.weightedScore( new highScore.HSdata("",width,height,score))+".");
			if (highScoreName==null)
				highScoreName = JOptionPane.showInputDialog(null,"You got a new high score!\nPlease enter your name:");
			
			if (highScoreName == null) // i.e. the user is not interested in high scores
				return false;
			// populate the high score item
			highScore.HSdata datum = new highScore.HSdata(highScoreName,width,height,score);
			hs.putScore(datum); // enter the name into the high score list

			hs.saveScores();
		}
		else
			JOptionPane.showMessageDialog(null, "Game Over - You did not make the high score.  You had "+score+" points.\n"+
					"Your weighted score for this sized board was "+ highScore.hsComp.weightedScore( new highScore.HSdata("",width,height,score))+".");


		JOptionPane.showMessageDialog(null, "Current high scores:\n"+hs.display());

		return false;

	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == quitGame)
			System.exit(0);
		else if (source == changeBoard) {
			// modified from http://www.vbforums.com/showthread.php?t=513699
			JTextField width = new JTextField(); width.setText(""+defaultWidth);
			JTextField height = new JTextField(); height.setText(""+defaultHeight);
			Object[] msg = {"Width:", width, "Height:", height};

			JOptionPane op = new JOptionPane(
					msg,
					JOptionPane.QUESTION_MESSAGE,
					JOptionPane.OK_CANCEL_OPTION,
					null,
					null);

			JDialog dialog = op.createDialog(this, "Enter new board size ...");
			dialog.setVisible(true);

			int result = JOptionPane.OK_OPTION;

			try
			{
				result = (Integer) op.getValue();
			}
			catch(Exception uninitializedValue)
			{}

			if(result == JOptionPane.OK_OPTION) // i.e. effect the change
			{
				// i.e. destroy the old board
				cleanUp(); 
				this.width = new Integer(width.getText());
				this.height = new Integer(height.getText());

				// create the new board
				setupGUI();
			}
		}
	}

	/**
	 * Starts a Brain that makes moves
	 */
	private void startBrain(Brain theBrain) {
		this.theBrain = theBrain;
		highScoreName = theBrain.myName(); // the computer gets credit for any subsequent high score
		brainThread = new Thread(theBrain,"Brain Thread");
		brainThread.start();
	}
	
	// For handling the menu checkBox
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();

		if (e.getStateChange() == ItemEvent.SELECTED) {
			if (source == noPlayerMenuItem)
				return; // i.e. no players
			else if (source == simplePlayerMenuItem)
				startBrain(new simpleBrain(this));
			else if (source == lazyPlayerMenuItem)
				startBrain(new lazyBrain(this));
/*
 * The smarterBrain is missing from this code :-)
*/
		}
		else {
			// deselected
			if (theBrain!=null)
				theBrain.allDone(); // will ask the Brain to stop thinking
		}
	}

	/**
	 * Runs the application
	 */
	public static void main(String args[]) {
		JFrame myApp = new sg();
		myApp.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		myApp.setSize(defaultWindowWidth, defaultWindowHeight);
		myApp.setVisible(true);
	}
}