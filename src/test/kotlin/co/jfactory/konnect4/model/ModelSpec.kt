package co.jfactory.konnect4.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ModelSpec : Spek({
    describe("GameState JSON Parsing") {
        val mapper = jacksonObjectMapper()
        val json = ""

        it("should create GameState objects from JSON ") {
            val gameState: GameState = mapper.readValue(json, GameState::class.java)
            assert(gameState.id.equals("f67c2987-c803-4c79-a065-32de5455bd09"))
            assert(gameState.currentState.equals(State.RedToPlay))
            assert(gameState.redPlayerId.equals("6351a731-e934-4397-bafa-29d09cfb9dd9"))
            assert(gameState.yellowPlayerId.equals("a05bf67c-2bbb-4243-bf18-fe60c52cf4f9"))
            assert(gameState.getCellContent(0, 0).equals(CellContent.EMPTY))
        }
    }

    describe("Game Started - Status Checking") {
        val redUser = User("RedUserID", "Team Red", "password123")
        val yellowUser = User("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.GameNotStarted, redUser.playerId, yellowUser.playerId)
        assert(!gameState.haveDrawn())
        assert(!gameState.haveLost(redUser))
        assert(!gameState.haveLost(yellowUser))
        assert(!gameState.haveWon(redUser))
        assert(!gameState.haveWon(yellowUser))
        assert(!gameState.isMyTurn(redUser))
        assert(!gameState.isMyTurn(yellowUser))
    }

    describe("Game Drawn - Status Checking") {
        val redUser = User("RedUserID", "Team Red", "password123")
        val yellowUser = User("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.Draw, redUser.playerId, yellowUser.playerId)
        assert(gameState.haveDrawn())
        assert(!gameState.haveLost(redUser))
        assert(!gameState.haveLost(yellowUser))
        assert(!gameState.haveWon(redUser))
        assert(!gameState.haveWon(yellowUser))
        assert(!gameState.isMyTurn(redUser))
        assert(!gameState.isMyTurn(yellowUser))
    }

    describe("Red To Play - Status Checking") {
        val redUser = User("RedUserID", "Team Red", "password123")
        val yellowUser = User("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.RedToPlay, redUser.playerId, yellowUser.playerId)
        assert(!gameState.haveDrawn())
        assert(!gameState.haveLost(redUser))
        assert(!gameState.haveLost(yellowUser))
        assert(!gameState.haveWon(redUser))
        assert(!gameState.haveWon(yellowUser))
        assert(gameState.isMyTurn(redUser))
        assert(!gameState.isMyTurn(yellowUser))
    }

    describe("Yellow To Play - Status Checking") {
        val redUser = User("RedUserID", "Team Red", "password123")
        val yellowUser = User("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.YellowToPlay, redUser.playerId, yellowUser.playerId)
        assert(!gameState.haveDrawn())
        assert(!gameState.haveLost(redUser))
        assert(!gameState.haveLost(yellowUser))
        assert(!gameState.haveWon(redUser))
        assert(!gameState.haveWon(yellowUser))
        assert(!gameState.isMyTurn(redUser))
        assert(gameState.isMyTurn(yellowUser))
    }
    describe("Red Win - Status Checking") {
        val redUser = User("RedUserID", "Team Red", "password123")
        val yellowUser = User("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.RedWin, redUser.playerId, yellowUser.playerId)
        assert(!gameState.haveDrawn())
        assert(!gameState.haveLost(redUser))
        assert(gameState.haveLost(yellowUser))
        assert(gameState.haveWon(redUser))
        assert(!gameState.haveWon(yellowUser))
        assert(!gameState.isMyTurn(redUser))
        assert(!gameState.isMyTurn(yellowUser))
    }

    describe("Yellow Win - Status Checking") {
        val redUser = User("RedUserID", "Team Red", "password123")
        val yellowUser = User("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.YellowWin, redUser.playerId, yellowUser.playerId)
        assert(!gameState.haveDrawn())
        assert(gameState.haveLost(redUser))
        assert(!gameState.haveLost(yellowUser))
        assert(!gameState.haveWon(redUser))
        assert(gameState.haveWon(yellowUser))
        assert(!gameState.isMyTurn(redUser))
        assert(!gameState.isMyTurn(yellowUser))
    }

})

private fun createGameState(state: State, redUserId: String, yellowUserId: String): GameState {
    val gameState = GameState("000021212122", state,
            listOf(listOf(CellContent.YELLOW, CellContent.RED, CellContent.EMPTY),
                    listOf(CellContent.EMPTY, CellContent.EMPTY, CellContent.EMPTY),
                    listOf(CellContent.YELLOW, CellContent.RED, CellContent.RED)),
            redUserId, yellowUserId)
    return gameState
}
