package org.computronium.chess.moves

import org.computronium.chess.Board

class RookMove(from : Int, to : Int) : StandardMove(from, to) {

    private var canQueenSideCastle = false
    private var canKingSideCastle = false

    override fun apply(board: Board) {

        val homeRank = board.sideConfig().homeRankStart
        if (from == homeRank) {     // queenside rook
            canQueenSideCastle = board.canQueenSideCastle()
            board.setCanQueenSideCastle(false)
        } else if (from == homeRank+7) {    // kingside rook
            canKingSideCastle = board.canKingSideCastle()
            board.setCanKingSideCastle(false)
        }

        super.apply(board)
    }

    override fun rollback(board: Board) {
        super.rollback(board)

        val homeRank = board.sideConfig().homeRankStart
        if (from == homeRank) {
            board.setCanQueenSideCastle(canQueenSideCastle)
        } else if (from == homeRank+7) {
            board.setCanKingSideCastle(canKingSideCastle)
        }
    }
}