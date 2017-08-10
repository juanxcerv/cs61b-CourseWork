package loa;

import org.junit.Test;
import static org.junit.Assert.*;
import static loa.Piece.*;
import static loa.Direction.*;
import static loa.Board.*;
import java.util.Iterator;

public class BoardTest {
    @Test
    public void testWin() {
        Board board = new Board();
        for (int col = 1; col < 9; col++) {
            for (int row = 1; row < 9; row++) {
                board.set(col, row, EMP);
            }
        }
        board.set(2, 2, BP);
        board.set(2, 3, BP);
        assertTrue(board.piecesContiguous(WP));
    }
    @Test
    public void testIterate() {
        Board board = new Board();
        for (int col = 1; col < 9; col++) {
            for (int row = 1; row < 9; row++) {
                board.set(col, row, EMP);
            }
        }
        board.set(4, 3, BP);
        board.set(4, 2, WP);
        board.set(4, 4, WP);
        board.set(1, 2, BP);
        board.set(7, 4, BP);
        Iterator<Move> legal = board.legalMoves();
        for (int i = 0; i < 30; i++) {
            System.out.println(legal.next());
            assertEquals(true, board.isLegal((legal.next())));
            if (!legal.hasNext()) {
                break;
            }
        }
    }
    @Test
    public void initialTest() {
        Board board1 = new Board();
        Board board2 = new Board();
        Direction norS;
        Direction worE;
        Direction neorSW;
        Direction nworSE;
        Move move1 = new Move(4, 2, 4, 4, WP, EMP);
        Move move2 = new Move(4, 3, 3, 3, WP, BP);
        Move move3 = new Move(4, 2, 3, 3, WP, BP);
        Move move4 = new Move(4, 2, 3, 1, WP, BP);
        Move move5 = new Move(5, 1, 5, 5, BP, EMP);
        Move move6 = new Move(6, 1, 6, 3, BP, EMP);
        Move move7 = new Move(6, 1, 6, 3, BP, BP);
        Move move8 = new Move(4, 1, 4, 5, BP, EMP);
        Move move9 = new Move(3, 3, 7, 3, BP, EMP);
        Move move10 = new Move(3, 1, 6, 4, BP, EMP);
        Move move11 = new Move(6, 1, 3, 4, BP, EMP);
        Move move12 = new Move(5, 1, 5, 3, BP, EMP);
        board2.set(2, 8, EMP, null);
        board2.set(4, 3, WP, null);
        board2.set(3, 3, BP, null);
        board2.set(4, 2, WP, null);
        assertTrue(board2.isLegal(move6));
        assertEquals(false, board2.isLegal(move7));
        assertEquals(false, board2.isLegal(move5));
        assertEquals(false, board2.isLegal(move8));
        assertEquals(false, board2.isLegal(move9));
        assertEquals(true, board2.blocked(move9));
        assertEquals(true, board2.blocked(move10));
        assertEquals(true, board2.blocked(move11));
        assertEquals(false, board2.blocked(move12));
        assertEquals(false, board2.isLegal(move10));
        assertEquals(true, board2.isLegal(move12));
        for (Direction d: Direction.values()) {
            if (d.dc == 0 && d.dr == 1 || d.dc == 0 && d.dr == -1) {
                norS = d;
                assertEquals(6, board1.pieceCountAlong(1, 2, norS));
                assertEquals(4, board2.pieceCountAlong(4, 3, norS));
                assertEquals(4, board2.pieceCountAlong(move1));
            } else if (d.dc == 1 && d.dr == 0 || d.dc == -1 && d.dr == 0) {
                worE = d;
                assertEquals(2, board1.pieceCountAlong(1, 2, worE));
                assertEquals(4, board2.pieceCountAlong(4, 3, worE));
                assertEquals(4, board2.pieceCountAlong(move2));
            } else if (d.dc == 1 && d.dr == 1 || d.dc == -1 && d.dr == -1) {
                neorSW = d;
                assertEquals(2, board1.pieceCountAlong(3, 1, neorSW));
                assertEquals(board2.get(4, 2), WP);
                assertEquals(1, board2.pieceCountAlong(3, 3, neorSW));
                assertEquals(3, board2.pieceCountAlong(move4));
                assertEquals(4, board2.pieceCountAlong(move3));
            } else if (d.dc == -1 && d.dr == 1 || d.dc == 1 && d.dr == -1) {
                nworSE = d;
                assertEquals(board2.get(3, 1), BP);
                assertEquals(4, board2.pieceCountAlong(4, 2, nworSE));
            }
        }
    }
}
