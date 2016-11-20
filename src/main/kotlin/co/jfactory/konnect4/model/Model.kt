package co.jfactory.konnect4.model

import com.fasterxml.jackson.annotation.JsonProperty

enum class CellColour(val value: Int) {
    EMPTY(0),
    RED(1),
    YELLOW(2)
}

enum class State(val value: Int) {
    GameNotStarted(0),
    RedWin(1),
    YellowWin(2),
    RedToPlay(3),
    YellowToPlay(4),
    Draw(5)
}

data class Player(val playerId: String, val teamName: String, val password: String)

data class GameState(@JsonProperty("ID") val id: String,
                     @JsonProperty("CurrentState") val currentState: State,
                     @JsonProperty("Cells") val cells: List<List<CellColour>>,
                     @JsonProperty("YellowPlayerID") val yellowPlayerId: String,
                     @JsonProperty("RedPlayerID") val redPlayerId: String)

fun GameState.isRedPlayer(player: Player) = redPlayerId == player.playerId
fun GameState.isYellowPlayer(player: Player) = yellowPlayerId == player.playerId

fun GameState.hasWon(player: Player) = (isRedPlayer(player) && currentState == State.RedWin) || (isYellowPlayer(player) && currentState == State.YellowWin)
fun GameState.hasLost(player: Player) = (isRedPlayer(player) && currentState == State.YellowWin) || (isYellowPlayer(player) && currentState == State.RedWin)
fun GameState.hasDrawn() = currentState.equals(State.Draw)
fun GameState.isMyTurn(player: Player) = (isRedPlayer(player) && currentState == State.RedToPlay) || (isYellowPlayer(player) && currentState == State.YellowToPlay)
fun GameState.getBoard() = Board(cells)
fun GameState.isComplete() = currentState in listOf(State.Draw,State.RedWin, State.YellowWin)

fun List<CellColour>.isFull() = this.last() != CellColour.EMPTY
fun List<CellColour>.getHeight() = this.indexOf(CellColour.EMPTY)
fun List<CellColour>.addCounter(row: Int, newColour: CellColour) = this.mapIndexed { i, cellColour -> if (i == row) newColour else cellColour }

/**
 * Representation of the current state of the board.
 */
data class Board(private val cells: List<List<CellColour>>) {
    fun getCellContent(column: Int, row: Int) = cells[column][row]

    fun getSize() = Pair(cells.size,cells[0].size)

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
    fun applyMove(column: Int, row: Int, colour: CellColour) =
            Board(cells.mapIndexed { i, list -> if (i == column) list.addCounter(row, colour) else list })
}

fun Board.print() = {
    val size = getSize()
    val m = mapOf(CellColour.EMPTY to "-", CellColour.RED to "R", CellColour.YELLOW to "Y")
    ((size.second-1)..0)
            .map { r ->
                (0..(size.first-1))
                        .map{this.getCellContent(it,r)}
                        .map { m[it] }.joinToString { "   " }
            }
            .forEach { println(it) }
}