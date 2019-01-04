package org.computronium.chess.moves

import org.computronium.chess.Board
import org.computronium.chess.Piece
import org.computronium.chess.PieceType

/**
 * Class representing a move.
 */
open class StandardCapture(from : Int, to : Int) : StandardMove(from, to) {


    var capturedPiece: Piece? = null

    /**
     * This will generate a simplified version of the last move in Algebraic notation that
     * suffices in most cases.  However, sometimes this version is ambiguous, and needs more
     * information, which can't be determined without comparison to other moves.  Figuring
     * out how to do this is something I still need TODO.
     */
    override fun toString(board: Board): String {
        val sb = StringBuilder()
        val piece = board[from]
        val capturedPiece = board[to]
        if (piece!!.type != PieceType.PAWN) {
            sb.append(piece.type.letter)
        } else if (capturedPiece != null) {
            sb.append(Board.fileChar(from))
        }
        sb.append("x")
        sb.append(to)
        return sb.toString()
    }

    override fun apply(board: Board) {

        capturedPiece = board[to]

        super.apply(board)
    }

    override fun rollback(board: Board) {

        super.rollback(board)

        board[to] = capturedPiece
    }

}
