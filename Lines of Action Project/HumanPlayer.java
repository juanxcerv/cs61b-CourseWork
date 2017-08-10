
package loa;

/** A Player that prompts for moves and reads them from its Game.
 *  @author Juan Cervantes
 */
class HumanPlayer extends Player {

    /** A HumanPlayer that plays the SIDE pieces in GAME.  It uses
     *  GAME.getMove() as a source of moves.  */
    HumanPlayer(Piece side, Game game) {
        super(side, game);
        g = game;
        s = side;
    }

    @Override
    Move makeMove() {
        _move = g.getMove();
        return _move;
    }
    /** Instance for game. */
    private final Game g;
    /** Instance for side of game. */
    private final Piece s;
    /** Move that is done. */
    private Move _move;
}
