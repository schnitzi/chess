package org.computronium.chess.moves

import org.computronium.chess.Board
import org.computronium.chess.Piece
import org.computronium.chess.PieceType

class PawnPromotion(val from: Int, val to: Int, private val promoteTo: PieceType) : Move() {

    private var pawn: Piece? = null

    override fun apply(board: Board) {

        pawn = board[from]
        board[from] = null
        board[to] = Piece.forTypeAndColor(promoteTo, pawn!!.color)

        super.apply(board)
    }

    override fun rollback(board: Board) {

        super.rollback(board)

        board[from] = pawn
        board[to] = null
    }

}