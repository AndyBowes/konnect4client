package co.jfactory.konnect4.model

import co.jfactory.konnect4.client.rest.GamesStateDeserializer
import io.kotlintest.specs.FeatureSpec

class ModelSpec : FeatureSpec() {
    init {

        feature("Handle Cell Colours") {

            scenario("calculates opposite of Red is Yellow") {
                !CellColour.RED shouldEqual CellColour.YELLOW
                -CellColour.RED shouldEqual CellColour.YELLOW
            }

            scenario("calculates opposite of Yellow is Red") {
                !CellColour.YELLOW shouldEqual CellColour.RED
                -CellColour.YELLOW shouldEqual CellColour.RED
            }

            scenario("calculates opposite of Empty is Empty") {
                !CellColour.EMPTY shouldEqual CellColour.EMPTY
                -CellColour.EMPTY shouldEqual CellColour.EMPTY
            }
        }

        feature("Check Legal Moves") {

            scenario("A Partially filled Board") {
                val board = Board(listOf(listOf(CellColour.RED, CellColour.EMPTY, CellColour.EMPTY, CellColour.EMPTY),
                        listOf(CellColour.RED, CellColour.YELLOW, CellColour.EMPTY, CellColour.EMPTY),
                        listOf(CellColour.EMPTY, CellColour.EMPTY, CellColour.EMPTY, CellColour.EMPTY),
                        listOf(CellColour.RED, CellColour.YELLOW, CellColour.RED, CellColour.YELLOW),
                        listOf(CellColour.EMPTY, CellColour.EMPTY, CellColour.EMPTY, CellColour.EMPTY),
                        listOf(CellColour.YELLOW, CellColour.EMPTY, CellColour.EMPTY, CellColour.EMPTY)))
                val legalMoves = board.legalMoves()

                //it("finds all the Legal Moves") {
                legalMoves.size shouldEqual 5
                legalMoves should contain(Pair(0, 1))
                legalMoves should contain(Pair(1, 2))
                legalMoves should contain(Pair(2, 0))
                legalMoves should contain(Pair(4, 0))
                legalMoves should contain(Pair(5, 1))

                //it("does not return a move for a full column") {
                legalMoves.filter { it.first == 3 }.size shouldEqual 0
            }

            scenario("An empty board") {
                val board = Board((0..6).map { (0..7).map { CellColour.EMPTY } })
                val legalMoves = board.legalMoves()
                //it("identifies the first row in each column as a legal move") {
                legalMoves.size shouldEqual 7
                (0..6).forEach {
                    legalMoves should contain(Pair(it, 0))
                }
            }

            scenario("A full board") {
                val board = Board((0..6).map { (0..7).map { CellColour.RED } })
                val legalMoves = board.legalMoves()
                //it("finds no legal moves") {
                legalMoves.size shouldEqual (0)
            }
        }

        feature("GameState JSON Parsing") {
            val json = "{\"CurrentState\":3,\"Cells\":[[0,0,0,0,0,0],[2,0,0,0,0,0],[1,2,2,1,2,2],[1,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[2,0,0,0,0,0]],\"YellowPlayerID\":\"a05bf67c-2bbb-4243-bf18-fe60c52cf4f9\", \"RedPlayerID\":\"6351a731-e934-4397-bafa-29d09cfb9dd9\", \"ID\":\"f67c2987-c803-4c79-a065-32de5455bd09\"}"

            scenario("should create GameState objects from JSON ") {
                val gameState: GameState = GamesStateDeserializer().deserialize(json)!!
                gameState.id shouldEqual "f67c2987-c803-4c79-a065-32de5455bd09"
                gameState.currentState shouldEqual State.RedToPlay
                gameState.redPlayerId shouldEqual "6351a731-e934-4397-bafa-29d09cfb9dd9"
                gameState.yellowPlayerId shouldEqual "a05bf67c-2bbb-4243-bf18-fe60c52cf4f9"

                val board = gameState.getBoard()
                board.getCellContent(0, 0) shouldEqual CellColour.EMPTY
                board.getCellContent(1, 0) shouldEqual CellColour.YELLOW
                board.getCellContent(3, 0) shouldEqual CellColour.RED

                val legalMoves = board.legalMoves()
                legalMoves.size shouldEqual 6
                legalMoves should contain(Pair(0, 0))
                legalMoves should contain(Pair(0, 0))
                legalMoves should contain(Pair(0, 0))
                legalMoves should contain(Pair(0, 0))
                legalMoves should contain(Pair(0, 0))
                legalMoves should contain(Pair(0, 0))
            }
        }

        feature("Game Started - Status Checking") {
            val redUser = Player("RedUserID", "Team Red", "password123")
            val yellowUser = Player("YellowUserID", "Team Yellow", "password999")
            val gameState = createGameState(State.GameNotStarted, redUser.playerId, yellowUser.playerId)
            scenario("Should identify that Game has started") {
                gameState.isComplete() shouldBe false
                gameState.hasDrawn() shouldBe false
                gameState.hasLost(redUser) shouldBe false
                gameState.hasLost(yellowUser) shouldBe false
                gameState.hasWon(redUser) shouldBe false
                gameState.hasWon(yellowUser) shouldBe false
                gameState.isMyTurn(redUser) shouldBe false
                gameState.isMyTurn(yellowUser) shouldBe false
            }
        }

        feature("Game Drawn - Status Checking") {
            val redUser = Player("RedUserID", "Team Red", "password123")
            val yellowUser = Player("YellowUserID", "Team Yellow", "password999")
            val gameState = createGameState(State.Draw, redUser.playerId, yellowUser.playerId)
            scenario("Should identify that Game has drawn") {
                gameState.isComplete() shouldBe true
                gameState.hasDrawn() shouldBe true
                gameState.hasLost(redUser) shouldBe false
                gameState.hasLost(yellowUser) shouldBe false
                gameState.hasWon(redUser) shouldBe false
                gameState.hasWon(yellowUser) shouldBe false
                gameState.isMyTurn(redUser) shouldBe false
                gameState.isMyTurn(yellowUser) shouldBe false
            }
        }

        feature("Red To Play - Status Checking") {
            val redUser = Player("RedUserID", "Team Red", "password123")
            val yellowUser = Player("YellowUserID", "Team Yellow", "password999")
            val gameState = createGameState(State.RedToPlay, redUser.playerId, yellowUser.playerId)
            scenario("Should identify that Red is next to Play") {
                gameState.isComplete() shouldBe false
                gameState.hasDrawn() shouldBe false
                gameState.hasLost(redUser) shouldBe false
                gameState.hasLost(yellowUser) shouldBe false
                gameState.hasWon(redUser) shouldBe false
                gameState.hasWon(yellowUser) shouldBe false
                gameState.isMyTurn(redUser) shouldBe true
                gameState.isMyTurn(yellowUser) shouldBe false
            }
        }

        feature("Yellow To Play - Status Checking") {
            val redUser = Player("RedUserID", "Team Red", "password123")
            val yellowUser = Player("YellowUserID", "Team Yellow", "password999")
            val gameState = createGameState(State.YellowToPlay, redUser.playerId, yellowUser.playerId)
            scenario("Should identify that Yellow is next to Play") {
                gameState.isComplete() shouldBe false
                gameState.hasDrawn() shouldBe false
                gameState.hasLost(redUser) shouldBe false
                gameState.hasLost(yellowUser) shouldBe false
                gameState.hasWon(redUser) shouldBe false
                gameState.hasWon(yellowUser) shouldBe false
                gameState.isMyTurn(redUser) shouldBe false
                gameState.isMyTurn(yellowUser) shouldBe true
            }
        }
        feature("Red Win - Status Checking") {
            val redUser = Player("RedUserID", "Team Red", "password123")
            val yellowUser = Player("YellowUserID", "Team Yellow", "password999")
            val gameState = createGameState(State.RedWin, redUser.playerId, yellowUser.playerId)
            scenario("Should identify that Red has Won") {
                gameState.isComplete() shouldBe true
                gameState.hasDrawn() shouldBe false
                gameState.hasLost(redUser) shouldBe false
                gameState.hasLost(yellowUser) shouldBe true
                gameState.hasWon(redUser) shouldBe true
                gameState.hasWon(yellowUser) shouldBe false
                gameState.isMyTurn(redUser) shouldBe false
                gameState.isMyTurn(yellowUser) shouldBe false
            }
        }

        feature("Yellow Win - Status Checking") {
            val redUser = Player("RedUserID", "Team Red", "password123")
            val yellowUser = Player("YellowUserID", "Team Yellow", "password999")
            val gameState = createGameState(State.YellowWin, redUser.playerId, yellowUser.playerId)
            scenario("Should identify that Yellow has won") {
                gameState.isComplete() shouldBe true
                gameState.hasDrawn() shouldBe false
                gameState.hasLost(redUser) shouldBe true
                gameState.hasLost(yellowUser) shouldBe false
                gameState.hasWon(redUser) shouldBe false
                gameState.hasWon(yellowUser) shouldBe true
                gameState.isMyTurn(redUser) shouldBe false
                gameState.isMyTurn(yellowUser) shouldBe false
            }
        }
    }
}

private fun createGameState(state: State, yellowUserId: String, redUserId: String): GameState {
    val gameState = GameState("000021212122", state,
            listOf(listOf(CellColour.YELLOW, CellColour.RED, CellColour.EMPTY),
                    listOf(CellColour.EMPTY, CellColour.EMPTY, CellColour.EMPTY),
                    listOf(CellColour.YELLOW, CellColour.RED, CellColour.RED)),
            redUserId, yellowUserId)
    return gameState
}
