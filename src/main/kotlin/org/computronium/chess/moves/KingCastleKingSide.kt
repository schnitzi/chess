package org.computronium.chess.moves

import org.computronium.chess.Board

class KingCastleKingSide : Move() {


    override fun apply(board: Board) {

        val homeRankStart = board.sideConfig().homeRankStart
        board.move(homeRankStart+4, homeRankStart+6)    // move the king
        board.move(homeRankStart+7, homeRankStart+5)    // move the rook
        board.setCanQueenSideCastle(false)
        board.setCanKingSideCastle(false)

        super.apply(board)

    }

    override fun rollback(board: Board) {

        super.rollback(board)

        val homeRankStart = board.sideConfig().homeRankStart
        board.move(homeRankStart+6, homeRankStart+4)    // move the king back
        board.move(homeRankStart+5, homeRankStart+7)    // move the rook back
        board.setCanQueenSideCastle(true)
        board.setCanKingSideCastle(true)
    }

    companion object {

        fun isPossible(board: Board) : Boolean {

            val homeRankStart = board.sideConfig().homeRankStart
            return !board.whoseTurnIsInCheck &&
                    board.canKingSideCastle() &&
                    board.empty(homeRankStart+5) &&
                    board.empty(homeRankStart+6) &&
                    !board.isAttacked(homeRankStart+5, board.whoseTurn.oppositeColor())
        }
    }

}