package org.computronium.chess

/**
 * The main class representing a board state, along with the move that got us here.
 */
class Board(private val board: Array<Array<Piece?>>) {

    var kingCoordinate = hashMapOf<PieceColor, Coordinate>()

    init {

        // Save the coordinate for each king, so we can figure out if the king is in check.
        for (rank in 0..7) {
            for (file in 0..7) {
                val piece = pieceAt(file, rank)
                if (piece?.type == PieceType.KING) {
                    kingCoordinate[piece.color] = Coordinate(file, rank)
                }
            }
        }
    }

    fun copy() : Board {
        val boardCopy = Array(8) { Array<Piece?>(8) { null }}

        for (rank in 0..7) {
            boardCopy[rank] = board[rank].clone()
        }

        return Board(boardCopy)
    }

    override fun toString(): String {

        val sb = StringBuilder()
        for (rank in 7 downTo 0) {
            for (file in 0..7) {
                sb.append("${board[file][rank] ?: (if ((file+rank)%2==0) "." else " ")} ")
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    fun pieceAt(pos: Coordinate): Piece? {
        return pieceAt(pos.file, pos.rank)
    }

    fun pieceAt(file: Int, rank: Int): Piece? {
        return if (onBoard(file, rank)) board[file][rank] else null
    }

    fun empty(pos: Coordinate): Boolean {
        return empty(pos.file, pos.rank)
    }

    fun empty(file: Int, rank: Int): Boolean {
        return pieceAt(file, rank) == null
    }

    fun set(pos: Coordinate, piece: Piece?) {
        board[pos.file][pos.rank] = piece
    }

    fun piecePositions(color: PieceColor): List<Coordinate> {
        val positions = mutableListOf<Coordinate>()
        for (rank in 0..7) {
            for (file in 0..7) {
                if (board[file][rank]?.color == color) {
                    positions.add(Coordinate(file, rank))
                }
            }
        }
        return positions
    }

    fun isAttackedBy(color: PieceColor, coord: Coordinate) : Boolean {
        return isAttackedByPieceTypes(color, coord, setOf(PieceType.QUEEN, PieceType.ROOK), HORIZONTAL_AND_VERTICAL_OFFSETS) ||
                isAttackedByPieceTypes(color, coord, setOf(PieceType.QUEEN, PieceType.BISHOP), DIAGONAL_OFFSETS) ||
                isAttackedByKnight(color, coord) ||
                isAttackedByPawn(color, coord) ||
                isAttackedByKing(color, coord)
    }

    private fun isAttackedByKing(color: PieceColor, coord: Coordinate): Boolean {
        for (dx in -1..1) {
            for (dy in -1..1) {
                if (dx != 0 || dy != 0) {
                    val piece = pieceAt(coord.file + dx, coord.rank + dy)
                    if (piece?.color == color && piece.type == PieceType.KING) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun isAttackedByPawn(color: PieceColor, coord: Coordinate): Boolean {
        val pawnRank = coord.rank - PAWN_MOVE_DIRECTION[color]!!
        var piece = pieceAt(coord.file-1, pawnRank)
        if (piece?.color == color && piece.type == PieceType.PAWN) {
            return true
        }
        piece = pieceAt(coord.file+1, pawnRank)
        if (piece?.color == color && piece.type == PieceType.PAWN) {
            return true
        }
        return false
    }

    private fun isAttackedByKnight(color: PieceColor, coord: Coordinate): Boolean {
        for ((dx, dy) in KNIGHT_OFFSETS) {
            val piece = pieceAt(coord.file + dx, coord.rank + dy)
            if (piece?.color == color && piece.type == PieceType.KNIGHT) {
                return true
            }
        }
        return false
    }

    private fun isAttackedByPieceTypes(color: PieceColor, coord: Coordinate, pieceTypes: Set<PieceType>, offsets: Array<Pair<Int, Int>>) : Boolean {
        for ((dx, dy) in offsets) {
            var rank = coord.rank + dx
            var file = coord.file + dy

            while (onBoard(file, rank) && empty(file, rank)) {
                rank += dx
                file += dy
            }

            // see if capture
            if (onBoard(file, rank)) {
                val piece = pieceAt(file, rank)!!
                if (piece.color == color && piece.type in pieceTypes) {
                    return true
                }
            }
        }
        return false
    }

    companion object {

        val PAWN_MOVE_DIRECTION = hashMapOf(PieceColor.WHITE to 1, PieceColor.BLACK to -1)
        val HOME_RANK = hashMapOf(PieceColor.WHITE to 0, PieceColor.BLACK to 7)
        val PAWN_HOME_RANK = hashMapOf(PieceColor.WHITE to 1, PieceColor.BLACK to 6)
        val PAWN_ABOUT_TO_PROMOTE_RANK = hashMapOf(PieceColor.WHITE to 6, PieceColor.BLACK to 1)

        val HORIZONTAL_AND_VERTICAL_OFFSETS = arrayOf(Pair(-1, 0), Pair(0, -1), Pair(1, 0), Pair(0, 1))
        val DIAGONAL_OFFSETS = arrayOf(Pair(-1, -1), Pair(-1, 1), Pair(1, 1), Pair(1, -1))
        val KNIGHT_OFFSETS = arrayOf(Pair(1, 2), Pair(1, -2), Pair(-1, 2), Pair(-1, -2),
                                                        Pair(2, 1), Pair(2, -1), Pair(-2, 1), Pair(-2, -1))

        fun onBoard(file: Int, rank: Int) : Boolean {
            return rank in 0..7 && file in 0..7
        }

        fun fromStrings(a : Array<String>) : Board {
            val board = Array(8) { Array<Piece?>(8) { null }}
            for (i in 0..7) {
                for (j in 0..7) {
                    board[j][7-i] = Piece.from(a[i][j])
                }
            }
            return Board(board)
        }
    }
}
