package co.jfactory.konnect4.client

import co.jfactory.konnect4.client.rest.Konnect4RestConnector
import co.jfactory.konnect4.model.*

class Konnect4Client(userName: String, password: String, rootUrl: String = "http://yorkdojoconnect4.azurewebsites.net", val pollPause: Long = 2000) {
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
                val selectedColumn = selectMove(gameState)
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
        val board = gameState.getBoard()
        board.print()
    }

    private fun startGame(player: Player) {
        val initialState = connector.getGameState(player)
        if (initialState.isComplete()) {
            connector.newGame(player)
        }
    }

    /**
     * Select the best move to make in this turn
     * Returns the number of the selected column.
     */
    fun selectMove(gameState: GameState) : Int {
        val board = gameState.getBoard()
        val legalMoves = board.legalMoves()

        // TODO - Your code goes here.  This example just gets the column of the 1st Legal Move
        val selectedColumn = legalMoves.first().first

        return selectedColumn
    }

}