package org.computronium.chess

import org.computronium.chess.Board.Companion.QUEEN_MOVE_OFFSETS
import org.computronium.chess.Board.Companion.ROOK_MOVE_OFFSETS
import org.computronium.chess.Board.Companion.onBoard
import org.computronium.chess.moves.*

class SearchNode(val board: Board) {

    val enPassantCaptureCoordinate = board.enPassantCapturePos

    val moves = mutableListOf<Move>()

    init {

        val piecePositions = board.piecePositions(board.whoseTurn)

        for (piecePosition in piecePositions) {
            findMoves(piecePosition)
        }
    }

    private fun maybeAddMove(move: Move) {
        move.apply(board)
        if (!board.isKingInCheck(board.whoseTurn.oppositeColor())) {
            board.whoseTurnIsInCheck = board.isKingInCheck(board.whoseTurn)
            moves.add(move)
        }
        move.rollback(board)
    }

    private fun findMoves(index: Int) {

        when (board[index]?.type) {
            PieceType.PAWN -> findPawnMoves(index)
            PieceType.ROOK -> findRookMoves(index)
            PieceType.BISHOP -> findBishopMoves(index)
            PieceType.KNIGHT -> findKnightMoves(index)
            PieceType.QUEEN -> findQueenMoves(index)
            PieceType.KING -> findKingMoves(index)
        }
    }

    private fun findKnightMoves(pos: Int) {

        for (offset in Board.KNIGHT_MOVE_OFFSETS) {
            val newPos = pos + offset

            if (Board.onBoard(newPos) && board[newPos]?.color != board.whoseTurn) {
                maybeAddMove(StandardMove(pos, newPos))
            }
        }
    }

    private fun findMovesViaOffsets(offsets: Array<Int>, from: Int) {

        for (offset in offsets) {
            var newIndex = from + offset

            while (Board.onBoard(newIndex) && board.empty(newIndex)) {
                maybeAddMove(StandardMove(from, newIndex))
                newIndex += offset
            }

            // see if capture
            if (onBoard(newIndex) && board[newIndex]?.color == board.whoseTurn.oppositeColor()) {
                maybeAddMove(StandardCapture(from, newIndex))
            }
        }
    }

    private fun findKingMoves(from: Int) {

        for (offset in Board.KING_MOVE_OFFSETS) {
            val to = from + offset
            if (onBoard(to) && board[to]?.color != board.whoseTurn) {
                maybeAddMove(KingMove(from, to))
            }
        }

        if (KingCastleQueenSide.isPossible(board)) {
            maybeAddMove(KingCastleQueenSide())
        }

        if (KingCastleKingSide.isPossible(board)) {
            maybeAddMove(KingCastleKingSide())
        }
    }

    private fun findBishopMoves(from: Int) {

        findMovesViaOffsets(Board.BISHOP_MOVE_OFFSETS, from)
    }

    private fun findRookMoves(from: Int) {

        for (offset in ROOK_MOVE_OFFSETS) {
            var newIndex = from + offset

            while (Board.onBoard(newIndex) && board.empty(newIndex)) {
                maybeAddMove(RookMove(from, newIndex))
                newIndex += offset
            }

            // see if capture
            if (onBoard(newIndex) && board[newIndex]?.color == board.whoseTurn.oppositeColor()) {
                maybeAddMove(RookCapture(from, newIndex))
            }
        }
    }

    private fun findQueenMoves(from: Int) {

        findMovesViaOffsets(QUEEN_MOVE_OFFSETS, from)
    }


    private fun findPawnMoves(from: Int) {

        val dir = board.sideConfig().pawnMoveDirection

        // Can the pawn move forward one?
        val forwardOnePosition = from + dir

        if (board.empty(forwardOnePosition)) {

            if (board.sideConfig().isAboutToPromote(from)) {
                maybeAddMove(PawnPromotion(from, forwardOnePosition, PieceType.QUEEN))
                maybeAddMove(PawnPromotion(from, forwardOnePosition, PieceType.ROOK))
                maybeAddMove(PawnPromotion(from, forwardOnePosition, PieceType.KNIGHT))
                maybeAddMove(PawnPromotion(from, forwardOnePosition, PieceType.BISHOP))
            } else {
                maybeAddMove(StandardMove(from, forwardOnePosition))
            }

            // Can the pawn move forward two?
            if (board.sideConfig().isPawnHomeRank(from)) {

                val forwardTwoPosition = from + dir + dir

                if (board.empty(forwardTwoPosition)) {

                    // We want to also note the special coordinates for handling en passant.
                    maybeAddMove(PawnInitialMove(from, forwardTwoPosition, forwardOnePosition))
                }
            }
        }
        
        // Pawn captures.
        for (dx in listOf(-1, 1)) {
            val capturePos = from + dx
            if (onBoard(capturePos)) {
                if (board[capturePos]?.color == board.whoseTurn.oppositeColor()) {

                    if (board.sideConfig().isAboutToPromote(from)) {
                        // Capture with promotion.
                        maybeAddMove(PawnPromotion(from, capturePos, PieceType.QUEEN))
                        maybeAddMove(PawnPromotion(from, capturePos, PieceType.ROOK))
                        maybeAddMove(PawnPromotion(from, capturePos, PieceType.KNIGHT))
                        maybeAddMove(PawnPromotion(from, capturePos, PieceType.BISHOP))
                    } else {
                        // Ordinary capture.
                        maybeAddMove(StandardCapture(from, capturePos))
                    }
                } else if (capturePos == board.enPassantCapturePos) {
                    // En passant capture.
                    maybeAddMove(PawnEnPassantCapture(from))
                }
            }
        }
    }

    fun isCheckmate(): Boolean {
        return board.whoseTurnIsInCheck && moves.isEmpty()
    }

    fun isStalemate(): Boolean {
        return !board.whoseTurnIsInCheck && moves.isEmpty()
    }

    companion object {

        private fun fromStrings(a : Array<String>) : SearchNode {
            return fromStrings(a, PieceColor.WHITE)
        }

        fun fromStrings(a : Array<String>, whoseTurn: PieceColor) : SearchNode {
            return SearchNode(Board.fromStrings(a))
        }

        fun newGame() : SearchNode {
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
