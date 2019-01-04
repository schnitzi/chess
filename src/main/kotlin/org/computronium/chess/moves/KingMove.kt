package org.computronium.chess.moves

import org.computronium.chess.Board

class KingMove(from : Int, to : Int) : StandardMove(from, to) {

    override fun apply(board: Board) {

        board.setCanQueenSideCastle(false)
        board.setCanKingSideCastle(false)

        super.apply(board)
    }

    override fun rollback(board: Board) {

        super.rollback(board)

        board.setCanQueenSideCastle(true)
        board.setCanKingSideCastle(true)
    }
}