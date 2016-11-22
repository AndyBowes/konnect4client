package co.jfactory.konnect4.model

import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.should.shouldMatch
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class BoardSpec : Spek({
    describe("Check winning positions are detected") {

        given("an empty board") {
            val board = createEmptyBoard(7, 6)
            it("no position should give a winning position") {
                for (colour in listOf(CellColour.RED, CellColour.YELLOW)) (0..7).forEach { c ->
                    for (r in 0..6) {
                        board.isWinningMove(c,r,colour) shouldMatch equalTo(false)
                    }
                }
            }
        }

        given("A board with 3 Red counters in a horizontal row") {
            val board = createEmptyBoard(7,6)
            val moves = listOf(Triple(2,2,CellColour.RED),Triple(3,2,CellColour.RED), Triple(4,2,CellColour.RED))
            val finalBoard = moves.fold(board, Board::applyMove)

            it("identifies horizontal winning position if counter added at end of line") {
                finalBoard.isWinningMove(1,2,CellColour.RED) shouldMatch equalTo(true)
                finalBoard.isWinningMove(5,2,CellColour.RED) shouldMatch equalTo(true)
            }
            it("recognises that other colour does not create a winning position") {
                finalBoard.isWinningMove(1,2,CellColour.YELLOW) shouldMatch equalTo(false)
                finalBoard.isWinningMove(5,2,CellColour.YELLOW) shouldMatch equalTo(false)
            }
            it("Adding a counter in a different place does not generate a winning position") {
                finalBoard.isWinningMove(0,2,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(6,2,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(1,1,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(1,5,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(5,1,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(5,5,CellColour.RED) shouldMatch equalTo(false)
            }
        }

        given("A board with 3 Yellow counters in a vertical line") {
            val board = createEmptyBoard(7,6)
            val moves = listOf(Triple(3,2,CellColour.YELLOW),Triple(3,3,CellColour.YELLOW), Triple(3,4,CellColour.YELLOW))
            val finalBoard = moves.fold(board, Board::applyMove)

            it("identifies horizontal winning position if counter added at end of line") {
                finalBoard.isWinningMove(3,1,CellColour.YELLOW) shouldMatch equalTo(true)
                finalBoard.isWinningMove(3,5,CellColour.YELLOW) shouldMatch equalTo(true)
            }
            it("recognises that other colour does not create a winning position") {
                finalBoard.isWinningMove(3,1,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(3,5,CellColour.RED) shouldMatch equalTo(false)
            }
            it("Adding a counter in a different place does not generate a winning position") {
                finalBoard.isWinningMove(0,2,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(6,2,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(1,1,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(1,5,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(5,1,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(5,5,CellColour.RED) shouldMatch equalTo(false)
            }
        }

        given("A board with 3 Yellow counters in a positive diagonal line") {
            val board = createEmptyBoard(7,6)
            val moves = listOf(Triple(3,2,CellColour.YELLOW),Triple(4,3,CellColour.YELLOW), Triple(5,4,CellColour.YELLOW))
            val finalBoard = moves.fold(board, Board::applyMove)

            it("identifies horizontal winning position if counter added at end of line") {
                finalBoard.isWinningMove(2,1,CellColour.YELLOW) shouldMatch equalTo(true)
                finalBoard.isWinningMove(6,5,CellColour.YELLOW) shouldMatch equalTo(true)
            }
            it("recognises that other colour does not create a winning position") {
                finalBoard.isWinningMove(2,1,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(6,5,CellColour.RED) shouldMatch equalTo(false)
            }
            it("Adding a counter in a different place does not generate a winning position") {
                finalBoard.isWinningMove(0,2,CellColour.YELLOW) shouldMatch equalTo(false)
                finalBoard.isWinningMove(6,2,CellColour.YELLOW) shouldMatch equalTo(false)
                finalBoard.isWinningMove(1,1,CellColour.YELLOW) shouldMatch equalTo(false)
                finalBoard.isWinningMove(1,5,CellColour.YELLOW) shouldMatch equalTo(false)
                finalBoard.isWinningMove(5,1,CellColour.YELLOW) shouldMatch equalTo(false)
                finalBoard.isWinningMove(5,5,CellColour.YELLOW) shouldMatch equalTo(false)
            }
        }

        given("A board with 3 Red counters in a negative diagonal line") {
            val board = createEmptyBoard(7,6)
            val moves = listOf(Triple(5,2,CellColour.RED),Triple(4,3,CellColour.RED), Triple(3,4,CellColour.RED))
            val finalBoard = moves.fold(board, Board::applyMove)

            it("identifies horizontal winning position if counter added at end of line") {
                finalBoard.isWinningMove(6,1,CellColour.RED) shouldMatch equalTo(true)
                finalBoard.isWinningMove(2,5,CellColour.RED) shouldMatch equalTo(true)
            }
            it("recognises that other colour does not create a winning position") {
                finalBoard.isWinningMove(6,1,CellColour.YELLOW) shouldMatch equalTo(false)
                finalBoard.isWinningMove(2,5,CellColour.YELLOW) shouldMatch equalTo(false)
            }
            it("Adding a counter in a different place does not generate a winning position") {
                finalBoard.isWinningMove(0,2,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(6,2,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(1,1,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(1,5,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(5,1,CellColour.RED) shouldMatch equalTo(false)
                finalBoard.isWinningMove(5,5,CellColour.RED) shouldMatch equalTo(false)
            }
        }
    }
})

private fun createEmptyBoard(columns: Int, rows: Int) = Board((0..columns).map { (0..rows).map { CellColour.EMPTY } })
