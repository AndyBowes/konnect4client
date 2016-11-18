package co.jfactory.konnect4.client.rest

import co.jfactory.konnect4.model.GameState
import co.jfactory.konnect4.model.User
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result

/**
 * Class which provides REST Interface to the Connect 4 Game Engine
 *
 */
class Konnect4RestConnector(rootUrl: String) {
    init {
        FuelManager.instance.basePath = rootUrl
    }

    fun registerUser(username: String, password: String): User? {
        val params= listOf("teamname" to username, "password" to password)
        var user: User? = null
        "/api/Register".httpPost(params).responseString { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    // error = result.error
                }
                is Result.Success -> {
                    val teamId = result.get()
                    user = User(teamId, username, password)
                }
            }
        }
        return user
    }

    fun newGame(user: User): String?{
        val params= listOf("playerID" to user.playerId)
        var gameId: String? = null
        "/api/NewGame".httpPost(params).responseString { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    // error = result.error
                }
                is Result.Success -> {
                    gameId = result.get()
                }
            }
        }
        return gameId
    }

    fun getGameState(user: User): GameState? {
        val mapper = jacksonObjectMapper()
        val params= listOf("playerID" to user.playerId)
        var gameState: GameState? = null
        "/api/GameState".httpGet(params).responseString { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    //error = result.error
                }
                is Result.Success -> {
                    val json = result.value
                    gameState = mapper.readValue<GameState>(json, GameState::class.java)
                }
            }
        }
        return gameState
    }

    fun makeMove(user: User, columnNo: Int): Boolean{
        val params = listOf("playerID" to user.playerId,
                "password" to user.password,
                "columnNumber" to columnNo)
        var success = false
        "/api/MakeMove".httpPost(params).responseString { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    success = false
                }
                is Result.Success -> {
                    success = true
                }
            }
        }
        return success
    }
}