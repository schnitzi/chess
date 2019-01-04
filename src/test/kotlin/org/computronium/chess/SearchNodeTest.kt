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
    fun equals() {
        Assert.assertEquals(SearchNode.newGame(), SearchNode.newGame())
    }

    @Test
    @UseDataProvider(value = "findMovesData")
    fun findMoves(whoseMove: PieceColor, start: Array<String>, expectedMove: Array<String>) {
        val startBoard = SearchNode.fromStrings(start, whoseMove)
        val expected = Board.fromStrings(expectedMove)

        var found = false
        for (move in startBoard.moves) {
            move.apply(startBoard.board)
            if (startBoard.board == expected) {
                found = true
                break
            }
        }
        Assert.assertTrue(found)
    }

    companion object {

        @DataProvider
        @JvmStatic
        fun findMovesData(): Array<Array<Any>> = `$$`(
//             @formatter:off


            `$` (PieceColor.WHITE,
                arrayOf(
                    "rnbqkbnr",
                    "pppppppp",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "PPPPPPPP",
                    "RNBQKBNR"),
                arrayOf(
                    "rnbqkbnr",
                    "pppppppp",
                    "        ",
                    "        ",
                    "        ",
                    "       P",
                    "PPPPPPP ",
                    "RNBQKBNR")),

            `$` (PieceColor.WHITE,
                arrayOf(
                    " k      ",
                    "     P  ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "    K   "),
                arrayOf(
                    " k   Q  ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "    K   ")),

            `$` (PieceColor.WHITE,
                arrayOf(
                    " k  r   ",
                    "     P  ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "    K   "),
                arrayOf(
                    " k  B   ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "        ",
                    "    K   ")),

            `$` (PieceColor.BLACK,
                arrayOf(
                    " k      ",
                    "        ",
                    "        ",
                    "     K  ",
                    "        ",
                    "        ",
                    "   p    ",
                    "        "),
                arrayOf(
                    " k      ",
                    "        ",
                    "        ",
                    "     K  ",
                    "        ",
                    "        ",
                    "        ",
                    "   n    ")),

            `$` (PieceColor.WHITE,
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

            `$` (PieceColor.WHITE,
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

            `$` (PieceColor.BLACK,
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


            `$` (PieceColor.BLACK,
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


            `$` (PieceColor.WHITE,
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