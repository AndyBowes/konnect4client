package co.jfactory.konnect4.scoring

import co.jfactory.konnect4.model.Board
import co.jfactory.konnect4.model.CellColour
import co.jfactory.konnect4.model.isWinningMove
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.should.shouldMatch
import javafx.scene.control.Cell
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class BoardScorerSpec : Spek({

    describe("Check that Scorer works appropriately") {
        it("calculates the appropriate score for a string containing XXXX") {
            BoardScorer.scoreMove("XXXX") shouldMatch equalTo(10000)
            BoardScorer.scoreMove("XXXXX") shouldMatch equalTo(10000)
            BoardScorer.scoreMove("__XXXX") shouldMatch equalTo(10000)
            BoardScorer.scoreMove("__XXXX_X") shouldMatch equalTo(10000)
        }
        it("calculates the appropriate score for a string containing XXX") {
            BoardScorer.scoreMove("XXX") shouldMatch equalTo(1) // Only 3 chars therefore can't extend
            BoardScorer.scoreMove("X_XXX__") shouldMatch equalTo(1024)
            BoardScorer.scoreMove("_XXX") shouldMatch equalTo(256)
            BoardScorer.scoreMove("XX_XXX") shouldMatch equalTo(256)
            BoardScorer.scoreMove("__XXX") shouldMatch equalTo(256)
            BoardScorer.scoreMove("XXX_X") shouldMatch equalTo(256)
        }
        it("calculates the appropriate score for a string containing XX") {
            BoardScorer.scoreMove("XX") shouldMatch equalTo(1) // Only 2 chars therefore can't extend
            BoardScorer.scoreMove("X_") shouldMatch equalTo(1) // Only 2 chars therefore can't extend
            BoardScorer.scoreMove("X_XX__") shouldMatch equalTo(256)
            BoardScorer.scoreMove("_XX") shouldMatch equalTo(1)
            BoardScorer.scoreMove("XX_XX") shouldMatch equalTo(256)
            BoardScorer.scoreMove("__XX") shouldMatch equalTo(4)
            BoardScorer.scoreMove("_XX_") shouldMatch equalTo(16)
            BoardScorer.scoreMove("XX___") shouldMatch equalTo(6)
            BoardScorer.scoreMove("XX_X") shouldMatch equalTo(256)
        }
        it("calculates the appropriate score for a string containing X") {
            BoardScorer.scoreMove("X") shouldMatch equalTo(1) // Only 2 chars therefore can't extend
            BoardScorer.scoreMove("__X_") shouldMatch equalTo(4) // Only 2 chars therefore can't extend
            BoardScorer.scoreMove("X__") shouldMatch equalTo(1)
            BoardScorer.scoreMove("_X") shouldMatch equalTo(1)
            BoardScorer.scoreMove("X_") shouldMatch equalTo(1)
            BoardScorer.scoreMove("_______X_") shouldMatch equalTo(12)
        }
    }

    describe("check that all of the scores for a move"){

        given("an empty board"){
            val board = Board.emptyBoard()
            val boardScorer = BoardScorer(board)

            it("calculates the score of the 1st column"){
                boardScorer.scorePosition(0,0,CellColour.RED) shouldMatch equalTo(13)
                boardScorer.scorePosition(0,0,CellColour.YELLOW) shouldMatch equalTo(13)
            }

            it("calculates the score of the 2nd column"){
                boardScorer.scorePosition(1,0,CellColour.RED) shouldMatch equalTo(15)
                boardScorer.scorePosition(1,0,CellColour.YELLOW) shouldMatch equalTo(15)
            }

            it("calculates the score of the 3rd column"){
                boardScorer.scorePosition(2,0,CellColour.RED) shouldMatch equalTo(17)
                boardScorer.scorePosition(2,0,CellColour.YELLOW) shouldMatch equalTo(17)
            }

            it("calculates the score of the 4th column"){
                boardScorer.scorePosition(3,0,CellColour.RED) shouldMatch equalTo(24)
                boardScorer.scorePosition(3,0,CellColour.YELLOW) shouldMatch equalTo(24)
            }

            it("calculates the score of the 5th column"){
                boardScorer.scorePosition(4,0,CellColour.RED) shouldMatch equalTo(17)
                boardScorer.scorePosition(4,0,CellColour.YELLOW) shouldMatch equalTo(17)
            }
            it("calculates the score of the 6th column"){
                boardScorer.scorePosition(5,0,CellColour.RED) shouldMatch equalTo(15)
                boardScorer.scorePosition(5,0,CellColour.YELLOW) shouldMatch equalTo(15)
            }
            it("calculates the score of the 7th column"){
                boardScorer.scorePosition(6,0,CellColour.RED) shouldMatch equalTo(13)
                boardScorer.scorePosition(6,0,CellColour.YELLOW) shouldMatch equalTo(13)
            }
        }

        given("a board with a few counters on it"){

            val moves = listOf(Triple(3,0,CellColour.RED), Triple(2,0,CellColour.YELLOW),
                    Triple(3,1,CellColour.RED),Triple(3,2,CellColour.YELLOW))
            val board = moves.fold(Board.emptyBoard(),Board::applyMove)
            val boardScorer = BoardScorer(board)
            val legalMoves = board.legalMoves()

            it("there should be 7 legal moves"){
                legalMoves.size shouldMatch equalTo(7)
            }

            it("should identify no winning move for Red or Yellow"){
                legalMoves.filter { board.isWinningMove(it.first, it.second, CellColour.RED)}.size shouldMatch equalTo(0)
                legalMoves.filter { board.isWinningMove(it.first, it.second, CellColour.YELLOW)}.size shouldMatch equalTo(0)
            }

            it("should generate a known set of scores for Red with one entry per column"){
                val scores =legalMoves.map{ move -> move to boardScorer.scorePosition(move.first, move.second, CellColour.RED)}
                            .sortedByDescending{it.second }
                scores.size shouldMatch equalTo(7)
                val bestMove = scores.first().first
                val bestScore = scores.first().second
                bestMove.first shouldMatch equalTo(2)
                bestMove.second shouldMatch equalTo(1)
                bestScore shouldMatch equalTo(73)
            }

            it("should generate a known set of scores for Yellow with one entry per column"){
                val scores = legalMoves.map{ move -> move to boardScorer.scorePosition(move.first, move.second, CellColour.YELLOW)}
                        .sortedByDescending{it.second}
                scores.size shouldMatch equalTo(7)
                val bestMove = scores.first().first
                val bestScore = scores.first().second
                bestMove.first shouldMatch equalTo(2)
                bestMove.second shouldMatch equalTo(1)
                bestScore shouldMatch equalTo(72)
            }
        }
    }
})