package co.jfactory.konnect4.client

import co.jfactory.konnect4.client.rest.Konnect4RestConnector
import co.jfactory.konnect4.model.*

// TODO - Implement a method with this signature which can be passed to the Konnect4 Client.
/**
 * Simple Move selector which just picks the 1st Legal Move
 */
fun simpleMoveSelector(player: Player, gameState: GameState) : Int {
    val board = gameState.getBoard()
    val myColour = gameState.getPlayerColour(player)
    val opponentColour = gameState.getOpponentColour(player)
    val legalMoves = board.legalMoves()
    val selectedMove = legalMoves.first()
    val selectedColumn = selectedMove.first
    //val selectedRow = selectedMove.second
    return selectedColumn
}


/**
 * Client that manages a game.
 *
 * In order to play a game you will need to create an instance of this class passing in the
 * user name and password or your player and then start the game.
 *
 * client = Konnect4Client("<<myUserName>>","<<password>>")
 * client.playGame()
 *
 *
 * In order to win you will need to create your own function which selects the
 *
 */
class Konnect4Client(userName: String,
                     password: String,
                     rootUrl: String = "http://yorkdojoconnect4.azurewebsites.net",
                     val pollPause: Long = 500,
                     val moveStrategy: (Player, GameState) -> Int = ::simpleMoveSelector,
                     val showFinalBoard: Boolean = true) {
    val connector: Konnect4RestConnector
    val player: Player
    init {
        connector = Konnect4RestConnector(rootUrl)
        player = connector.registerPlayer(userName, password)
    }

    /**
     * Play a game of Connect4
     */
    fun playGame(){

        startGame(player)

        while (true){
            val gameState = connector.getGameState(player)
            if (gameState.isComplete()){
                showFinalBoard(player, gameState)
                break
            }

            if (gameState.isMyTurn(player)){
                val selectedColumn = moveStrategy(player, gameState)
                connector.makeMove(player, selectedColumn)
            } else {
                // Wait for opponent to make a move
                Thread.sleep(pollPause)
            }
        }
    }

    private fun  showFinalBoard(player: Player, gameState: GameState) {
        if (gameState.hasWon(player)) {
            System.out.println("You're a winner")
        } else if (gameState.hasLost(player)) {
            System.out.println("Loser")
        } else {
            System.out.println("Its a draw")
        }
        if (showFinalBoard) {
            val board = gameState.getBoard()
            println("Final Board Positions")
            board.print()
        }
    }

    private fun startGame(player: Player) {
        val initialState = connector.getGameState(player)
        if (initialState.isComplete()) {
            connector.newGame(player)
        }
    }
}

fun Board.print() {
    val size = getSize()
    val m = mapOf(CellColour.EMPTY to ".", CellColour.RED to "X", CellColour.YELLOW to "0")
    ((size.second - 1) downTo 0) // Iterate through rows in reverse order
            .map { r ->
                (0..(size.first - 1))  // Iterate through the columns
                        .map { m[this.getCellContent(it, r)] } // Convert the Colour in the Cell to a Symbol
                        .joinToString(separator = " ")
            }
            .forEach(::println)
}