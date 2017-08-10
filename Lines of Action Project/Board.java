
package loa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Formatter;
import java.util.NoSuchElementException;

import java.util.regex.Pattern;

import static loa.Piece.*;
import static loa.Direction.*;

/** Represents the state of a game of Lines of Action.
 *  @author Juan Cervantes */
class Board implements Iterable<Move> {

    /** Size of a board. */
    static final int M = 8;

    /** Pattern describing a valid square designator (cr). */
    static final Pattern ROW_COL = Pattern.compile("^[a-h][1-8]$");

    /** A Board whose initial contents are taken from INITIALCONTENTS
     *  and in which the player playing TURN is to move. The resulting
     *  Board has
     *        get(col, row) == INITIALCONTENTS[row-1][col-1]
     *  Assumes that PLAYER is not null and INITIALCONTENTS is MxM.
     *
     *  CAUTION: The natural written notation for arrays initializers puts
     *  the BOTTOM row of INITIALCONTENTS at the top.
     */
    Board(Piece[][] initialContents, Piece turn) {
        initialize(initialContents, turn);
    }

    /** A new board in the standard initial position. */
    Board() {
        clear();
    }

    /** A Board whose initial contents and state are copied from
     *  BOARD. */
    Board(Board board) {
        copyFrom(board);
    }

    /** Set my state to CONTENTS with SIDE to move. */
    void initialize(Piece[][] contents, Piece side) {
        _moves.clear();
        for (int r = 1; r <= M; r += 1) {
            for (int c = 1; c <= M; c += 1) {
                set(c, r, contents[r - 1][c - 1]);
            }
        }
        _turn = side;
    }

    /** Set me to the initial configuration. */
    void clear() {
        initialize(INITIAL_PIECES, BP);
    }

    /** Set my state to a copy of BOARD. */
    void copyFrom(Board board) {
        if (board == this) {
            return;
        }
        _moves.clear();
        _moves.addAll(board._moves);
        _turn = board._turn;
        myBoard = board.myBoard;
    }

    /** Return the contents of column C, row R, where 1 <= C,R <= 8,
     *  where column 1 corresponds to column 'a' in the standard
     *  notation. */
    Piece get(int c, int r) {
        return myBoard[r - 1][c - 1];

    }

    /** Return the contents of the square SQ.  SQ must be the
     *  standard printed designation of a square (having the form cr,
     *  where c is a letter from a-h and r is a digit from 1-8). */
    Piece get(String sq) {
        return get(col(sq), row(sq));
    }

    /** Return the column number (a value in the range 1-8) for SQ.
     *  SQ is as for {@link get(String)}. */
    static int col(String sq) {
        if (!ROW_COL.matcher(sq).matches()) {
            throw new IllegalArgumentException("bad square designator");
        }
        return sq.charAt(0) - 'a' + 1;
    }

    /** Return the row number (a value in the range 1-8) for SQ.
     *  SQ is as for {@link get(String)}. */
    static int row(String sq) {
        if (!ROW_COL.matcher(sq).matches()) {
            throw new IllegalArgumentException("bad square designator");
        }
        return sq.charAt(1) - '0';
    }

    /** Set the square at column C, row R to V, and make NEXT the next side
     *  to move, if it is not null. */
    void set(int c, int r, Piece v, Piece next) {
        myBoard[r - 1][c - 1] = v;
        if (next != null) {
            _turn = next;
        }
    }

    /** Set the square at column C, row R to V. */
    void set(int c, int r, Piece v) {
        set(c, r, v, null);
    }

    /** Assuming isLegal(MOVE), make MOVE. */
    void makeMove(Move move) {
        assert isLegal(move);
        _moves.add(move);
        Piece replaced = move.replacedPiece();
        int c0 = move.getCol0(), c1 = move.getCol1();
        int r0 = move.getRow0(), r1 = move.getRow1();
        if (replaced != EMP) {
            set(c1, r1, EMP);
        }
        set(c1, r1, move.movedPiece());
        set(c0, r0, EMP);
        _turn = _turn.opposite();
    }

    /** Retract (unmake) one move, returning to the state immediately before
     *  that move.  Requires that movesMade () > 0. */
    void retract() {
        assert movesMade() > 0;
        Move move = _moves.remove(_moves.size() - 1);
        Piece replaced = move.replacedPiece();
        int c0 = move.getCol0(), c1 = move.getCol1();
        int r0 = move.getRow0(), r1 = move.getRow1();
        Piece movedPiece = move.movedPiece();
        set(c1, r1, replaced);
        set(c0, r0, movedPiece);
        _turn = _turn.opposite();
    }
    /** Returns the last move made without retracting. */
    Move getLast() {
        return _moves.get(_moves.size() - 1);
    }
    /** Return the Piece representing who is next to move. */
    Piece turn() {
        return _turn;
    }

    /** Return true iff MOVE is legal for the player currently on move. */
    boolean isLegal(Move move) {
        if (move != null && !blocked(move)
            && inBounds(move.getCol1(), move.getRow1())) {
            if (move.movedPiece() == move.replacedPiece()) {
                return true;
            }
            if (Math.abs(move.getCol1() - move.getCol0())
                == pieceCountAlong(move)) {
                return true;
            } else if (Math.abs(move.getRow1() - move.getRow0())
                == pieceCountAlong(move)) {
                return true;
            }
        }
        return false;
    }
    /** Returns true if C and R are in bounds. */
    boolean inBounds(int c, int r) {
        return 1 <= c && c <= M && 1 <= r && r <= M;
    }

    /** Return a sequence of all legal moves from this position. */
    Iterator<Move> legalMoves() {
        return new MoveIterator();
    }

    @Override
    public Iterator<Move> iterator() {
        return legalMoves();
    }

    /** Return true if there is at least one legal move for the player
     *  on move. */
    public boolean isLegalMove() {
        return iterator().hasNext();
    }

    /** Return true iff either player has all his pieces continguous. */
    boolean gameOver() {
        return piecesContiguous(BP) || piecesContiguous(WP);
    }

    /** Return true iff SIDE's pieces are continguous. */
    boolean piecesContiguous(Piece side) {
        if (totalPieces(side) <= 1) {
            return true;
        }
        findFirst(side);
        int counter = 0;
        int total;
        total = totalPieces(side);
        for (col = 1; col < 9; col++) {
            for (row = 1; row < 9; row++) {
                if (contig[row - 1][col - 1]) {
                    counter++;
                    checkAround(side, col, row);
                }
            }
        }
        return counter == total;
    }
    /** Finds the first piece for player SIDE. */
    public void findFirst(Piece side) {
        for (col = 1; col < 9; col++) {
            for (row = 1; row < 9; row++) {
                if (get(col, row) == side) {
                    contig[row - 1][col - 1] = true;
                    broken = true;
                    break;
                }
            }
            if (broken) {
                break;
            }
        }
    }
    /** Checks a C and R for player SIDE to make sure pieces around are
    of same type. */
    public void checkAround(Piece side, int c, int r) {
        for (Direction dir: Direction.values()) {
            if (dir.dc == -1 && col == 1 || dir.dr == -1 && row == 1
                || dir.dc == 1 && col == 8 || dir.dr == 1 && row == 8) {
                continue;
            } else if (get(col + dir.dc, row + dir.dr) == side) {
                contig[row + dir.dr - 1][col + dir.dc - 1] = true;
            }
        }
    }
    /** Returns COUNTS, which is number of total pieces for player SIDE. */
    public int totalPieces(Piece side) {
        for (col = 1; col < 9; col++) {
            for (row = 1; row < 9; row++) {
                if (get(col, row) == side) {
                    count++;
                }
            }
        }
        return count;
    }
    /** Return the total number of moves that have been made (and not
     *  retracted).  Each valid call to makeMove with a normal move increases
     *  this number by 1. */
    int movesMade() {
        return _moves.size();
    }

    @Override
    public boolean equals(Object obj) {
        Board b = (Board) obj;
        return b == this;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===%n");
        for (int r = M; r >= 1; r -= 1) {
            out.format("    ");
            for (int c = 1; c <= M; c += 1) {
                out.format("%s ", get(c, r).abbrev());
            }
            out.format("%n");
        }
        out.format("Next move: %s%n===", turn().fullName());
        return out.toString();
    }

    /** Return the number of pieces in the line of action indicated by MOVE. */
    public int pieceCountAlong(Move move) {
        count = 0;
        for (Direction d: Direction.values()) {
            if (d.dc == 0 && d.dr == 1) {
                norS = d;
            } else if (d.dc == 1 && d.dr == 0) {
                worE = d;
            } else if (d.dc == 1 && d.dr == 1 || d.dc == -1 && d.dr == -1) {
                neorSW = d;
            } else if (d.dc == 1 && d.dr == -1 || d.dc == -1 && d.dr == 1) {
                nworSE = d;
            }
        }
        if (move.getCol1() == move.getCol0()) {
            count = pieceCountAlong(move.getCol0(), move.getRow0(), norS);
        } else if (move.getRow1() == move.getRow0()) {
            count = pieceCountAlong(move.getCol0(), move.getRow0(), worE);
        } else if (move.getRow1() > move.getRow0() && move.getCol1()
            < move.getCol0() || move.getRow1() < move.getRow0()
            && move.getCol1() > move.getCol0()) {
            count = pieceCountAlong(move.getCol0(), move.getRow0(), nworSE);
        } else if (move.getRow1() < move.getRow0()
            && move.getCol1() < move.getCol0() || move.getRow1()
            > move.getRow0() && move.getCol1() > move.getCol0()) {
            count = pieceCountAlong(move.getCol0(), move.getRow0(), neorSW);
        }
        return count;
    }

    /** Return the number of pieces in the line of action in direction DIR and
     *  containing the square at column C and row R. */
    public int pieceCountAlong(int c, int r, Direction dir) {
        count = 0;
        if (dir.dc == -1 && dir.dr == 1 || dir.dc == 1 && dir.dr == -1) {
            for (int i = c, j = r; i > 0 && j <= 8; i -= 1, j++) {
                if (get(i, j) != EMP) {
                    count += 1;
                }
            }
            for (int k = c, l = r; l > 1 && k < 8; k += 1, l -= 1) {
                if (get(k + 1, l - 1) != EMP) {
                    count += 1;
                }
            }
        } else if (dir.dc == 0) {
            for (int i = 0; i < M; i++) {
                if (get(c, i + 1) != EMP) {
                    count += 1;
                }
            }
        } else if (dir.dr == 0) {
            for (int i = 0; i < M; i++) {
                if (get(i + 1, r) != EMP) {
                    count += 1;
                }
            }
        } else if (dir.dc == 1 && dir.dr == 1 || dir.dc == -1 && dir.dr == -1) {
            for (int i = c, j = r; i <= 8 && j <= 8; i++, j++) {
                if (get(i, j) != EMP) {
                    count += 1;
                }
            }
            for (int k = c, l = r; k > 1 && l > 1; k -= 1, l -= 1) {
                if (get(k - 1, l - 1) != EMP) {
                    count++;
                }
            }
        }
        return count;
    }

    /** Return true iff MOVE is blocked by an opposing piece or by a
     *  friendly piece on the target square. */
    public boolean blocked(Move move) {
        int endRow = move.getRow1();
        int startRow = move.getRow0();
        int endCol = move.getCol1();
        int startCol = move.getCol0();
        Piece start = move.movedPiece();
        Piece end = move.replacedPiece();
        if (move.movedPiece() == move.replacedPiece()) {
            return true;
        }
        if (startCol == endCol) {
            for (int i = startRow; i != endRow;) {
                if (get(startCol, i) == start.opposite()) {
                    return true;
                }
                if (i < endRow) {
                    i += 1;
                } else if (i > endRow) {
                    i -= 1;
                }
            }
        } else if (startRow == endRow) {
            for (int j = startCol; j != endCol;) {
                if (get(j, startRow) == start.opposite()) {
                    return true;
                }
                if (j < endCol) {
                    j += 1;
                } else if (j > endCol) {
                    j -= 1;
                }
            }
        } else if (endRow > startRow && endCol < startCol) {
            for (int i = startCol, j = startRow; i != endCol && j != endRow;
                i -= 1, j++) {
                if (get(i, j) == start.opposite()) {
                    return true;
                }
            }
        } else if (endRow < startRow && endCol > startCol) {
            for (int i = startCol, j = startRow; i != endCol && j != endRow;
                i++ , j -= 1) {
                if (get(i, j) == start.opposite()) {
                    return true;
                }
            }
        } else if (endRow > startRow && endCol > startCol) {
            for (int i = startCol, j = startRow; i != endCol && j != endRow;
                i++, j++) {
                if (get(i, j) == start.opposite()) {
                    return true;
                }
            }
        } else if (endRow < startRow && endCol < startCol) {
            for (int i = startCol, j = startRow; i != endCol && j != endRow;
                i -= 1, j -= 1) {
                if (get(i, j) == start.opposite()) {
                    return true;
                }
            }
        }
        return false;
    }
    /** The standard initial configuration for Lines of Action. */
    static final Piece[][] INITIAL_PIECES = {
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
    };

    /** List of all unretracted moves on this board, in order. */
    private final ArrayList<Move> _moves = new ArrayList<>();
    /** Current side on move. */
    private Piece _turn;
    /** Data structure holding information. */
    private Piece[][] myBoard = new Piece[8][8];
    /** Keeps track of count for count along. */
    private int count;
    /** A direction. */
    private Direction norS;
    /** A direction. */
    private Direction worE;
    /** A direction. */
    private Direction neorSW;
    /** A direction. */
    private Direction nworSE;
    /** boolean board that checks contig. */
    private boolean[][] contig = new boolean[8][8];
    /** boolean for for loop. */
    private boolean broken;
    /** keeps track of column for for loop. */
    private int col;
    /** keeps track of row for for loop. */
    private int row;
    /** An iterator returning the legal moves from the current board. */
    private class MoveIterator implements Iterator<Move> {

        /** Current piece under consideration. */
        private int _c, _r;
        /** Next direction of current piece to return. */
        private Direction _dir;
        /** Next move. */
        private Move _move;
        /** 2 dimensional array to check pieces. */
        private boolean[][] checked = new boolean[8][8];
        /** boolean to exit a for loop. */
        private boolean broken;
        /** keeps track of how many spaces a piece can move. */
        private int steps;
        /** possible move. */
        private Move possible;
        /** for loop row. */
        private int row;
        /** for loop column. */
        private int col;
        /** total. */
        private int total;


        /** A new move iterator for turn(). */
        MoveIterator() {
            _c = 1; _r = 1; _dir = NOWHERE;
            incr();

        }

        @Override
        public boolean hasNext() {
            return _move != null;
        }

        @Override
        public Move next() {
            if (_move == null) {
                throw new NoSuchElementException("no legal move");
            }
            Move move = _move;
            incr();
            return move;
        }

        @Override
        public void remove() {
        }

        /** Advance to the next legal move. */
        private void incr() {
            _dir = _dir.succ();
            broken = false;
            int allTrue = 0;
            for (col = 1; col < 9; col++) {
                for (row = 1; row < 9; row++) {
                    if (Board.this.get(col, row) == _turn
                        && !checked[row - 1][col - 1]) {
                        _c = col;
                        _r = row;
                        broken = true;
                        allTrue = 2;
                        break;
                    }
                }
                if (broken) {
                    break;
                }
            }
            if (allTrue == 0) {
                _move = null;
                return;
            }
            if (_dir == null) {
                _dir = NOWHERE;
                checked[_r - 1][_c - 1] = true;
                incr();
                return;
            }
            steps = Board.this.pieceCountAlong(_c, _r, _dir);
            _move = Move.create(_c, _r, steps, _dir, Board.this);
            while (!isLegal(_move)) {
                _dir = _dir.succ();
                if (_dir == null) {
                    _dir = NOWHERE;
                    checked[_r - 1][_c - 1] = true;
                    incr();
                    return;
                }
                steps = Board.this.pieceCountAlong(_c, _r, _dir);
                _move = Move.create(_c, _r, steps, _dir, Board.this);
            }
        }
    }
    /** RETURNS true if all of the 2d array was CHECKED. */
    private boolean allTrue(boolean[][] checked) {
        boolean allTrue = true;
        for (col = 1; col < 9; col++) {
            for (row = 1; row < 9; row++) {
                if (!checked[row - 1][col - 1]) {
                    allTrue = false;
                }
            }
        }
        return allTrue;
    }
}
