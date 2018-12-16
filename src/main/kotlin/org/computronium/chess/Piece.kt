package org.computronium.chess

data class Piece(val color: PieceColor, val type: PieceType) {

    override fun toString(): String {
        return (if (color == PieceColor.WHITE) type.letter else type.letter.toLowerCase()).toString()
    }

    companion object {
        fun from(c : Char) : Piece? {
            if (c == ' ' || c == '.') return null
            val type = PieceType.from(c)
            return if (type == null) null else Piece(PieceColor.from(c), type)
        }
    }
}
