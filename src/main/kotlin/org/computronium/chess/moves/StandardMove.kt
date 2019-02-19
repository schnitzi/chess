package org.computronium.chess.moves

import org.computronium.chess.BoardState
import org.computronium.chess.PieceType

/**
 * Class representing a move.
 */
open class StandardMove(var from : Int, var to : Int) : Move() {

    /**
     * This will generate a simplified version of the last move in Algebraic notation that
     * suffices in most cases.  However, sometimes this version is ambiguous, and needs more
     * information, which can't be determined without comparison to other moves.  Figuring
     * out how to do this is something I still need TODO.
     */
    override fun toString(boardState: BoardState): String {
        val sb = StringBuilder()
        val piece = boardState[from]
        if (piece!!.type != PieceType.PAWN) {
            sb.append(piece.type.letter)
        }
        sb.append(BoardState.squareName(to))
        if (resultsInCheck) {
            sb.append("+")
        }
        return sb.toString()
    }

    override fun apply(boardState: BoardState) {

        boardState.move(from, to)

        boardState.halfMoveClock++

        super.apply(boardState)
    }

    override fun rollback(boardState: BoardState) {

        super.rollback(boardState)

        boardState.halfMoveClock--

        boardState.move(to, from)
    }
}
