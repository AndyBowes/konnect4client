package co.jfactory.konnect4.client.rest

import co.jfactory.konnect4.model.GameState
import co.jfactory.konnect4.model.Player
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import java.net.URLEncoder

/**
 * Convert JSON to an instance of @link(GameState)
 */
class GamesStateDeserializer : ResponseDeserializable<GameState> {
    val mapper = jacksonObjectMapper()
    override fun deserialize(json: String): GameState? = mapper.readValue(json, GameState::class.java)
}


/**
 * Class which provides REST Interface to the Connect 4 Game Engine
 *
 */
@Suppress("UNUSED_VARIABLE")
class Konnect4RestConnector(val rootUrl: String) {

    private fun encode(paramValue:String) = URLEncoder.encode(paramValue, "UTF-8")

    private fun queryParams(params: List<Pair<String,String>>) = params.map { "${it.first}=${encode(it.second)}"}.joinToString("&", prefix="?")

    fun registerPlayer(playerName: String, password: String): Player {
        val params = listOf("teamName" to playerName, "password" to password)
        val url = "$rootUrl/api/Register" + queryParams(params)
        val (request, response, result) = url.httpPost().body("Test")
                .header("Accept" to "application/json")
                .responseString()
        val playerId = result.get().replace("\"", "")
        return Player(playerId, playerName, password)
    }

    fun newGame(player: Player): String {
        val params = listOf("playerID" to player.playerId)
        val url = "$rootUrl/api/NewGame" + queryParams(params)
        val (request, response, result) = url.httpPost().body("Test")
                .header("Accept" to "application/json")
                .responseString()
        return result.get()
    }

    fun getGameState(player: Player): GameState {
        val params = listOf("playerID" to player.playerId)
        val url = "$rootUrl/api/GameState" + queryParams(params)
        val (request, response, result) = url.httpGet()
                .header("Accept" to "application/json")
                .responseObject(GamesStateDeserializer())
        return result.get()
    }

    fun makeMove(player: Player, columnNo: Int): Boolean {
        val params = listOf("playerID" to player.playerId, "password" to player.password, "columnNumber" to columnNo.toString())
        val url = "$rootUrl/api/MakeMove" + queryParams(params)
        val (request, response, result) = url.httpPost().body("Test")
                .header("Accept" to "application/json")
                .responseString()
        return true
    }
}