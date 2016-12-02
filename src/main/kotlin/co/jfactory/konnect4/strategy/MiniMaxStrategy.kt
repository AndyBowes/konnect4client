package co.jfactory.konnect4.strategy

import co.jfactory.konnect4.model.*
import co.jfactory.konnect4.scoring.BoardScorer

@Suppress("UNUSED_VARIABLE")
 /**
 * Use the Minimax Algorithm to attempt to minimise the maximum win by the opponent.
 */
fun minimaxStategy(colour: CellColour, gameState: GameState): Int {

    fun findMove(board: Board, colour: CellColour, depth: Int, isPlayer: Boolean): Pair<Pair<Int, Int>, Int> {

        // If there is a winning move for this colour then we can short circuit the process
        val winningMove: Pair<Int, Int>? = board.legalMoves().find{ board.isWinningMove(it.first,it.second, colour) }
        if (winningMove != null){
            return Pair( winningMove, BoardScorer.WINNING_SCORE * ( if (isPlayer) -1 else 1))
        }

        if (depth == 0) {
            // At the leaf level then find the move that gives the maximum score
            val scorer = BoardScorer(board)
            return board.legalMoves().map { Pair(it, scorer.scorePosition(it.first, it.second, colour)) }.maxBy { it.second }!!
        } else {
            if (isPlayer) {
                // This Player attempts to minimise score
                return board.legalMoves().map { move -> Pair(move, board.applyMove(move, colour)) }
                        .map { Pair(it.first, findMove(it.second, !colour, depth, !isPlayer).second) }
                        .minBy { it.second }!!
            } else {
                // Other Player attempts to maximise score
                return board.legalMoves().map { move -> Pair(move, board.applyMove(move, colour)) }
                        .map { Pair(it.first, findMove(it.second, !colour, depth - 1, !isPlayer).second) }
                        .maxBy { it.second }!!
            }
        }
    }

    val depth = 2
    val (bestMove, score) = findMove(gameState.getBoard(), colour, depth, true)
    println("${colour}: Playing Best move ${bestMove}")
    return bestMove.first  // Returns the column from the Best Move
}