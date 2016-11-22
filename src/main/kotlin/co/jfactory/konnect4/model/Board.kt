package co.jfactory.konnect4.model

import co.jfactory.konnect4.model.Direction.Companion.DIRECTIONS

/**
 * Representation of the current state of the board.
 */
data class Board(private val cells: List<List<co.jfactory.konnect4.model.CellColour>>) {
    fun getCellContent(column: Int, row: Int) = cells[column][row]

    fun getSize() = Pair(cells.size, cells[0].size)

    /**
     * Find the current Legal Moves on the current board.
     * There will be 1 move per incomplete column.
     *
     * Each legal move is a Pair<Int,Int>
     *     legalMove.first --> zero-based Column
     *     legalMove.second --> zero-based Row
     */
    fun legalMoves() = cells.mapIndexed { i, column -> Pair(i, column) }
            .filter { !it.second.isFull() }
            .map { Pair(it.first, it.second.getHeight()) }

    /**
     * Returns a copy of the board but with the addition of a new counter in the allocated position
     */
    fun applyMove(move: Triple<Int,Int, co.jfactory.konnect4.model.CellColour>) = applyMove(move.first,move.second, move.third)
    fun applyMove(column: Int, row: Int, colour: co.jfactory.konnect4.model.CellColour) =
            co.jfactory.konnect4.model.Board(cells.mapIndexed { i, list -> if (i == column) list.addCounter(row, colour) else list })
}

fun Board.matchesColour(column: Int, row: Int, colour: co.jfactory.konnect4.model.CellColour): Boolean {
    val size = getSize()
    if (column < 0 || column >= size.first || row < 0 || row >= size.second){
        return false
    }
    return getCellContent(column, row) == colour
}

data class Direction(val dx: Int, val dy: Int) {
    operator fun not() = co.jfactory.konnect4.model.Direction(dx * -1, dy * -1) // Get the opposite direction
    operator fun times(n: Int) = co.jfactory.konnect4.model.Direction(dx * n, dy * n)

    companion object {
        val RIGHT = co.jfactory.konnect4.model.Direction(1, 0)
        val UP = co.jfactory.konnect4.model.Direction(0, 1)
        val DIAG1 = co.jfactory.konnect4.model.Direction(1, 1)
        val DIAG2 = co.jfactory.konnect4.model.Direction(1, -1)
        val DIRECTIONS = listOf(co.jfactory.konnect4.model.Direction.Companion.RIGHT, co.jfactory.konnect4.model.Direction.Companion.UP, co.jfactory.konnect4.model.Direction.Companion.DIAG1, co.jfactory.konnect4.model.Direction.Companion.DIAG2)
    }
}

/**
 * Check if adding a counter of a specified colour to a position would result in a winning position.
 * This is true if there is at least 1 line or 4 or more counters of that colour going through that position.
 */
fun Board.isWinningMove(column: Int, row: Int, colour: co.jfactory.konnect4.model.CellColour): Boolean {
    val longest = (DIRECTIONS).map{ d ->
        listOf((1..3).map{d * it}.takeWhile {matchesColour(column+it.dx, row+it.dy, colour)}.size,
        (1..3).map{!d * it}.takeWhile {matchesColour(column+it.dx, row+it.dy, colour)}.size,
        1).sum()}.max()
    return longest !! >= 4
}