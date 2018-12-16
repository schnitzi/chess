package org.computronium.chess.move

import org.computronium.chess.BoardState


interface Move {

    fun doMove(boardState: BoardState)
}
