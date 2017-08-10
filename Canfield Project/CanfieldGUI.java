package canfield;

import ucb.gui.TopLevel;
import ucb.gui.LayoutSpec;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/** A top-level GUI for Canfield solitaire.
 *  @author Juan Cervantes
 */
class CanfieldGUI extends TopLevel {
    /** SIZES. */
    static final int WIDTH = 300, HEIGHT = 300;
    /** Checks to see if waste was pressed. */
    boolean stockPressed = false;
    /** checks to see if reserve was pressed. */
    boolean reservePressed = false;
    /** checks to see if first tableau was pressed. */
    boolean tableau1Pressed = false;
    /** checks to see if second tableau was pressed. */
    boolean tableau2Pressed = false;
    /** checks to see if third tableau was pressed. */
    boolean tableau3Pressed = false;
    /** checks to see if fourth tableau was pressed. */
    boolean tableau4Pressed = false;
    /** checks to see if first foundation was pressed. */
    boolean foundation1Pressed = false;
    /** checks to see if second foundation was pressed. */
    boolean foundation2Pressed = false;
    /** checks to see if third foundation was pressed. */
    boolean foundation3Pressed = false;
    /** checks to see if fourth foundation was pressed. */
    boolean foundation4Pressed = false;

    /** A new window with given TITLE and displaying GAME. */
    CanfieldGUI(String title, Game game) {
        super(title, true);
        _game = game;
        _display = new GameDisplay(game);
        addLabel("Field of Cans",
                 new LayoutSpec("y", 0, "x", 0));
        addMenuButton("Data->Quit", "quit");
        addMenuButton("Data->Undo", "undo");
        add(_display, new LayoutSpec("y", 2, "width", 2));
        _display.setMouseHandler("click", this, "mouseClicked");
        _display.setMouseHandler("release", this, "mouseReleased");
        _display.setMouseHandler("drag", this, "mouseDragged");
        _display.setMouseHandler("press", this, "mousePressed");
        tableaus =  new ArrayList<Boolean>();
        tableaus.add(tableau1Pressed);
        tableaus.add(tableau2Pressed);
        tableaus.add(tableau3Pressed);
        tableaus.add(tableau4Pressed);
        foundations = new ArrayList<Boolean>();
        foundations.add(foundation1Pressed);
        foundations.add(foundation2Pressed);
        foundations.add(foundation3Pressed);
        foundations.add(foundation4Pressed);
        display(true);
    }
    /** Respond to "Quit" button. */
    public void quit(String dummy) {
        System.exit(1);
    }
    /** Goes back a state in the game. */
    public void undo(String dummy) {
        _game.undo();
        _display.repaint();
    }
    /** Action in response to mouse-clicking event EVENT. */
    public synchronized void mouseClicked(MouseEvent event) {
        if (event.getX() >= STOCK && event.getX() <= STOCK_INCR
            && event.getY() >= LOWY && event.getY() <= LOWY_INCR) {
            _game.stockToWaste();
        }
        _display.repaint();
    }

    /** Action in response to mouse-released event EVENT. */
    public synchronized void mouseReleased(MouseEvent event) {
        for (int width = TAF1X, i = 1, height = MIDY; i < FIVE; i += 1,
            width += WP) {
            if (stockPressed == true && event.getX() >= width
                && event.getX() <= width + WPC
                && event.getY() >= height && event.getY() <= height + HP) {
                _game.wasteToTableau(i);
            }
        }
        for (int width = TAF1X, i = 1, height = HIGHY; i < FIVE; i += 1,
            width += WP) {
            if (stockPressed == true && event.getX() >= width
                && event.getX() <= width + WPC
                && event.getY() >= height && event.getY() <= height + HP) {
                _game.wasteToFoundation();
            }
        }
        for (int width = TAF1X, i = 1, height = HIGHY; i < 5; i += 1,
            width += WP) {
            if (reservePressed == true && event.getX() >= width
                && event.getX() <= width + WPC && event.getY() >= height
                && event.getY() <= height + HP) {
                _game.reserveToFoundation();
            }
        }
        for (int i = 0; i < FOUR; i += 1) {
            if (tableaus.get(i) == true) {
                for (int width = TAF1X, j = 1, height = MIDY; j < 5;
                    j += 1, width += WP) {
                    if (event.getX() >= width && event.getX() <= width + WPC
                        && event.getY() >= height
                        && event.getY() <= height + HP) {
                        _game.tableauToTableau(i + 1, j);
                    }
                }
            }
        }
        for (int width = TAF1X, i = 1, height = MIDY; i < 5; i += 1,
            width += WP) {
            if (reservePressed == true && event.getX() >= width
                && event.getX() <= width + WPC && event.getY() >= height
                    && event.getY() <= height + HP) {
                _game.reserveToTableau(i);
            }
        }
        for (int i = 0; i < 4; i += 1) {
            if (tableaus.get(i) == true) {
                for (int width = TAF1X, j = 1, height = HIGHY; j < 5;
                    j += 1, width += WP) {
                    if (event.getX() >= width && event.getX() <= width + WPC
                        && event.getY() >= height
                        && event.getY() <= height + HP) {
                        _game.tableauToFoundation(j);
                    }
                }
            }
        }
        for (int i = 0; i < 4; i += 1) {
            if (foundations.get(i) == true) {
                for (int width = TAF1X, j = 1, height = MIDY; j < 5;
                    j += 1, width += WP) {
                    if (event.getX() >= width && event.getX() <= width + WPC
                        && event.getY() >= height
                        && event.getY() <= height + HP) {
                        _game.foundationToTableau(i + 1, j);
                    }
                }
            }
        }
        makeFalse();
        _display.repaint();
    }

    /** Action in response to mouse-dragging event EVENT. */
    public synchronized void mouseDragged(MouseEvent event) {
        _display.repaint();
    }
    /** Action in response to mouse-pressing event EVENT. */
    public synchronized void mousePressed(MouseEvent event) {
        if (event.getX() >= WASTE && event.getX() <= WASTE_INCR
            && event.getY() >= LOWY && event.getY() <= LOWY_INCR) {
            stockPressed = true;
        }
        if (event.getX() >= RES && event.getX() <= RES_INCR
            && event.getY() >= MIDY && event.getY() <= MIDY_INCR) {
            reservePressed = true;
        }
        if (event.getX() >= TAF1X && event.getX() <= TAF1X_INCR
            && event.getY() >= MIDY && event.getY() <= MIDY_INCR) {
            tableaus.set(0, true);
        }
        if (event.getX() >= TAF2X && event.getX() <= TAF2X_INCR
            && event.getY() >= MIDY && event.getY() <= MIDY_INCR) {
            tableaus.set(1, true);
        }
        if (event.getX() >= TAF3X && event.getX() <= TAF3X_INCR
            && event.getY() >= MIDY && event.getY() <= MIDY_INCR) {
            tableaus.set(2, true);
        }
        if (event.getX() >= TAF4X && event.getX() <= TAF4X_INCR
            && event.getY() >= MIDY && event.getY() <= MIDY_INCR) {
            tableaus.set(3, true);
        }
        if (event.getX() >= TAF1X && event.getX() <= TAF1X_INCR
            && event.getY() >= HIGHY && event.getY() <= HIGHY_INCR) {
            foundations.set(4, true);
        }
        if (event.getX() >= TAF2X && event.getX() <= TAF2X_INCR
            && event.getY() >= HIGHY && event.getY() <= HIGHY_INCR) {
            foundations.set(1, true);
        }
        if (event.getX() >= TAF3X && event.getX() <= TAF3X_INCR
            && event.getY() >= HIGHY && event.getY() <= HIGHY_INCR) {
            foundations.set(2, true);
        }
        if (event.getX() >= TAF4X && event.getX() <= TAF4X_INCR
            && event.getY() >= HIGHY && event.getY() <= HIGHY_INCR) {
            foundations.set(3, true);
        }
        _display.repaint();
    }
    /** Turns all pressed statements false after release. */
    private void makeFalse() {
        stockPressed = false;
        reservePressed = false;
        tableaus.set(ZERO, false);
        tableaus.set(ONE, false);
        tableaus.set(TWO, false);
        tableaus.set(THREE, false);
        foundations.set(ZERO, false);
        foundations.set(ONE, false);
        foundations.set(TWO, false);
        foundations.set(THREE, false);
    }
    /** List of tableau pressed booleans. */
    private ArrayList<Boolean> tableaus;
    /** List of foundation pressed booleans. */
    private ArrayList<Boolean> foundations;
    /** Horizontal Coordinates of cards. */
    private static final int TAF1X = 420, TAF2X = 540, TAF3X = 660, TAF4X = 780,
        RES = 20, WASTE = 160, STOCK = 20;
    /** Vertical Coordinates of cards in game. */
    private static final int HIGHY = 100, MIDY = 350, LOWY = 550,
        MIDY_OTHER = 320;
    /** Incrementations and additions to vertical cards. */
    private static final int HIGHY_INCR = 250, MIDY_INCR = 500,
        LOWY_INCR = 700;
    /** Increments and additions to horizontal cards. */
    private static final int TAF1X_INCR = 520, TAF2X_INCR = 640,
        TAF3X_INCR = 760, TAF4X_INCR = 880, RES_INCR = 120, WASTE_INCR = 260,
        STOCK_INCR = 120;
    /** Other necessary declarations where P = plus and C = current. */
    private static final int ONE = 1, TWO = 2, THREE = 3, FOUR = 4, ZERO = 0,
        FIVE = 5, HP = 150, WP = 120, WPC = 100;
    /** The board widget. */
    private final GameDisplay _display;
    /** The game I am consulting. */
    private final Game _game;

}
