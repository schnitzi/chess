package org.computronium.chess

/**
 * Class representing a move.
 */
data class Move(var piece : Piece? = null, var from : Coordinate? = null, var to : Coordinate? = null,
                var capturedPiece : Piece? = null, var isEnPassantCapture : Boolean = false,
                var promotionTo : PieceType? = null) {

    /**
     * This will generate a simplified version of the last move in Algebraic notation that
     * suffices in most cases.  However, sometimes this version is ambiguous, and needs more
     * information, which can't be determined without comparison to other moves.  Figuring
     * out how to do this is something I still need TODO.
     */
    override fun toString(): String {
        val sb = StringBuilder()
        if (piece!!.type != PieceType.PAWN) {
            sb.append(piece!!.type.letter)
        } else if (capturedPiece != null) {
            sb.append(from!!.fileChar())
        }
        if (capturedPiece != null) {
            sb.append("x")
        }
        sb.append(to)
        if (isEnPassantCapture) {
            sb.append("e.p.")
        }
        return sb.toString()
    }
}
