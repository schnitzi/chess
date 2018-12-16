package org.computronium.chess

import java.util.*

/**
 * The main class representing a board state, along with the move that got us here.
 */
class BoardState(private val board: Array<Array<Piece?>>, private val whoseTurn: PieceColor, private val moveNumber: Int, val lastBoardState : BoardState?) {

    private var enPassantCaptureCoordinate: Coordinate? = null

    private var enPassantPawnToCapture: Coordinate? = null
    
    private var canQueensideCastle = hashMapOf(PieceColor.WHITE to false, PieceColor.BLACK to false)
    private var canKingsideCastle = hashMapOf(PieceColor.WHITE to false, PieceColor.BLACK to false)

    private var kingCoordinate = hashMapOf<PieceColor, Coordinate>()

    private var lastMove : Move? = null

    var whoseTurnIsInCheck = false


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


    override fun toString(): String {

        val sb = StringBuilder()
        for (rank in 7 downTo 0) {
            for (file in 0..7) {
                sb.append("${board[file][rank] ?: (if ((file+rank)%2==0) "." else " ")} ")
            }
            sb.append("\n")
        }
        if (lastMove != null) {
            sb.append("$moveNumber. ")
            if (whoseTurn == PieceColor.WHITE) {
                sb.append("... ")
            }
            sb.append("$lastMove\n")
        }
        sb.append("$whoseTurn to move")
        return sb.toString()
    }

    fun pieceAt(pos: Coordinate): Piece? {
        return pieceAt(pos.file, pos.rank)
    }

    private fun pieceAt(file: Int, rank: Int): Piece? {
        return if (onBoard(file, rank)) board[file][rank] else null
    }

    private fun empty(pos: Coordinate): Boolean {
        return empty(pos.file, pos.rank)
    }

    private fun empty(file: Int, rank: Int): Boolean {
        return pieceAt(file, rank) == null
    }

    fun set(pos: Coordinate, piece: Piece?) {
        board[pos.file][pos.rank] = piece
    }

    private fun piecePositions(color: PieceColor): List<Coordinate> {
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

    fun findMoves(): List<BoardState> {

        val piecePositions = piecePositions(whoseTurn)

        val moves = mutableListOf<BoardState>()

        for (piecePosition in piecePositions) {
            findMoves(moves, piecePosition)
        }

        val legalMoves = moves.filter { move -> move.isLegal() }
        for (legalMove in legalMoves) {
            legalMove.whoseTurnIsInCheck = legalMove.isAttackedBy(whoseTurn, legalMove.kingCoordinate[legalMove.whoseTurn]!!)
        }

        return legalMoves
    }

    private fun isLegal(): Boolean {
        return !isAttackedBy(whoseTurn, kingCoordinate[whoseTurn.oppositeColor()]!!)
    }

    private fun isAttackedBy(color: PieceColor, coord: Coordinate) : Boolean {
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

    private fun findMoves(moves: MutableList<BoardState>, coordinate: Coordinate) {

        val piece = pieceAt(coordinate)
        when (piece?.type) {
            PieceType.PAWN -> findPawnMoves(moves, coordinate)
            PieceType.ROOK -> findRookMoves(moves, coordinate)
            PieceType.BISHOP -> findBishopMoves(moves, coordinate)
            PieceType.KNIGHT -> findKnightMoves(moves, coordinate)
            PieceType.QUEEN -> findQueenMoves(moves, coordinate)
            PieceType.KING -> findKingMoves(moves, coordinate)
        }
    }

    private fun onBoard(file: Int, rank: Int) : Boolean {
        return rank in 0..7 && file in 0..7
    }

    private fun findKnightMoves(moves: MutableList<BoardState>, from: Coordinate) {

        for ((dx, dy) in KNIGHT_OFFSETS) {
            val rank = from.rank + dx
            val file = from.file + dy

            if (onBoard(file, rank) && pieceAt(file, rank)?.color != whoseTurn) {
                moves.add(doMove(from, Coordinate(file, rank)))
            }
        }
    }

    private fun findMovesViaOffsets(offsets: Array<Pair<Int, Int>>, from: Coordinate) : MutableList<BoardState> {

        val moves = mutableListOf<BoardState>()

        for ((dx, dy) in offsets) {
            var rank = from.rank + dx
            var file = from.file + dy

            while (onBoard(file, rank) && empty(file, rank)) {
                moves.add(doMove(from, Coordinate(file, rank)))
                rank += dx
                file += dy
            }

            // see if capture
            if (onBoard(file, rank) && pieceAt(file, rank)?.color == whoseTurn.oppositeColor()) {
                moves.add(doMove(from, Coordinate(file, rank)))
            }
        }

        return moves
    }

    private fun findKingMoves(moves: MutableList<BoardState>, from: Coordinate) {

        for (dx in -1..1) {
            for (dy in -1..1) {
                if (dx != 0 || dy != 0) {
                    if (onBoard(from.file + dx, from.rank + dy) && pieceAt(from.file + dx, from.rank + dy)?.color != whoseTurn) {
                        val move = doMove(from, Coordinate(from.file + dx, from.rank + dy))

                        // Moving the king means they can't castle afterwards.
                        move.canQueensideCastle[whoseTurn] = false
                        move.canKingsideCastle[whoseTurn] = false

                        moves.add(move)
                    }
                }
            }
        }

        if (!whoseTurnIsInCheck) {
            val homeRank = HOME_RANK[whoseTurn]!!
            if (canQueensideCastle[whoseTurn]!! &&
                    empty(1, homeRank) &&
                    empty(2, homeRank) &&
                    empty(3, homeRank) &&
                    !isAttackedBy(whoseTurn.oppositeColor(), Coordinate(3, homeRank))) {
                moves.add(doQueensideCastle())
            }

            if (canKingsideCastle[whoseTurn]!! &&
                    empty(5, homeRank) &&
                    empty(6, homeRank) &&
                    !isAttackedBy(whoseTurn.oppositeColor(), Coordinate(5, homeRank))) {
                moves.add(doKingsideCastle())
            }
        }
    }

    private fun findBishopMoves(moves: MutableList<BoardState>, from: Coordinate) {

        moves.addAll(findMovesViaOffsets(DIAGONAL_OFFSETS, from))
    }

    private fun findRookMoves(moves: MutableList<BoardState>, from: Coordinate) {

        val rookMoves = findMovesViaOffsets(HORIZONTAL_AND_VERTICAL_OFFSETS, from)
        if (whoseTurn == PieceColor.WHITE) {
            if (from.file == 0 && from.rank == 0) {
                for (rookMove in rookMoves) {
                    rookMove.canQueensideCastle[whoseTurn] = false
                }
            } else if (from.file == 7 && from.rank == 0) {
                for (rookMove in rookMoves) {
                    rookMove.canKingsideCastle[whoseTurn] = false
                }
            }
        } else if (from.file == 0 && from.rank == 7) {
            for (rookMove in rookMoves) {
                rookMove.canQueensideCastle[whoseTurn] = false
            }
        } else if (from.file == 7 && from.rank == 7) {
            for (rookMove in rookMoves) {
                rookMove.canKingsideCastle[whoseTurn] = false
            }
        }
        moves.addAll(rookMoves)
    }

    private fun findQueenMoves(moves: MutableList<BoardState>, from: Coordinate) {

        moves.addAll(findMovesViaOffsets(HORIZONTAL_AND_VERTICAL_OFFSETS, from))
        moves.addAll(findMovesViaOffsets(DIAGONAL_OFFSETS, from))
    }


    private fun findPawnMoves(moves: MutableList<BoardState>, from: Coordinate) {

        val dir = PAWN_MOVE_DIRECTION[whoseTurn]!!

        // Can the pawn move forward one?
        val forwardOnePosition = Coordinate(from.file, from.rank + dir)

        if (empty(forwardOnePosition)) {

            if (from.rank == PAWN_ABOUT_TO_PROMOTE_RANK[whoseTurn]) {
                moves.add(doPawnPromotion(from, forwardOnePosition, PieceType.QUEEN))
                moves.add(doPawnPromotion(from, forwardOnePosition, PieceType.ROOK))
                moves.add(doPawnPromotion(from, forwardOnePosition, PieceType.KNIGHT))
                moves.add(doPawnPromotion(from, forwardOnePosition, PieceType.BISHOP))
            } else {
                moves.add(doMove(from, forwardOnePosition))
            }

            // Can the pawn move forward two?
            if (from.rank == PAWN_HOME_RANK[whoseTurn]) {

                val forwardTwoPosition = Coordinate(from.file, from.rank + dir + dir)

                if (empty(forwardTwoPosition)) {

                    // We want to also note the special coordinates for handling en passant.
                    moves.add(doInitialPawnMove(from, forwardTwoPosition, forwardOnePosition))
                }
            }
        }
        
        // Pawn captures.
        for (dx in listOf(-1, 1)) {
            if (onBoard(from.file + dx, from.rank + dir)) {
                val captureLeftCoordinate = Coordinate(from.file + dx, from.rank + dir)

                if (pieceAt(captureLeftCoordinate)?.color == whoseTurn.oppositeColor()) {

                    if (from.rank == PAWN_ABOUT_TO_PROMOTE_RANK[whoseTurn]) {
                        // Capture with promotion.
                        moves.add(doPawnPromotion(from, captureLeftCoordinate, PieceType.QUEEN))
                        moves.add(doPawnPromotion(from, captureLeftCoordinate, PieceType.ROOK))
                        moves.add(doPawnPromotion(from, captureLeftCoordinate, PieceType.KNIGHT))
                        moves.add(doPawnPromotion(from, captureLeftCoordinate, PieceType.BISHOP))
                    } else {
                        // Ordinary capture.
                        moves.add(doMove(from, captureLeftCoordinate))
                    }
                } else if (captureLeftCoordinate == enPassantCaptureCoordinate) {
                    // En passant capture.
                    moves.add(doEnPassantCapture(from))
                }
            }
        }
    }

    /**
     * Clone the board and reverse whose turn it is.
     */
    private fun copyForNextMove() : BoardState {

        val boardCopy = Array(8) { Array<Piece?>(8) { null }}

        for (rank in 0..7) {
            boardCopy[rank] = board[rank].clone()
        }

        val copy = BoardState(boardCopy, whoseTurn.oppositeColor(), if (whoseTurn == PieceColor.WHITE) moveNumber else moveNumber+1, this)
        copy.canQueensideCastle = HashMap(canQueensideCastle)
        copy.canKingsideCastle = HashMap(canKingsideCastle)
        copy.kingCoordinate = HashMap(kingCoordinate)
        return copy
    }

    /**
     * Do an ordinary move.
     */
    private fun doMove(from: Coordinate, to: Coordinate) : BoardState {
        val move = copyForNextMove()
        val piece = pieceAt(from)
        move.lastMove = Move(piece, from, to, pieceAt(to))
        move.set(from, null)
        move.set(to, piece)
        if (piece!!.type == PieceType.KING) {
            move.kingCoordinate[whoseTurn] = to
        }

        return move
    }

    private fun doQueensideCastle() : BoardState {
        val rank = HOME_RANK[whoseTurn]!!
        val move = doMove(Coordinate(4, rank), Coordinate(2, rank))
        val rookCoord = Coordinate(0, rank)
        move.set(Coordinate(3, rank), pieceAt(rookCoord))
        move.set(rookCoord, null)
        move.canQueensideCastle[whoseTurn] = false
        move.canKingsideCastle[whoseTurn] = false
        return move
    }

    private fun doKingsideCastle() : BoardState {
        val rank = HOME_RANK[whoseTurn]!!
        val move = doMove(Coordinate(4, rank), Coordinate(6, rank))
        val rookCoord = Coordinate(7, rank)
        move.set(Coordinate(5, rank), pieceAt(rookCoord))
        move.set(rookCoord, null)
        move.canQueensideCastle[whoseTurn] = false
        move.canKingsideCastle[whoseTurn] = false
        return move
    }

    /**
     * Do an initial pawn move (forward two spaces).
     */
    private fun doInitialPawnMove(from: Coordinate, to: Coordinate, passingOver: Coordinate) : BoardState {

        val move = doMove(from, to)

        // The square that the pawn just jumped over is the place where the opposing player could capture en passant.
        move.enPassantCaptureCoordinate = passingOver
        move.enPassantPawnToCapture = to
        
        return move
    }

    /**
     * Do an en passant capture.
     */
    private fun doEnPassantCapture(from: Coordinate) : BoardState {

        val move = doMove(from, enPassantCaptureCoordinate!!)
        move.lastMove!!.capturedPiece = pieceAt(enPassantPawnToCapture!!)
        move.set(enPassantPawnToCapture!!, null)
        move.lastMove!!.isEnPassantCapture = true

        return move
    }

    /**
     * Do a a pawn promotion.
     */
    private fun doPawnPromotion(from: Coordinate, to: Coordinate, type: PieceType) : BoardState {

        val move = doMove(from, to)
        move.set(to, Piece(whoseTurn, type))
        move.lastMove!!.promotionTo = type

        return move
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BoardState

        if (!Arrays.deepEquals(board, other.board)) return false
        if (whoseTurn != other.whoseTurn) return false
        if (enPassantCaptureCoordinate != other.enPassantCaptureCoordinate) return false
        if (enPassantPawnToCapture != other.enPassantPawnToCapture) return false

        return true
    }

    override fun hashCode(): Int {
        var result = board.contentDeepHashCode()
        result = 31 * result + whoseTurn.hashCode()
        result = 31 * result + (enPassantCaptureCoordinate?.hashCode() ?: 0)
        result = 31 * result + (enPassantPawnToCapture?.hashCode() ?: 0)
        return result
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

        private fun fromStrings(a : Array<String>) : BoardState {
            return fromStrings(a, PieceColor.WHITE)
        }

        fun fromStrings(a : Array<String>, whoseTurn: PieceColor) : BoardState {
            val board = Array(8) { Array<Piece?>(8) { null }}
            for (i in 0..7) {
                for (j in 0..7) {
                    board[j][7-i] = Piece.from(a[i][j])
                }
            }
            return BoardState(board, whoseTurn, 1, null)
        }

        fun newGame() : BoardState {
            return fromStrings(arrayOf(
                "rnbqkbnr",
                "pppppppp",
                "        ",
                "        ",
                "        ",
                "        ",
                "PPPPPPPP",
                "RNBQKBNR"
            ))
        }
    }
}
