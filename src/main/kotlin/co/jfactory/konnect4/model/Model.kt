package co.jfactory.konnect4.model

import com.fasterxml.jackson.annotation.JsonProperty

enum class CellColour(val value: Int) {
    EMPTY(0),
    RED(1),
    YELLOW(2);

    operator fun not() {
        when (this) {
            RED -> YELLOW
            YELLOW -> RED
            EMPTY -> EMPTY
        }
    }
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
fun  GameState.getPlayerColour(player: Player) = if (isRedPlayer(player)) CellColour.RED else CellColour.YELLOW
fun  GameState.getOpponentColour(player: Player) = !getPlayerColour(player)

fun GameState.hasWon(player: Player) = (isRedPlayer(player) && currentState == State.RedWin) || (isYellowPlayer(player) && currentState == State.YellowWin)
fun GameState.hasLost(player: Player) = (isRedPlayer(player) && currentState == State.YellowWin) || (isYellowPlayer(player) && currentState == State.RedWin)
fun GameState.hasDrawn() = currentState == State.Draw
fun GameState.isMyTurn(player: Player) = (isRedPlayer(player) && currentState == State.RedToPlay) || (isYellowPlayer(player) && currentState == State.YellowToPlay)
fun GameState.getBoard() = Board(cells)
fun GameState.isComplete() = currentState in listOf(State.Draw, State.RedWin, State.YellowWin)

fun List<CellColour>.isFull() = this.last() != CellColour.EMPTY
fun List<CellColour>.getHeight() = this.indexOf(CellColour.EMPTY)
fun List<CellColour>.addCounter(row: Int, newColour: CellColour) = this.mapIndexed { i, cellColour -> if (i == row) newColour else cellColour }
