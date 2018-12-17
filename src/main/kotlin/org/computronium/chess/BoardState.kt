package org.computronium.chess

import org.computronium.chess.Board.Companion.DIAGONAL_OFFSETS
import org.computronium.chess.Board.Companion.HOME_RANK
import org.computronium.chess.Board.Companion.HORIZONTAL_AND_VERTICAL_OFFSETS
import org.computronium.chess.Board.Companion.PAWN_ABOUT_TO_PROMOTE_RANK
import org.computronium.chess.Board.Companion.PAWN_HOME_RANK
import org.computronium.chess.Board.Companion.PAWN_MOVE_DIRECTION
import java.util.*

/**
 * The main class representing a board state, along with the move that got us here.
 */
class BoardState(val board: Board, private val whoseTurn: PieceColor, private val moveNumber: Int, val lastBoardState : BoardState?) {

    private var enPassantCaptureCoordinate: Coordinate? = null

    private var enPassantPawnToCapture: Coordinate? = null
    
    private var canQueensideCastle = hashMapOf(PieceColor.WHITE to false, PieceColor.BLACK to false)
    private var canKingsideCastle = hashMapOf(PieceColor.WHITE to false, PieceColor.BLACK to false)

    private var lastMove : Move? = null

    var whoseTurnIsInCheck = false


    override fun toString(): String {

        val sb = StringBuilder()

        sb.append(board)

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

    fun findMoves(): List<BoardState> {

        val piecePositions = board.piecePositions(whoseTurn)

        val moves = mutableListOf<BoardState>()

        for (piecePosition in piecePositions) {
            findMoves(moves, piecePosition)
        }

        val legalMoves = moves.filter { move -> move.isLegal() }
        for (legalMove in legalMoves) {
            legalMove.whoseTurnIsInCheck = legalMove.board.isAttackedBy(whoseTurn, legalMove.board.kingCoordinate[legalMove.whoseTurn]!!)
        }

        return legalMoves
    }

    private fun isLegal(): Boolean {
        return !board.isAttackedBy(whoseTurn, board.kingCoordinate[whoseTurn.oppositeColor()]!!)
    }

    private fun findMoves(moves: MutableList<BoardState>, coordinate: Coordinate) {

        val piece = board.pieceAt(coordinate)
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

        for ((dx, dy) in Board.KNIGHT_OFFSETS) {
            val rank = from.rank + dx
            val file = from.file + dy

            if (onBoard(file, rank) && board.pieceAt(file, rank)?.color != whoseTurn) {
                moves.add(doMove(from, Coordinate(file, rank)))
            }
        }
    }

    private fun findMovesViaOffsets(offsets: Array<Pair<Int, Int>>, from: Coordinate) : MutableList<BoardState> {

        val moves = mutableListOf<BoardState>()

        for ((dx, dy) in offsets) {
            var rank = from.rank + dx
            var file = from.file + dy

            while (onBoard(file, rank) && board.empty(file, rank)) {
                moves.add(doMove(from, Coordinate(file, rank)))
                rank += dx
                file += dy
            }

            // see if capture
            if (onBoard(file, rank) && board.pieceAt(file, rank)?.color == whoseTurn.oppositeColor()) {
                moves.add(doMove(from, Coordinate(file, rank)))
            }
        }

        return moves
    }

    private fun findKingMoves(moves: MutableList<BoardState>, from: Coordinate) {

        for (dx in -1..1) {
            for (dy in -1..1) {
                if (dx != 0 || dy != 0) {
                    if (onBoard(from.file + dx, from.rank + dy) && board.pieceAt(from.file + dx, from.rank + dy)?.color != whoseTurn) {
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
            val homeRank = Board.HOME_RANK[whoseTurn]!!
            if (canQueensideCastle[whoseTurn]!! &&
                    board.empty(1, homeRank) &&
                    board.empty(2, homeRank) &&
                    board.empty(3, homeRank) &&
                    !board.isAttackedBy(whoseTurn.oppositeColor(), Coordinate(3, homeRank))) {
                moves.add(doQueensideCastle())
            }

            if (canKingsideCastle[whoseTurn]!! &&
                    board.empty(5, homeRank) &&
                    board.empty(6, homeRank) &&
                    !board.isAttackedBy(whoseTurn.oppositeColor(), Coordinate(5, homeRank))) {
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

        if (board.empty(forwardOnePosition)) {

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

                if (board.empty(forwardTwoPosition)) {

                    // We want to also note the special coordinates for handling en passant.
                    moves.add(doInitialPawnMove(from, forwardTwoPosition, forwardOnePosition))
                }
            }
        }
        
        // Pawn captures.
        for (dx in listOf(-1, 1)) {
            if (onBoard(from.file + dx, from.rank + dir)) {
                val captureLeftCoordinate = Coordinate(from.file + dx, from.rank + dir)

                if (board.pieceAt(captureLeftCoordinate)?.color == whoseTurn.oppositeColor()) {

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

        val copy = BoardState(board.copy(), whoseTurn.oppositeColor(), if (whoseTurn == PieceColor.WHITE) moveNumber else moveNumber+1, this)
        copy.canQueensideCastle = HashMap(canQueensideCastle)
        copy.canKingsideCastle = HashMap(canKingsideCastle)
        return copy
    }

    /**
     * Do an ordinary move.
     */
    private fun doMove(from: Coordinate, to: Coordinate) : BoardState {
        val move = copyForNextMove()
        val piece = board.pieceAt(from)
        move.lastMove = Move(piece, from, to, board.pieceAt(to))
        move.board.set(from, null)
        move.board.set(to, piece)
        if (piece!!.type == PieceType.KING) {
            move.board.kingCoordinate[whoseTurn] = to
        }

        return move
    }

    private fun doQueensideCastle() : BoardState {
        val rank = HOME_RANK[whoseTurn]!!
        val move = doMove(Coordinate(4, rank), Coordinate(2, rank))
        val rookCoord = Coordinate(0, rank)
        move.board.set(Coordinate(3, rank), board.pieceAt(rookCoord))
        move.board.set(rookCoord, null)
        move.canQueensideCastle[whoseTurn] = false
        move.canKingsideCastle[whoseTurn] = false
        return move
    }

    private fun doKingsideCastle() : BoardState {
        val rank = HOME_RANK[whoseTurn]!!
        val move = doMove(Coordinate(4, rank), Coordinate(6, rank))
        val rookCoord = Coordinate(7, rank)
        move.board.set(Coordinate(5, rank), board.pieceAt(rookCoord))
        move.board.set(rookCoord, null)
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
        move.lastMove!!.capturedPiece = board.pieceAt(enPassantPawnToCapture!!)
        move.board.set(enPassantPawnToCapture!!, null)
        move.lastMove!!.isEnPassantCapture = true

        return move
    }

    /**
     * Do a a pawn promotion.
     */
    private fun doPawnPromotion(from: Coordinate, to: Coordinate, type: PieceType) : BoardState {

        val move = doMove(from, to)
        move.board.set(to, Piece(whoseTurn, type))
        move.lastMove!!.promotionTo = type

        return move
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BoardState

        if (board != other.board) return false
        if (whoseTurn != other.whoseTurn) return false
        if (enPassantCaptureCoordinate != other.enPassantCaptureCoordinate) return false
        if (enPassantPawnToCapture != other.enPassantPawnToCapture) return false
        if (canQueensideCastle != other.canQueensideCastle) return false
        if (canKingsideCastle != other.canKingsideCastle) return false

        return true
    }

    override fun hashCode(): Int {
        var result = board.hashCode()
        result = 31 * result + whoseTurn.hashCode()
        result = 31 * result + (enPassantCaptureCoordinate?.hashCode() ?: 0)
        result = 31 * result + (enPassantPawnToCapture?.hashCode() ?: 0)
        result = 31 * result + canQueensideCastle.hashCode()
        result = 31 * result + canKingsideCastle.hashCode()
        return result
    }


    companion object {

        private fun fromStrings(a : Array<String>) : BoardState {
            return fromStrings(a, PieceColor.WHITE)
        }

        fun fromStrings(a : Array<String>, whoseTurn: PieceColor) : BoardState {
            return BoardState(Board.fromStrings(a), whoseTurn, 1, null)
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
