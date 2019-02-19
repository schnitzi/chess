package org.computronium.chess.moves

import org.computronium.chess.BoardState

class PawnInitialMove(from: Int, to: Int, private val over: Int) : StandardMove(from, to) {

    override fun apply(boardState: BoardState) {

        super.apply(boardState)

        boardState.enPassantCapturePos = over
    }

    override fun toString(boardState: BoardState): String {
        return BoardState.squareName(to)
    }
}
