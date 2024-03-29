package org.computronium.chess.moves

import org.computronium.chess.BoardState

class RookCapture(from : Int, to : Int) : StandardCapture(from, to) {


    private var canQueenSideCastle = false
    private var canKingSideCastle = false

    override fun apply(boardState: BoardState) {

        val config = boardState.whoseTurnConfig()
        val homeRank = config.homeRankStart
        if (from == homeRank) {     // queenside rook
            canQueenSideCastle = config.canQueenSideCastle
            config.canQueenSideCastle = false
        } else if (from == homeRank+7) {    // kingside rook
            canKingSideCastle = config.canKingSideCastle
            config.canKingSideCastle = false
        }

        super.apply(boardState)
    }

    override fun rollback(boardState: BoardState) {
        super.rollback(boardState)

        val config = boardState.whoseTurnConfig()
        val homeRank = config.homeRankStart
        if (from == homeRank) {
            config.canQueenSideCastle = canQueenSideCastle
        } else if (from == homeRank+7) {
            config.canKingSideCastle = canKingSideCastle
        }
    }
}