package org.computronium.chess

import com.tngtech.java.junit.dataprovider.DataProvider
import com.tngtech.java.junit.dataprovider.DataProviderRunner
import com.tngtech.java.junit.dataprovider.DataProviders.`$$`
import com.tngtech.java.junit.dataprovider.DataProviders.`$`
import com.tngtech.java.junit.dataprovider.UseDataProvider
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(DataProviderRunner::class)
internal class SearchNodeTest {

    @Test
    @UseDataProvider(value = "findMovesData")
    fun findMoves(startFEN: String, expectedMoveFEN: String) {
        val startBoard = SearchNode.fromFEN(startFEN)
        println("start =\n$startBoard")
        val expected = BoardState.fromFEN(expectedMoveFEN)

        println("expected =\n$expected")
        var found = false
        for (move in startBoard.moves) {
            println("move = ${move.toString(startBoard.boardState)}\n")
            move.apply(startBoard.boardState)
            println("${startBoard.boardState}")
            if (startBoard.boardState == expected) {
                found = true
                break
            }
            move.rollback(startBoard.boardState)
        }
        Assert.assertTrue(found)
    }

    companion object {

        @DataProvider
        @JvmStatic
        fun findMovesData(): Array<Array<Any>> = `$$`(
//             @formatter:off


//            `$` (BoardState.WHITE,
//                arrayOf(
//                    "rnbqkbnr",
//                    "pppppppp",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "PPPPPPPP",
//                    "RNBQKBNR"),
//                arrayOf(
//                    "rnbqkbnr",
//                    "pppppppp",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "P       ",
//                    " PPPPPPP",
//                    "RNBQKBNR")),
//
//            `$` (BoardState.WHITE,
//                arrayOf(
//                    " k      ",
//                    "     P  ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "    K   "),
//                arrayOf(
//                    " k   Q  ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "    K   ")),

//            `$` (BoardState.WHITE,
//                arrayOf(
//                    " k  r   ",
//                    "     P  ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "    K   "),
//                arrayOf(
//                    " k  B   ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "    K   ")),

//            `$` (BoardState.BLACK,
//                arrayOf(
//                    " k      ",
//                    "        ",
//                    "        ",
//                    "     K  ",
//                    "        ",
//                    "        ",
//                    "   p    ",
//                    "        "),
//                arrayOf(
//                    " k      ",
//                    "        ",
//                    "        ",
//                    "     K  ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "   n    ")),

            `$` (BoardState.WHITE,
                arrayOf(
                    " k      ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "R   K   "),
                arrayOf(
                    " k      ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "  KR    ")),

            `$` (BoardState.WHITE,
                arrayOf(
                    " k      ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "    K  R"),
                arrayOf(
                    " k      ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "     RK ")),

            `$` (BoardState.BLACK,
                arrayOf(
                    "r   k   ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "    K  R"),
                arrayOf(
                    "  kr    ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "    K  R")),


            `$` (BoardState.BLACK,
                arrayOf(
                    "rnb.kb..",
                    "ppqppppr",
                    ".....N..",
                    "..p....p",
                    "........",
                    "P....P..",
                    "RPPPP.PP",
                    "..BQKBNR"),
                arrayOf(
                    "rnbk.b..",
                    "ppqppppr",
                    ".....N..",
                    "..p....p",
                    "........",
                    "P....P..",
                    "RPPPP.PP",
                    "..BQKBNR")),


            `$` (BoardState.WHITE,
                arrayOf(
                    " . k . .",
                    ".Q. . . ",
                    " . N . .",
                    ". b . .p",
                    "B. . . P",
                    ". . .Pn ",
                    " . . . .",
                    ". .K. . "),
                arrayOf(
                    " . k . .",
                    ".Q. .N. ",
                    " .   . .",
                    ". b . .p",
                    "B. . . P",
                    ". . .Pn ",
                    " . . . .",
                    ". .K. . "))
        )
    }
}