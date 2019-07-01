package org.computronium.chess.moves

import org.computronium.chess.BoardState

class PawnInitialMove(from: Int, to: Int, private val over: Int) : PawnMove(from, to) {

    override fun apply(boardState: BoardState) {

        super.apply(boardState)

        boardState.enPassantCapturePos = over
    }

    // Surprisingly, no need to override rollback here.  En passant capture position is
    // entirely handled in Move.

    override fun toString(boardState: BoardState): String {
        return BoardState.squareName(to)
    }
}
