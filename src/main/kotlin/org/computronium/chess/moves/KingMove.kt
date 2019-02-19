package org.computronium.chess.moves

import org.computronium.chess.BoardState

class KingMove(from : Int, to : Int) : StandardMove(from, to) {

    override fun apply(boardState: BoardState) {

        val config = boardState.whoseTurnConfig()
        config.canQueenSideCastle = false
        config.canKingSideCastle = false

        super.apply(boardState)
    }

    override fun rollback(boardState: BoardState) {

        super.rollback(boardState)

        val config = boardState.whoseTurnConfig()
        config.canQueenSideCastle = true
        config.canKingSideCastle = true
    }
}