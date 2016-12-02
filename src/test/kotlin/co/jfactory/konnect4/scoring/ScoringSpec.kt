package co.jfactory.konnect4.scoring

import co.jfactory.konnect4.model.Board
import co.jfactory.konnect4.model.CellColour
import co.jfactory.konnect4.model.isWinningMove
import io.kotlintest.specs.FeatureSpec

class BoardScorerSpec : FeatureSpec(){
init {
    feature("Check that Scorer works appropriately") {
        scenario("calculates the appropriate score for a string containing XXXX") {
            BoardScorer.scoreMove("XXXX") shouldEqual 10000
            BoardScorer.scoreMove("XXXXX") shouldEqual 10000
            BoardScorer.scoreMove("__XXXX") shouldEqual 10000
            BoardScorer.scoreMove("__XXXX_X") shouldEqual 10000
        }
        scenario("calculates the appropriate score for a string containing XXX") {
            BoardScorer.scoreMove("XXX") shouldEqual 1 // Only 3 chars therefore can't extend
            BoardScorer.scoreMove("X_XXX__") shouldEqual 1024
            BoardScorer.scoreMove("_XXX") shouldEqual 256
            BoardScorer.scoreMove("XX_XXX") shouldEqual 256
            BoardScorer.scoreMove("__XXX") shouldEqual 256
            BoardScorer.scoreMove("XXX_X") shouldEqual 256
        }
        scenario("calculates the appropriate score for a string containing XX") {
            BoardScorer.scoreMove("XX") shouldEqual 1 // Only 2 chars therefore can't extend
            BoardScorer.scoreMove("X_") shouldEqual 1 // Only 2 chars therefore can't extend
            BoardScorer.scoreMove("X_XX__") shouldEqual 256
            BoardScorer.scoreMove("_XX") shouldEqual 1
            BoardScorer.scoreMove("XX_XX") shouldEqual 256
            BoardScorer.scoreMove("__XX") shouldEqual 4
            BoardScorer.scoreMove("_XX_") shouldEqual 16
            BoardScorer.scoreMove("XX___") shouldEqual 6
            BoardScorer.scoreMove("XX_X") shouldEqual 256
        }
        scenario("calculates the appropriate score for a string containing X") {
            BoardScorer.scoreMove("X") shouldEqual 1 // Only 2 chars therefore can't extend
            BoardScorer.scoreMove("__X_") shouldEqual 4 // Only 2 chars therefore can't extend
            BoardScorer.scoreMove("X__") shouldEqual 1
            BoardScorer.scoreMove("_X") shouldEqual 1
            BoardScorer.scoreMove("X_") shouldEqual 1
            BoardScorer.scoreMove("_______X_") shouldEqual 12
        }
    }

    feature("check that all of the scores for a move") {
        scenario("an empty board") {
            val board = Board.emptyBoard()
            val boardScorer = BoardScorer(board)

            boardScorer.scorePosition(0, 0, CellColour.RED) shouldEqual 13
            boardScorer.scorePosition(0, 0, CellColour.YELLOW) shouldEqual 13

            boardScorer.scorePosition(1, 0, CellColour.RED) shouldEqual 15
            boardScorer.scorePosition(1, 0, CellColour.YELLOW) shouldEqual 15

            boardScorer.scorePosition(2, 0, CellColour.RED) shouldEqual 17
            boardScorer.scorePosition(2, 0, CellColour.YELLOW) shouldEqual 17

            boardScorer.scorePosition(3, 0, CellColour.RED) shouldEqual 24
            boardScorer.scorePosition(3, 0, CellColour.YELLOW) shouldEqual 24

            boardScorer.scorePosition(4, 0, CellColour.RED) shouldEqual 17
            boardScorer.scorePosition(4, 0, CellColour.YELLOW) shouldEqual 17

            boardScorer.scorePosition(5, 0, CellColour.RED) shouldEqual 15
            boardScorer.scorePosition(5, 0, CellColour.YELLOW) shouldEqual 15

            boardScorer.scorePosition(6, 0, CellColour.RED) shouldEqual 13
            boardScorer.scorePosition(6, 0, CellColour.YELLOW) shouldEqual 13
        }

        scenario("a board with a few counters on it") {

            val moves = listOf(Triple(3, 0, CellColour.RED), Triple(2, 0, CellColour.YELLOW),
                    Triple(3, 1, CellColour.RED), Triple(3, 2, CellColour.YELLOW))
            val board = moves.fold(Board.emptyBoard(), Board::applyMove)
            val boardScorer = BoardScorer(board)
            val legalMoves = board.legalMoves()

//            it("there should be 7 legal moves"){
            legalMoves.size shouldEqual 7

//            it("should identify no winning move for Red or Yellow"){
            legalMoves.filter { board.isWinningMove(it.first, it.second, CellColour.RED) }.size shouldEqual 0
            legalMoves.filter { board.isWinningMove(it.first, it.second, CellColour.YELLOW) }.size shouldEqual 0

//            it("should generate a known set of scores for Red with one entry per column"){
            var scores = legalMoves.map { move -> move to boardScorer.scorePosition(move.first, move.second, CellColour.RED) }
                    .sortedByDescending { it.second }
            scores.size shouldEqual 7
            var bestMove = scores.first().first
            var bestScore = scores.first().second
            bestMove.first shouldEqual 2
            bestMove.second shouldEqual 1
            bestScore shouldEqual 73

//            it("should generate a known set of scores for Yellow with one entry per column"){
            scores = legalMoves.map { move -> move to boardScorer.scorePosition(move.first, move.second, CellColour.YELLOW) }
                    .sortedByDescending { it.second }
            scores.size shouldEqual 7
            bestMove = scores.first().first
            bestScore = scores.first().second
            bestMove.first shouldEqual 2
            bestMove.second shouldEqual 1
            bestScore shouldEqual 72
        }
    }
}
}