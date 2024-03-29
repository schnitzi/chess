package org.computronium.chess.moves

import org.computronium.chess.BoardState

/**
 * Interface representing a move that can be applied and rolled back.
 */
abstract class Move {

    var resultsInCheck = false

    private var enPassantCapturePos : Int? = null

    private var whoseTurnIsInCheck : Boolean = false

    open fun apply(boardState: BoardState) {

        enPassantCapturePos = boardState.enPassantCapturePos

        boardState.enPassantCapturePos = null

        whoseTurnIsInCheck = boardState.whoseTurnConfig().isInCheck

        if (boardState.whoseTurn == BoardState.BLACK) {
            boardState.moveNumber++
        }

        boardState.whoseTurn = 1 - boardState.whoseTurn
    }

    open fun rollback(boardState: BoardState) {

        boardState.whoseTurn = 1 - boardState.whoseTurn

        if (boardState.whoseTurn == BoardState.BLACK) {
            boardState.moveNumber--
        }

        boardState.whoseTurnConfig().isInCheck = whoseTurnIsInCheck

        boardState.enPassantCapturePos = enPassantCapturePos
    }

    abstract fun toString(boardState: BoardState): String
}
