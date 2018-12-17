package org.computronium.chess.move

import org.computronium.chess.BoardState
import org.computronium.chess.Coordinate

class StandardMove(val from: Coordinate, val to: Coordinate) : Move {

    override fun doMove(boardState: BoardState) {
        val piece = boardState.board.pieceAt(from)
        boardState.board.set(to, piece)
    }

    override fun toString(): String {
        return "$from to $to"
    }


}