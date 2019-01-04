package org.computronium.chess

import java.util.concurrent.atomic.AtomicInteger

/**
 * The main class representing a board state, along with the move that got us here.
 */
class Board(private val board: Array<Piece?>) {

    var kingCoordinate = hashMapOf<PieceColor, Int>()

    var whoseTurn: PieceColor = PieceColor.WHITE
    var whoseTurnIsInCheck = false

    var moveNumber = 0

    var enPassantCapturePos: Int? = null

    var canQueenSideCastle = hashMapOf(PieceColor.WHITE to AtomicInteger(0), PieceColor.BLACK to AtomicInteger(0))
    var canKingSideCastle = hashMapOf(PieceColor.WHITE to AtomicInteger(0), PieceColor.BLACK to AtomicInteger(0))


    init {
        // Save the coordinate for each king, so we can figure out if the king is in check.
        for (index in BOARD_INDEXES) {
            if (board[index]?.type == PieceType.KING) {
                kingCoordinate[board[index]?.color!!] = index
            }
        }
    }

    override fun toString(): String {

        val sb = StringBuilder()

        for (rank in 7 downTo 0) {
            for (file in 0..7) {
                sb.append("${board[indexOf(file, rank)] ?: (if ((file+rank)%2==0) "." else " ")} ")
            }
            sb.append("\n")
        }

        sb.append("$whoseTurn to move")
        return sb.toString()
    }

    operator fun get(pos: Int): Piece? {
        return board[pos]
    }

    fun empty(index: Int): Boolean {
        return get(index) == null
    }

    operator fun set(index: Int, piece: Piece?) {
        board[index] = piece
    }

    fun move(from: Int, to: Int) {
        board[to] = get(from)
        board[from] = null
    }

    fun piecePositions(color: PieceColor): List<Int> {
        val positions = mutableListOf<Int>()
        for (index in BOARD_INDEXES) {
            if (board[index]?.color == color) {
                positions.add(index)
            }
        }
        return positions
    }

    fun isAttacked(pos: Int, attackingColor: PieceColor) : Boolean {
        return isAttackedByPieceTypes(pos, setOf(PieceType.QUEEN, PieceType.ROOK), ROOK_MOVE_OFFSETS, attackingColor) ||
                isAttackedByPieceTypes(pos, setOf(PieceType.QUEEN, PieceType.BISHOP), Board.BISHOP_MOVE_OFFSETS, attackingColor) ||
                isAttackedByKnight(pos, attackingColor) ||
                isAttackedByPawn(pos, attackingColor) ||
                isAttackedByKing(pos, attackingColor)
    }

    private fun isPieceOfType(type: PieceType, pos: Int, attackingColor: PieceColor) : Boolean {
        val piece = get(pos)
        return piece?.color == attackingColor && piece.type == type
    }

    private fun isAttackedByKing(pos: Int, attackingColor: PieceColor): Boolean {
        for (offset in Board.KING_MOVE_OFFSETS) {
            if (isPieceOfType(PieceType.KING, pos+offset, attackingColor)) {
                return true
            }
        }
        return false
    }

    private fun isAttackedByPawn(pos: Int, attackingColor: PieceColor): Boolean {
        val pawnIndex = pos - sideConfig().pawnMoveDirection
        return isPieceOfType(PieceType.PAWN, pawnIndex-1, attackingColor) ||
                isPieceOfType(PieceType.PAWN, pawnIndex+1, attackingColor)
    }

    private fun isAttackedByKnight(pos: Int, attackingColor: PieceColor): Boolean {
        for (offset in Board.KNIGHT_MOVE_OFFSETS) {
            if (isPieceOfType(PieceType.PAWN, pos+offset, attackingColor)) {
                return true
            }
        }
        return false
    }

    private fun isAttackedByPieceTypes(index: Int, pieceTypes: Set<PieceType>, offsets: Array<Int>, attackingColor: PieceColor) : Boolean {
        for (offset in offsets) {
            var currentIndex = index + offset

            while (ON_BOARD[currentIndex] && empty(currentIndex)) {
                currentIndex += offset
            }

            // see if capture
            if (ON_BOARD[currentIndex]) {
                val piece = get(currentIndex)!!
                if (piece.color == attackingColor && piece.type in pieceTypes) {
                    return true
                }
            }
        }
        return false
    }

    fun canQueenSideCastle() : Boolean {
        return canQueenSideCastle[whoseTurn]!!.get() == 0
    }

    fun canKingSideCastle() : Boolean {
        return canKingSideCastle[whoseTurn]!!.get() == 0
    }

    fun sideConfig() : SideConfig {
        return SIDE_CONFIG[whoseTurn]!!
    }

    fun setCanQueenSideCastle(newValue: Boolean) {
        if (newValue) {
            canQueenSideCastle[whoseTurn]!!.decrementAndGet()
        } else {
            canQueenSideCastle[whoseTurn]!!.incrementAndGet()
        }
    }

    fun setCanKingSideCastle(newValue: Boolean) {
        if (newValue) {
            canKingSideCastle[whoseTurn]!!.decrementAndGet()
        } else {
            canKingSideCastle[whoseTurn]!!.incrementAndGet()
        }
    }

    fun isKingInCheck(color: PieceColor): Boolean {
        return isAttacked(kingCoordinate[color]!!, color.oppositeColor())
    }


    companion object {

        val ROOK_MOVE_OFFSETS = arrayOf(-12, -1, 1, 12)
        val QUEEN_MOVE_OFFSETS = arrayOf(-13, -12, -11, -1, 1, 11, 12, 13)
        val KNIGHT_MOVE_OFFSETS = arrayOf(-25, -23, -14, -10, 10, 14, 23, 25)
        val KING_MOVE_OFFSETS = arrayOf(-13, -12, -11, -1, 1, 11, 12, 13)
        val BISHOP_MOVE_OFFSETS = arrayOf(-13, -11, 11, 13)

        // The board is implemented as a single array that wraps around, with padding around
        // the outside so that we can easily check for a piece trying to move off the board.
        // These are the resulting array indexes of the squares that make up the board itself,
        // excluding the padding:
        val BOARD_INDEXES = arrayOf(
            109, 110, 111, 112, 113, 114, 115, 116,
             97,  98,  99, 100, 101, 102, 103, 104,
             85,  86,  87,  88,  89,  90,  91,  92,
             73,  74,  75,  76,  77,  78,  79,  80,
             61,  62,  63,  64,  65,  66,  67,  68,
             49,  50,  51,  52,  53,  54,  55,  56,
             37,  38,  39,  40,  41,  42,  43,  44,
             25,  26,  27,  28,  29,  30,  31,  32
            )

        // An efficient array for testing if a particular index is on the board.
        private val ON_BOARD = arrayOf(
                   false, false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false, false, false,
            false, false, true,  true,  true,  true,  true,  true,  true,  true,  false, false,
            false, false, true,  true,  true,  true,  true,  true,  true,  true,  false, false,
            false, false, true,  true,  true,  true,  true,  true,  true,  true,  false, false,
            false, false, true,  true,  true,  true,  true,  true,  true,  true,  false, false,
            false, false, true,  true,  true,  true,  true,  true,  true,  true,  false, false,
            false, false, true,  true,  true,  true,  true,  true,  true,  true,  false, false,
            false, false, true,  true,  true,  true,  true,  true,  true,  true,  false, false,
            false, false, true,  true,  true,  true,  true,  true,  true,  true,  false, false,
            false, false, false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false, false
        )

        val SIDE_CONFIG = hashMapOf(
            PieceColor.WHITE to SideConfig(
                25,
                37,
                97,
                12),
            PieceColor.BLACK to SideConfig(
                109,
                97,
                37,
                -12
            ))

        private fun indexOf(file: Int, rank: Int) : Int {
            return 25 + 12*rank + file
        }

        fun fromStrings(a : Array<String>) : Board {
            val board = Array<Piece?>(ON_BOARD.size) { null }
            for (i in 0..7) {
                for (j in 0..7) {
                    board[indexOf(j, 7-i)] = Piece.ofChar(a[i][j])
                }
            }
            return Board(board)
        }

        fun onBoard(pos: Int) : Boolean {
            return ON_BOARD[pos]
        }

        fun fileChar(pos: Int): Any {
            return (97 + ((pos-1) % 12)).toChar()
        }
    }

    data class SideConfig(val homeRankStart: Int,
                     val pawnHomeRankStart: Int, val aboutToPromoteRankStart: Int, val pawnMoveDirection: Int) {

        fun isHomeRank(pos: Int) : Boolean {
            return homeRankStart <= pos && pos <= homeRankStart + 7
        }

        fun isPawnHomeRank(pos: Int) : Boolean {
            return pawnHomeRankStart <= pos && pos <= pawnHomeRankStart + 7
        }

        fun isAboutToPromote(pos: Int) : Boolean {
            return aboutToPromoteRankStart <= pos && pos <= aboutToPromoteRankStart + 7
        }
    }
}
