
package loa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
/** An automated Player.
 *  @author Juan Cervantes */
class MachinePlayer extends Player {

    /** A MachinePlayer that plays the SIDE pieces in GAME. */
    MachinePlayer(Piece side, Game game) {
        super(side, game);
        s = side;
        g = game;
        storedBoards = new HashSet<Integer>();
    }

    @Override
    /** RETURNS MOVE that is most convenient. */
    Move makeMove() {
        Move best = findBest(side(), getBoard(), 2, Integer.MIN_VALUE,
                                                        Integer.MAX_VALUE);
        String player = side().abbrev().toUpperCase();
        System.out.println(player + "::" + best.toString());
        return best;
    }
    /** All legal moves for current SIDE player White or Black on
    the BOARD, RETURNS ARRAYLIST<MOVE> so that I can iterate through
    legal moves much clearer. */
    private ArrayList<Move> allMoves(Board board, Piece side) {
        ArrayList<Move> legal = new ArrayList<>();
        Iterator<Move> all = board.legalMoves();
        while (all.hasNext()) {
            Move move = all.next();
            legal.add(move);
        }
        return legal;
    }

    /** Method that returns an evaluation of the BOARD as an int
    representation RETURNS INT representing convenience of board
    state after a move. */
    private int evaluation(Board board) {
        int total = 0;
        if (board.piecesContiguous(side())) {
            return Integer.MAX_VALUE;
        } else if (board.piecesContiguous(side().opposite())) {
            return Integer.MIN_VALUE;
        } else {
            total += checkAround(board);
            total += ate(board);
            return total;
        }
    }

    /** Method that returns the best move for CURRENT player on the
    BOARD, MAX and MIN are used for Alpha Beta pruning, the DEPTH
    is how many traversals there will be down the game tree RETURNS
    MOVE that is most convenient. */
    Move findBest(Piece current, Board board, int depth, int max, int min) {
        int value = Integer.MIN_VALUE;
        ArrayList<Move> options = allMoves(board, current);
        System.out.println(options.size());
        Move bestMove = options.get(getGame().randInt(options.size()));
        for (Move m : options) {
            Board nextBoard = new Board(board);
            nextBoard.makeMove(m);
            int node = traversed(current.opposite(), nextBoard, depth,
                max, min);
            if (node > value && !storedBoards.contains(node)) {
                value = node;
                bestMove = m;
            }
            nextBoard.retract();
            if (value > max) {
                max = value;
            }
        }
        storedBoards.add(value);
        return bestMove;
    }
    /** Looks at the CURRENT player on the BOARD, DEPTH, MAX, and
    MIN are used for traversal down game tree, RETURNS INT representing
    best moves for whoever's turn it is. */
    private int traversed(Piece current, Board board, int depth,
            int max, int min) {
        if (depth == 0) {
            return evaluation(board);
        } else if (current == side()) {
            return traverseWithMax(current, board, depth, max, min);
        } else {
            return traverseWithMin(current, board, depth, max, min);
        }
    }
    /** CURRENT player on BOARD, DEPTH, MAX, and MIN are used for traversal,
    looks at one pruning condition, RETURNS INT representing best traversal
    for max player. */
    private int traverseWithMax(Piece current, Board board, int depth,
            int max, int min) {
        int currMax = max;
        ArrayList<Move> legal = allMoves(board, current);
        if (legal.isEmpty()) {
            return evaluation(board);
        }
        for (Move m : legal) {
            Board updated = new Board(board);
            updated.makeMove(m);
            int node = traversed(current.opposite(), updated, depth - 1,
                max, min);
            currMax = Math.max(node, currMax);
            updated.retract();
            if (currMax > min) {
                return currMax;
            } else {
                max = Math.max(currMax, max);
            }
        }
        return currMax;
    }
    /** CURRENT player on BOARD, DEPTH, MAX, and MIN are used for traversal,
    looks at other pruning condition, RETURNS INT representing best traversal
    for min player. */
    private int traverseWithMin(Piece current, Board board, int depth,
        int max, int min) {
        int currMin = min;
        ArrayList<Move> legal = allMoves(board, current);
        if (legal.isEmpty()) {
            return evaluation(board);
        }
        for (Move m : legal) {
            Board updated2 = new Board(board);
            updated2.makeMove(m);
            int node = traversed(current.opposite(), updated2, depth - 1,
                max, min);
            currMin = Math.min(node, currMin);
            updated2.retract();
            if (currMin < max) {
                return currMin;
            } else {
                min = Math.min(currMin, min);
            }
        }
        return currMin;
    }
    /** Method that checks how many pieces are touching for current
    player on the BOARD, RETURNS INT. */
    private int checkAround(Board board) {
        Move done = board.getLast();
        int howMany = 0;
        int col = done.getCol1();
        int row = done.getRow1();
        for (Direction dir: Direction.values()) {
            if (dir.dc == -1 && col == 1 || dir.dr == -1 && row == 1
                || dir.dc == 1 && col == 8 || dir.dr == 1 && row == 8) {
                continue;
            } else if (board.get(col + dir.dc, row + dir.dr) == side()) {
                howMany++;
            }
        }
        return howMany;
    }
    /** Method that checks if the last move captured another piece
    on the BOARD, RETURNS INT. */
    private int ate(Board board) {
        Move done = board.getLast();
        int total = 0;
        if (done.replacedPiece() == done.movedPiece()) {
            total += 3;
        }
        return total;
    }
    /** Instance for game. */
    private final Game g;
    /** Initiate side for game. */
    private final Piece s;
    /** Keeps track of previous boards. */
    private HashSet<Integer> storedBoards;

}

