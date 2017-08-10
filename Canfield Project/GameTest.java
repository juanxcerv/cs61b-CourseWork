package canfield;

import static org.junit.Assert.*;
import org.junit.Test;

/** Tests of the Game class.
 *  @author Juan Cervantes
 */

public class GameTest {

    /** Example. */
    @Test
    public void testInitialScore() {
        Game g = new Game();
        g.deal();
        assertEquals(5, g.getScore());
    }
    /** Test undo waste. */
    @Test
    public void testUndoWaste() {
    	Game g = new Game();
    	g.deal();
    	g.stockToWaste();
    	g.undo();
    	assertTrue(g.topWaste() == null);
    }
    /** Test undo with reserveToFoundation. */
    @Test
    public void teststockToWaste() {
    	Game g = new Game();
    	g.seed(3);
    	g.deal();
    	g.stockToWaste();
    	assertEquals("10S", g.topWaste().toString());
    }
    /** Test tableau to tableau. */
    @Test
    public void testtableauToTableau() {
    	Game g = new Game();
    	g.seed(3);
    	g.deal();
    	g.tableauToTableau(1, 2);
    	assertEquals(2, g.tableauSize(2));
    }
    /** Test tableau to foundation. */
    @Test 
    public void testableauToFoundation() {
    Game g = new Game();
    	g.seed(3);
    	g.deal();
    	g.tableauToFoundation(4);
    	assertEquals(1, g.foundationSize(2));
    }
    /** test automatice reserve to Tableau. */
    @Test
    public void testreserveToTableau() {
    	Game g = new Game();
    	g.seed(3);
    	g.deal();
    	g.tableauToTableau(1, 2);
    	assertEquals("8D", g.topReserve().toString());
    	assertEquals("6D", g.topTableau(1).toString());
    }
}
