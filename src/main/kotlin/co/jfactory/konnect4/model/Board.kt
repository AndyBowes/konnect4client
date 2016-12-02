package co.jfactory.konnect4.model

import co.jfactory.konnect4.model.Direction.Companion.DIRECTIONS

/**
 * Representation of the current state of the board.
 */
data class Board(private val cells: List<List<CellColour>>) {

    companion object {
        val DEFAULT_WIDTH = 7
        val DEFAULT_HEIGHT = 6
        fun emptyBoard(columns:Int = DEFAULT_WIDTH, rows: Int = DEFAULT_HEIGHT) = Board((0..(columns-1)).map { (0..(rows-1)).map { CellColour.EMPTY } })
    }

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
    fun applyMove(move: Triple<Int, Int, CellColour>) = applyMove(move.first, move.second, move.third)

    fun applyMove(pos: Pair<Int,Int>, colour: CellColour) = applyMove(pos.first, pos.second, colour)

    fun applyMove(column: Int, row: Int, colour: CellColour) =
            Board(cells.mapIndexed { i, list -> if (i == column) list.addCounter(row, colour) else list })
}


fun Board.matchesColour(column: Int, row: Int, colour: CellColour) = this.matchesColour(column, row, listOf(colour))
fun Board.matchesColour(column: Int, row: Int, colours: List<CellColour>): Boolean {
    if (!onBoard(column, row)) return false
    return getCellContent(column, row) in colours
}

private fun Board.onBoard(column: Int, row: Int): Boolean {
    val size = getSize()
    return (column >= 0 && column < size.first && row >= 0 && row < size.second)
}

data class Direction(val dx: Int, val dy: Int) {
    operator fun not() = -this  // Not Direction returns the opposite direction
    operator fun unaryMinus() = Direction(dx * -1, dy * -1) // Get the opposite direction
    operator fun times(n: Int) = Direction(dx * n, dy * n)

    companion object {
        val RIGHT = Direction(1, 0)
        val UP = Direction(0, 1)
        val DIAG1 = Direction(1, 1)
        val DIAG2 = Direction(1, -1)
        val DIRECTIONS = listOf(RIGHT, UP, DIAG1, DIAG2)
    }
}

/**
 * Check if adding a counter of a specified colour to a position would result in a winning position.
 * This is true if there is at least 1 line or 4 or more counters of that colour going through that position.
 */
fun Board.isWinningMove(column: Int, row: Int, colour: CellColour): Boolean {
    val longest = (DIRECTIONS).map { d ->
        listOf((1..3).map { d * it }.takeWhile { matchesColour(column + it.dx, row + it.dy, colour) }.size,
                (1..3).map { !d * it }.takeWhile { matchesColour(column + it.dx, row + it.dy, colour) }.size,
                1).sum()
    }.max()
    return longest!! >= 4
}

/**
 * Find all of the available moves which pass through the point.
 *
 * Each 'move' will be represented by a String of X's for counters of the same colour & '_' for empty cells.
 * The 'move' will be terminated when it encounters the edge of the grid of a counter of a different colour
 *
 * There will be 4 moves, i.e. 1 for each direction through the point.
 */
fun Board.getAvailableMoves(column: Int, row: Int, colour: CellColour): List<String> {
    val colours = listOf(colour, CellColour.EMPTY)
    return (DIRECTIONS).map { d ->
        listOf( findAdjacentCells(column, row, !d, colours).reversed(),
                "X",
                findAdjacentCells(column, row, d, colours))
                .joinToString(separator = "")
    }
}

private fun Board.findAdjacentCells(column: Int, row: Int, d: Direction, colours: List<CellColour>): String {
    return (1..3).map { d * it }.map { Pair(column + it.dx, row + it.dy) }
            .takeWhile { onBoard(it.first, it.second) }
            .map { getCellContent(it.first, it.second) }
            .takeWhile { it in colours }
            .map { if (it == CellColour.EMPTY) "_" else "X" }.joinToString(separator = "")
}