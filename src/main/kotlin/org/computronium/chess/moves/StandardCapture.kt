package org.computronium.chess.moves

import org.computronium.chess.BoardState
import org.computronium.chess.Piece
import org.computronium.chess.PieceType

/**
 * Class representing a move.
 */
open class StandardCapture(from : Int, to : Int) : StandardMove(from, to) {


    var capturedPiece: Piece? = null

    var halfMoveClock = 0

    /**
     * This will generate a simplified version of the last move in Algebraic notation that
     * suffices in most cases.  However, sometimes this version is ambiguous, and needs more
     * information, which can't be determined without comparison to other moves.  Figuring
     * out how to do this is something I still need TODO.
     */
    override fun toString(boardState: BoardState): String {
        val sb = StringBuilder()
        val piece = boardState[from]
        val capturedPiece = boardState[to]
        if (piece!!.type != PieceType.PAWN) {
            sb.append(piece.type.letter)
        } else if (capturedPiece != null) {
            sb.append(BoardState.fileChar(from))
        }
        sb.append("x")
        sb.append(to)
        return sb.toString()
    }

    override fun apply(boardState: BoardState) {

        capturedPiece = boardState[to]

        super.apply(boardState)

        halfMoveClock = boardState.halfMoveClock
        boardState.halfMoveClock = 0
    }

    override fun rollback(boardState: BoardState) {

        super.rollback(boardState)

        boardState.halfMoveClock = halfMoveClock

        boardState[to] = capturedPiece
    }
}
