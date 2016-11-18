package co.jfactory.konnect4.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.natpryce.hamkrest.assertion.*
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.should.shouldMatch
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ModelSpec : Spek({
    describe("GameState JSON Parsing") {
        val mapper = jacksonObjectMapper()
        val json = "{\"CurrentState\":3,\"Cells\":[[0,0,0,0,0,0],[2,0,0,0,0,0],[0,0,0,0,0,0],[1,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[2,0,0,0,0,0]],\"YellowPlayerID\":\"a05bf67c-2bbb-4243-bf18-fe60c52cf4f9\", \"RedPlayerID\":\"6351a731-e934-4397-bafa-29d09cfb9dd9\", \"ID\":\"f67c2987-c803-4c79-a065-32de5455bd09\"}"

        it("should create GameState objects from JSON ") {
            val gameState: GameState = mapper.readValue(json, GameState::class.java)
            gameState.id shouldMatch equalTo("f67c2987-c803-4c79-a065-32de5455bd09")
            gameState.currentState shouldMatch equalTo(State.RedToPlay)
            gameState.redPlayerId shouldMatch equalTo("6351a731-e934-4397-bafa-29d09cfb9dd9")
            gameState.yellowPlayerId shouldMatch equalTo("a05bf67c-2bbb-4243-bf18-fe60c52cf4f9")
            gameState.getCellContent(0, 0) shouldMatch equalTo(CellContent.EMPTY)
            gameState.getCellContent(1, 0) shouldMatch equalTo(CellContent.YELLOW)
            gameState.getCellContent(3, 0) shouldMatch equalTo(CellContent.RED)
        }
    }

    describe("Game Started - Status Checking") {
        val redUser = User("RedUserID", "Team Red", "password123")
        val yellowUser = User("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.GameNotStarted, redUser.playerId, yellowUser.playerId)
        it("Should identify that Game has started") {
            assertThat("haveDrawn", gameState.haveDrawn(),equalTo(false))
            assertThat("Red lost", gameState.haveLost(redUser),equalTo(false))
            assertThat("Yellow lost", gameState.haveLost(yellowUser),equalTo(false))
            assertThat("Red won", gameState.haveWon(redUser),equalTo(false))
            assertThat("Yellow won", gameState.haveWon(yellowUser),equalTo(false))
            assertThat("Red's turn", gameState.isMyTurn(redUser),equalTo(false))
            assertThat("Yellow's turn", gameState.isMyTurn(yellowUser),equalTo(false))
        }
    }

    describe("Game Drawn - Status Checking") {
        val redUser = User("RedUserID", "Team Red", "password123")
        val yellowUser = User("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.Draw, redUser.playerId, yellowUser.playerId)
        it("Should identify that Game has drawn") {
            assertThat("haveDrawn", gameState.haveDrawn(),equalTo(true))
            assertThat("Red lost", gameState.haveLost(redUser),equalTo(false))
            assertThat("Yellow lost", gameState.haveLost(yellowUser),equalTo(false))
            assertThat("Red won", gameState.haveWon(redUser),equalTo(false))
            assertThat("Yellow won", gameState.haveWon(yellowUser),equalTo(false))
            assertThat("Red's turn", gameState.isMyTurn(redUser),equalTo(false))
            assertThat("Yellow's turn", gameState.isMyTurn(yellowUser),equalTo(false))
        }
    }

    describe("Red To Play - Status Checking") {
        val redUser = User("RedUserID", "Team Red", "password123")
        val yellowUser = User("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.RedToPlay, redUser.playerId, yellowUser.playerId)
        it("Should identify that Red is next to Play") {
            assertThat("haveDrawn", gameState.haveDrawn(),equalTo(false))
            assertThat("Red lost", gameState.haveLost(redUser),equalTo(false))
            assertThat("Yellow lost", gameState.haveLost(yellowUser),equalTo(false))
            assertThat("Red won", gameState.haveWon(redUser),equalTo(false))
            assertThat("Yellow won", gameState.haveWon(yellowUser),equalTo(false))
            assertThat("Red's turn", gameState.isMyTurn(redUser),equalTo(true))
            assertThat("Yellow's turn", gameState.isMyTurn(yellowUser),equalTo(false))
        }
    }

    describe("Yellow To Play - Status Checking") {
        val redUser = User("RedUserID", "Team Red", "password123")
        val yellowUser = User("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.YellowToPlay, redUser.playerId, yellowUser.playerId)
        it("Should identify that Yellow is next to Play") {
            assertThat("haveDrawn", gameState.haveDrawn(),equalTo(false))
            assertThat("Red lost", gameState.haveLost(redUser),equalTo(false))
            assertThat("Yellow lost", gameState.haveLost(yellowUser),equalTo(false))
            assertThat("Red won", gameState.haveWon(redUser),equalTo(false))
            assertThat("Yellow won", gameState.haveWon(yellowUser),equalTo(false))
            assertThat("Red's turn", gameState.isMyTurn(redUser),equalTo(false))
            assertThat("Yellow's turn", gameState.isMyTurn(yellowUser),equalTo(true))
        }
    }
    describe("Red Win - Status Checking") {
        val redUser = User("RedUserID", "Team Red", "password123")
        val yellowUser = User("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.RedWin, redUser.playerId, yellowUser.playerId)
        it("Should identify that Red has Won") {
            assertThat("haveDrawn", gameState.haveDrawn(),equalTo(false))
            assertThat("Red lost", gameState.haveLost(redUser),equalTo(false))
            assertThat("Yellow lost", gameState.haveLost(yellowUser),equalTo(true))
            assertThat("Red won", gameState.haveWon(redUser),equalTo(true))
            assertThat("Yellow won", gameState.haveWon(yellowUser),equalTo(false))
            assertThat("Red's turn", gameState.isMyTurn(redUser),equalTo(false))
            assertThat("Yellow's turn", gameState.isMyTurn(yellowUser),equalTo(false))
        }
    }

    describe("Yellow Win - Status Checking") {
        val redUser = User("RedUserID", "Team Red", "password123")
        val yellowUser = User("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.YellowWin, redUser.playerId, yellowUser.playerId)
        it("Should identify that Yellow has won") {
            assertThat("haveDrawn", gameState.haveDrawn(),equalTo(false))
            assertThat("Red lost", gameState.haveLost(redUser),equalTo(true))
            assertThat("Yellow lost", gameState.haveLost(yellowUser),equalTo(false))
            assertThat("Red won", gameState.haveWon(redUser),equalTo(false))
            assertThat("Yellow won", gameState.haveWon(yellowUser),equalTo(true))
            assertThat("Red's turn", gameState.isMyTurn(redUser),equalTo(false))
            assertThat("Yellow's turn", gameState.isMyTurn(yellowUser),equalTo(false))
        }
    }
})

private fun createGameState(state: State, yellowUserId: String, redUserId: String): GameState {
    val gameState = GameState("000021212122", state,
            listOf(listOf(CellContent.YELLOW, CellContent.RED, CellContent.EMPTY),
                    listOf(CellContent.EMPTY, CellContent.EMPTY, CellContent.EMPTY),
                    listOf(CellContent.YELLOW, CellContent.RED, CellContent.RED)),
            redUserId, yellowUserId)
    return gameState
}
