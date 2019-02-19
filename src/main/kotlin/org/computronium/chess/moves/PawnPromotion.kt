package org.computronium.chess.moves

import org.computronium.chess.BoardState
import org.computronium.chess.Piece
import org.computronium.chess.PieceType

class PawnPromotion(val from: Int, val to: Int, private val promoteTo: PieceType) : Move() {

    private var pawn: Piece? = null

    override fun apply(boardState: BoardState) {

        pawn = boardState[from]
        boardState[from] = null
        boardState[to] = Piece.forTypeAndColor(promoteTo, pawn!!.color)

        super.apply(boardState)
    }

    override fun rollback(boardState: BoardState) {

        super.rollback(boardState)

        boardState[from] = pawn
        boardState[to] = null
    }

    override fun toString(boardState: BoardState): String {
        return BoardState.squareName(to) + "=" + promoteTo.letter
    }
}