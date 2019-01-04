package org.computronium.chess

enum class Piece(val color: PieceColor, val type: PieceType, val char: Char) {

        WHITE_KING(PieceColor.WHITE, PieceType.KING, 'K'),
        WHITE_QUEEN(PieceColor.WHITE, PieceType.QUEEN, 'Q'),
        WHITE_BISHOP(PieceColor.WHITE, PieceType.BISHOP, 'B'),
        WHITE_KNIGHT(PieceColor.WHITE, PieceType.KNIGHT, 'K'),
        WHITE_PAWN(PieceColor.WHITE, PieceType.PAWN, 'P'),
        BLACK_KING(PieceColor.BLACK, PieceType.KING, 'k'),
        BLACK_QUEEN(PieceColor.BLACK, PieceType.QUEEN, 'q'),
        BLACK_BISHOP(PieceColor.BLACK, PieceType.BISHOP, 'b'),
        BLACK_KNIGHT(PieceColor.BLACK, PieceType.KNIGHT, 'k'),
        BLACK_PAWN(PieceColor.BLACK, PieceType.PAWN, 'p');

    companion object {
        fun ofChar(char: Char) : Piece? {
            for (piece in values()) {
                if (piece.char == char) {
                    return piece
                }
            }
            return null
        }

        fun forTypeAndColor(type: PieceType, color: PieceColor): Piece? {
            for (piece in values()) {
                if (piece.type == type && piece.color == color) {
                    return piece
                }
            }
            return null
        }
    }
}
