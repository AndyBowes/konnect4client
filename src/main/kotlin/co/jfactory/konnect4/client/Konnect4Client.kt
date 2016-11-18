package co.jfactory.konnect4.client

import co.jfactory.konnect4.client.rest.Konnect4RestConnector
import co.jfactory.konnect4.model.GameState
import co.jfactory.konnect4.model.User

class Konnect4Client (val connector: Konnect4RestConnector) {

    fun registerUser(name: String, password: String) : User? {
        return connector.registerUser(name,password)
    }

    fun newGame(user: User): String? {
        return connector.newGame(user)
    }

    fun getGameState(user: User, gameId: String) : GameState? {
        return connector.getGameState(user)
    }

    fun makeMove(user: User, column: Int) {
        connector.makeMove(user, column)
    }
}