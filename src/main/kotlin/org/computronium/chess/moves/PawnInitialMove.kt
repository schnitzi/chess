package org.computronium.chess.moves

import org.computronium.chess.Board

class PawnInitialMove(from: Int, to: Int, private val over: Int) : StandardMove(from, to) {

    override fun apply(board: Board) {

        super.apply(board)

        board.enPassantCapturePos = over
    }
}