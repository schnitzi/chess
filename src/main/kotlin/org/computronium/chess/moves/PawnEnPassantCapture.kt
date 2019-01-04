package org.computronium.chess.moves

import org.computronium.chess.Board
import org.computronium.chess.Piece

class PawnEnPassantCapture(val from: Int) : Move() {

    private var capturedPiece: Piece? = null

    override fun apply(board: Board) {

        val pawnToCapturePos = board.enPassantCapturePos!! - board.sideConfig().pawnMoveDirection
        capturedPiece = board[pawnToCapturePos]
        board[pawnToCapturePos] = null
        board[board.enPassantCapturePos!!] = board[from]
        board[from] = null

        super.apply(board)
    }

    override fun rollback(board: Board) {

        super.rollback(board)

        val pawnToCapturePos : Int = board.enPassantCapturePos!! - board.sideConfig().pawnMoveDirection
        board[from] = board[pawnToCapturePos]
        board[pawnToCapturePos] = capturedPiece
        board[board.enPassantCapturePos!!] = null
    }
}