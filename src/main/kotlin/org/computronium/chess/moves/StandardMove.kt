package org.computronium.chess.moves

import org.computronium.chess.Board
import org.computronium.chess.PieceType

/**
 * Class representing a move.
 */
open class StandardMove(var from : Int, var to : Int) : Move() {

    var resultsInCheck : Boolean = false


    /**
     * This will generate a simplified version of the last move in Algebraic notation that
     * suffices in most cases.  However, sometimes this version is ambiguous, and needs more
     * information, which can't be determined without comparison to other moves.  Figuring
     * out how to do this is something I still need TODO.
     */
    open fun toString(board: Board): String {
        val sb = StringBuilder()
        val piece = board[from]
        if (piece!!.type != PieceType.PAWN) {
            sb.append(piece.type.letter)
        }
        sb.append(to)
        return sb.toString()
    }

    override fun apply(board: Board) {

        board.move(from, to)

        super.apply(board)
    }

    override fun rollback(board: Board) {

        super.rollback(board)

        board.move(to, from)
    }

}
