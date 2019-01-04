package org.computronium.chess.moves

import org.computronium.chess.Board

/**
 * Interface representing a move that can be applied and rolled back.
 */
open class Move {

    private var enPassantCapturePos : Int? = null

    private var whoseTurnIsInCheck : Boolean = false

    open fun apply(board: Board) {
        enPassantCapturePos = board.enPassantCapturePos
        board.enPassantCapturePos = null
        whoseTurnIsInCheck = board.whoseTurnIsInCheck
        board.moveNumber++
        board.whoseTurn = board.whoseTurn.oppositeColor()
    }

    open fun rollback(board: Board) {
        board.enPassantCapturePos = enPassantCapturePos
        board.whoseTurnIsInCheck = whoseTurnIsInCheck
        board.moveNumber--
        board.whoseTurn = board.whoseTurn.oppositeColor()
    }

}
