package co.jfactory.konnect4.scoring

import co.jfactory.konnect4.model.Board
import co.jfactory.konnect4.model.CellColour
import co.jfactory.konnect4.model.getAvailableMoves

class BoardScorer(val board: Board) {
    companion object {
        val SCORING_PATTERNS = listOf(Pair(Regex(".*XXXX.*"), 10000),
                Pair(Regex(".*_XXX_.*"), 1024),
                Pair(Regex(".*_XXX.*|.*XXX_.*|.*XX_X.*|.*X_XX.*"), 256),
                Pair(Regex(".*__XX_.*|.*_XX__.*"), 64),
                Pair(Regex(".*X__X.*|.*_XX_.*"), 16),
                Pair(Regex(".{7,}+"), 12),
                Pair(Regex(".{6,}+"), 8),
                Pair(Regex(".{5,}+"), 6),
                Pair(Regex(".{4,}+"), 4),
                Pair(Regex(".{1,}+"), 1))

        fun scoreMove(move: String): Int {
            return SCORING_PATTERNS.find { it.first.matches(move) }!!.second
        }
    }

    fun scorePosition(column: Int, row: Int, colour: CellColour): Int {
        return board.getAvailableMoves(column, row, colour).map { scoreMove(it) }.sum()
    }
}