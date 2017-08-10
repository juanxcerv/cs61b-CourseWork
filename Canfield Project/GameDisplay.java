package canfield;

import ucb.gui.Pad;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.imageio.ImageIO;

import java.io.InputStream;
import java.io.IOException;

/** A widget that displays a Pinball playfield.
 *  @author P. N. Hilfinger
 */
class GameDisplay extends Pad {

    /** Color of display field. */
    private static final Color BACKGROUND_COLOR = Color.white;

    /* Coordinates and lengths in pixels unless otherwise stated. */

    /** Preferred dimensions of the playing surface. */
    private static final int BOARD_WIDTH = 900, BOARD_HEIGHT = 900;

    /** Displayed dimensions of a card image. */
    private static final int CARD_HEIGHT = 150, CARD_WIDTH = 100;

    /** Horizontal Coordinates of cards. */
    private static final int TAF1X = 420, TAF2X = 540, TAF3X = 660,
        TAF4X = 780, RES = 20, WASTE = 160, STOCK = 20;

    /** Vertical Coordinates of cards in game. */
    private static final int HIGHY = 100, MIDY = 350, LOWY = 550,
        MIDY_OTHER = 320;

    /** Other necessary declarations. */
    private static final int ONE = 1, TWO = 2, THREE = 3, FOUR = 4, ZERO = 0,
        STOCK_STR = 540, OTHER_STR = 340, TABLEAUY_STR = 620, FOUNDX_STR = 620,
        STOCKX_STR = 50, WASTEX_STR = 195, RESERVEX_STR = 42, FOUNDY_STR = 90,
        THIRTY = 30, OTHER = 340;

    /** A graphical representation of GAME. */
    public GameDisplay(Game game) {
        _game = game;
        setPreferredSize(BOARD_WIDTH, BOARD_HEIGHT);
    }

    /** Return an Image read from the resource named NAME. */
    private Image getImage(String name) {
        InputStream in =
            getClass().getResourceAsStream("/canfield/resources/" + name);
        try {
            return ImageIO.read(in);
        } catch (IOException excp) {
            return null;
        }
    }

    /** Return an Image of CARD. */
    private Image getCardImage(Card card) {
        return getImage("playing-cards/" + card + ".png");
    }

    /** Return an Image of the back of a card. */
    private Image getBackImage() {
        return getImage("playing-cards/blue-back.png");
    }

    /** Draw CARD at X, Y on G. */
    private void paintCard(Graphics2D g, Card card, int x, int y) {
        if (card != null) {
            g.drawImage(getCardImage(card), x, y,
                        CARD_WIDTH, CARD_HEIGHT, null);
        }
    }

    /** Draw card back at X, Y on G. */
    private void paintBack(Graphics2D g, int x, int y) {
        g.drawImage(getBackImage(), x, y, CARD_WIDTH, CARD_HEIGHT, null);
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        g.setColor(BACKGROUND_COLOR);
        Rectangle b = g.getClipBounds();
        g.fillRect(ZERO, ZERO, b.width, b.height);
        g.setColor(Color.black);
        paintCard(g, _game.topReserve(), RES, MIDY);
        paintCard(g, _game.topWaste(), WASTE, LOWY);
        g.drawRect(TAF4X, HIGHY, CARD_WIDTH, CARD_HEIGHT);
        g.drawRect(TAF3X, HIGHY, CARD_WIDTH, CARD_HEIGHT);
        g.drawRect(TAF2X, HIGHY, CARD_WIDTH, CARD_HEIGHT);
        paintBack(g, RES, LOWY);
        g.drawRect(WASTE, LOWY, CARD_WIDTH, CARD_HEIGHT);
        g.drawString("Stock", STOCKX_STR, STOCK_STR);
        g.drawString("Waste", WASTEX_STR, STOCK_STR);
        g.drawString("Tableau", TABLEAUY_STR, OTHER_STR);
        g.drawString("Reserve", RESERVEX_STR, OTHER);
        g.drawString("Foundation", FOUNDX_STR, FOUNDY_STR);
        Card top1 = _game.topFoundation(ONE);
        Card top2 = _game.topFoundation(TWO);
        Card top3 = _game.topFoundation(THREE);
        Card top4 = _game.topFoundation(FOUR);
        for (int cardY = MIDY_OTHER, j = _game.tableauSize(ONE); j >= ZERO;
            j -= ONE, cardY += THIRTY) {
            paintCard(g, _game.getTableau(ONE, j), TAF1X, cardY);
        }
        for (int cardY = MIDY_OTHER, j = _game.tableauSize(TWO); j >= ZERO;
            j -= ONE, cardY += THIRTY) {
            paintCard(g, _game.getTableau(TWO, j), TAF2X, cardY);
        }
        for (int cardY = MIDY_OTHER, j = _game.tableauSize(THREE); j >= ZERO;
            j -= ONE, cardY += THIRTY) {
            paintCard(g, _game.getTableau(THREE, j), TAF3X, cardY);
        }
        for (int cardY = MIDY_OTHER, j = _game.tableauSize(FOUR); j >= ZERO;
            j -= ONE, cardY += THIRTY) {
            paintCard(g, _game.getTableau(FOUR, j), TAF4X, cardY);
        }
        if (top1 != null) {
            paintCard(g, top1, TAF1X, HIGHY);
        }
        if (top2 != null) {
            paintCard(g, top2, TAF2X, HIGHY);
        }
        if (top3 != null) {
            paintCard(g, top3, TAF3X, HIGHY);
        }
        if (top4 != null) {
            paintCard(g, top4, TAF4X, HIGHY);
        }
    }

    /** Game I am displaying. */
    private final Game _game;

}
