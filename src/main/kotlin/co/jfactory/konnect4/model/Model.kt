package co.jfactory.konnect4.model

import com.fasterxml.jackson.annotation.JsonProperty

enum class CellContent(value: Int) {
    EMPTY(0),
    RED(1),
    YELLOW(2)
}

enum class State(value: Int) {
    GameNotStarted(0),
    RedWin(1),
    YellowWin(2),
    RedToPlay(3),
    YellowToPlay(4),
    Draw(5)
}

data class User(val playerId: String, val teamName: String, val password: String)

data class GameState(@JsonProperty("ID") val id: String,
                @JsonProperty("CurrentState") val currentState: State,
                @JsonProperty("Cells") val cells: List<List<CellContent>>,
                @JsonProperty("YellowPlayerID") val yellowPlayerId: String,
                @JsonProperty("RedPlayerID")  val redPlayerId: String)

fun GameState.isRedPlayer(user: User) = redPlayerId.equals(user.playerId)
fun GameState.isYellowPlayer(user: User) = yellowPlayerId.equals(user.playerId)

fun GameState.haveWon(user: User) = (isRedPlayer(user) && currentState.equals(State.RedWin)) || (isYellowPlayer(user) && currentState.equals(State.YellowWin))
fun GameState.haveLost(user: User) = (isRedPlayer(user) && currentState.equals(State.YellowWin)) || (isYellowPlayer(user) && currentState.equals(State.RedWin))
fun GameState.haveDrawn() = currentState.equals(State.Draw)
fun GameState.isMyTurn(user: User) = (isRedPlayer(user) && currentState.equals(State.RedToPlay)) || (isYellowPlayer(user) && currentState.equals(State.YellowToPlay))
fun GameState.getCellContent(column: Int, row: Int) = cells[column][row]