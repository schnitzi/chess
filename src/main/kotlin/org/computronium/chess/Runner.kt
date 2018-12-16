package org.computronium.chess

import java.util.*
import kotlin.system.exitProcess

fun main(args : Array<String>) {

    val random = Random()

    var boardState = BoardState.newGame()

    for (m in 0..300) {
        println(boardState)

        val moves = boardState.findMoves()

        if (moves.isEmpty()) {

            println(if (boardState.whoseTurnIsInCheck) "Mate!" else "Stalemate!")
            exitProcess(0)
        }

        boardState = moves[random.nextInt(moves.size)]
    }
}
