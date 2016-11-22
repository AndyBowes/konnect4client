package co.jfactory.konnect4.model

import co.jfactory.konnect4.client.rest.GamesStateDeserializer
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.should.shouldMatch
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class ModelSpec : Spek({

    describe("Check Legal Moves") {

        given("A Partially filled Board") {
            val board = Board(listOf(listOf(CellColour.RED, CellColour.EMPTY, CellColour.EMPTY, CellColour.EMPTY),
                    listOf(CellColour.RED, CellColour.YELLOW, CellColour.EMPTY, CellColour.EMPTY),
                    listOf(CellColour.EMPTY, CellColour.EMPTY, CellColour.EMPTY, CellColour.EMPTY),
                    listOf(CellColour.RED, CellColour.YELLOW, CellColour.RED, CellColour.YELLOW),
                    listOf(CellColour.EMPTY, CellColour.EMPTY, CellColour.EMPTY, CellColour.EMPTY),
                    listOf(CellColour.YELLOW, CellColour.EMPTY, CellColour.EMPTY, CellColour.EMPTY)))
            val legalMoves = board.legalMoves()

            it("finds all the Legal Moves") {
                legalMoves.size shouldMatch equalTo(5)
                legalMoves.contains(Pair(0,1)) shouldMatch equalTo(true)
                legalMoves.contains(Pair(1,2)) shouldMatch equalTo(true)
                legalMoves.contains(Pair(2,0)) shouldMatch equalTo(true)
                legalMoves.contains(Pair(4,0)) shouldMatch equalTo(true)
                legalMoves.contains(Pair(5,1)) shouldMatch equalTo(true)
            }

            it("does not return a move for a full column") {
                legalMoves.filter { it.first == 3 }.size shouldMatch equalTo(0)
            }
        }

        given("An empty board") {
            val board = Board((0..6).map { (0..7).map { CellColour.EMPTY } })
            val legalMoves = board.legalMoves()
            it("identifies the first row in each column as a legal move") {
                legalMoves.size shouldMatch equalTo(7)
                (0..6).forEach {
                    legalMoves.contains(Pair(it,0))
                }
            }
        }

        given("A full board") {
            val board = Board((0..6).map { (0..7).map { CellColour.RED } })
            val legalMoves = board.legalMoves()
            it("finds no legal moves") {
                legalMoves.size shouldMatch equalTo(0)
            }
        }
    }

    describe("GameState JSON Parsing") {
        val json = "{\"CurrentState\":3,\"Cells\":[[0,0,0,0,0,0],[2,0,0,0,0,0],[1,2,2,1,2,2],[1,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[2,0,0,0,0,0]],\"YellowPlayerID\":\"a05bf67c-2bbb-4243-bf18-fe60c52cf4f9\", \"RedPlayerID\":\"6351a731-e934-4397-bafa-29d09cfb9dd9\", \"ID\":\"f67c2987-c803-4c79-a065-32de5455bd09\"}"

        it("should create GameState objects from JSON ") {
            val gameState: GameState = GamesStateDeserializer().deserialize(json)!!
            gameState.id shouldMatch equalTo("f67c2987-c803-4c79-a065-32de5455bd09")
            gameState.currentState shouldMatch equalTo(State.RedToPlay)
            gameState.redPlayerId shouldMatch equalTo("6351a731-e934-4397-bafa-29d09cfb9dd9")
            gameState.yellowPlayerId shouldMatch equalTo("a05bf67c-2bbb-4243-bf18-fe60c52cf4f9")

            val board = gameState.getBoard()
            board.getCellContent(0, 0) shouldMatch equalTo(CellColour.EMPTY)
            board.getCellContent(1, 0) shouldMatch equalTo(CellColour.YELLOW)
            board.getCellContent(3, 0) shouldMatch equalTo(CellColour.RED)

            val legalMoves = board.legalMoves()
            legalMoves.size shouldMatch equalTo(6)
            assertThat("Column 0", legalMoves.contains(Pair(0, 0)), equalTo(true))
            assertThat("Column 1", legalMoves.contains(Pair(0, 0)), equalTo(true))
            assertThat("Column 2", legalMoves.contains(Pair(0, 0)), equalTo(true))
            assertThat("Column 4", legalMoves.contains(Pair(0, 0)), equalTo(true))
            assertThat("Column 5", legalMoves.contains(Pair(0, 0)), equalTo(true))
            assertThat("Column 6", legalMoves.contains(Pair(0, 0)), equalTo(true))
        }
    }

    describe("Game Started - Status Checking") {
        val redUser = Player("RedUserID", "Team Red", "password123")
        val yellowUser = Player("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.GameNotStarted, redUser.playerId, yellowUser.playerId)
        it("Should identify that Game has started") {
            assertThat("isComplete", gameState.isComplete(), equalTo(false))
            assertThat("hasDrawn", gameState.hasDrawn(), equalTo(false))
            assertThat("Red lost", gameState.hasLost(redUser), equalTo(false))
            assertThat("Yellow lost", gameState.hasLost(yellowUser), equalTo(false))
            assertThat("Red won", gameState.hasWon(redUser), equalTo(false))
            assertThat("Yellow won", gameState.hasWon(yellowUser), equalTo(false))
            assertThat("Red's turn", gameState.isMyTurn(redUser), equalTo(false))
            assertThat("Yellow's turn", gameState.isMyTurn(yellowUser), equalTo(false))
        }
    }

    describe("Game Drawn - Status Checking") {
        val redUser = Player("RedUserID", "Team Red", "password123")
        val yellowUser = Player("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.Draw, redUser.playerId, yellowUser.playerId)
        it("Should identify that Game has drawn") {
            assertThat("isComplete", gameState.isComplete(), equalTo(true))
            assertThat("hasDrawn", gameState.hasDrawn(), equalTo(true))
            assertThat("Red lost", gameState.hasLost(redUser), equalTo(false))
            assertThat("Yellow lost", gameState.hasLost(yellowUser), equalTo(false))
            assertThat("Red won", gameState.hasWon(redUser), equalTo(false))
            assertThat("Yellow won", gameState.hasWon(yellowUser), equalTo(false))
            assertThat("Red's turn", gameState.isMyTurn(redUser), equalTo(false))
            assertThat("Yellow's turn", gameState.isMyTurn(yellowUser), equalTo(false))
        }
    }

    describe("Red To Play - Status Checking") {
        val redUser = Player("RedUserID", "Team Red", "password123")
        val yellowUser = Player("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.RedToPlay, redUser.playerId, yellowUser.playerId)
        it("Should identify that Red is next to Play") {
            assertThat("isComplete", gameState.isComplete(), equalTo(false))
            assertThat("hasDrawn", gameState.hasDrawn(), equalTo(false))
            assertThat("Red lost", gameState.hasLost(redUser), equalTo(false))
            assertThat("Yellow lost", gameState.hasLost(yellowUser), equalTo(false))
            assertThat("Red won", gameState.hasWon(redUser), equalTo(false))
            assertThat("Yellow won", gameState.hasWon(yellowUser), equalTo(false))
            assertThat("Red's turn", gameState.isMyTurn(redUser), equalTo(true))
            assertThat("Yellow's turn", gameState.isMyTurn(yellowUser), equalTo(false))
        }
    }

    describe("Yellow To Play - Status Checking") {
        val redUser = Player("RedUserID", "Team Red", "password123")
        val yellowUser = Player("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.YellowToPlay, redUser.playerId, yellowUser.playerId)
        it("Should identify that Yellow is next to Play") {
            assertThat("isComplete", gameState.isComplete(), equalTo(false))
            assertThat("hasDrawn", gameState.hasDrawn(), equalTo(false))
            assertThat("Red lost", gameState.hasLost(redUser), equalTo(false))
            assertThat("Yellow lost", gameState.hasLost(yellowUser), equalTo(false))
            assertThat("Red won", gameState.hasWon(redUser), equalTo(false))
            assertThat("Yellow won", gameState.hasWon(yellowUser), equalTo(false))
            assertThat("Red's turn", gameState.isMyTurn(redUser), equalTo(false))
            assertThat("Yellow's turn", gameState.isMyTurn(yellowUser), equalTo(true))
        }
    }
    describe("Red Win - Status Checking") {
        val redUser = Player("RedUserID", "Team Red", "password123")
        val yellowUser = Player("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.RedWin, redUser.playerId, yellowUser.playerId)
        it("Should identify that Red has Won") {
            assertThat("isComplete", gameState.isComplete(), equalTo(true))
            assertThat("hasDrawn", gameState.hasDrawn(), equalTo(false))
            assertThat("Red lost", gameState.hasLost(redUser), equalTo(false))
            assertThat("Yellow lost", gameState.hasLost(yellowUser), equalTo(true))
            assertThat("Red won", gameState.hasWon(redUser), equalTo(true))
            assertThat("Yellow won", gameState.hasWon(yellowUser), equalTo(false))
            assertThat("Red's turn", gameState.isMyTurn(redUser), equalTo(false))
            assertThat("Yellow's turn", gameState.isMyTurn(yellowUser), equalTo(false))
        }
    }

    describe("Yellow Win - Status Checking") {
        val redUser = Player("RedUserID", "Team Red", "password123")
        val yellowUser = Player("YellowUserID", "Team Yellow", "password999")
        val gameState = createGameState(State.YellowWin, redUser.playerId, yellowUser.playerId)
        it("Should identify that Yellow has won") {
            assertThat("isComplete", gameState.isComplete(), equalTo(true))
            assertThat("hasDrawn", gameState.hasDrawn(), equalTo(false))
            assertThat("Red lost", gameState.hasLost(redUser), equalTo(true))
            assertThat("Yellow lost", gameState.hasLost(yellowUser), equalTo(false))
            assertThat("Red won", gameState.hasWon(redUser), equalTo(false))
            assertThat("Yellow won", gameState.hasWon(yellowUser), equalTo(true))
            assertThat("Red's turn", gameState.isMyTurn(redUser), equalTo(false))
            assertThat("Yellow's turn", gameState.isMyTurn(yellowUser), equalTo(false))
        }
    }
})

private fun createGameState(state: State, yellowUserId: String, redUserId: String): GameState {
    val gameState = GameState("000021212122", state,
            listOf(listOf(CellColour.YELLOW, CellColour.RED, CellColour.EMPTY),
                    listOf(CellColour.EMPTY, CellColour.EMPTY, CellColour.EMPTY),
                    listOf(CellColour.YELLOW, CellColour.RED, CellColour.RED)),
            redUserId, yellowUserId)
    return gameState
}
