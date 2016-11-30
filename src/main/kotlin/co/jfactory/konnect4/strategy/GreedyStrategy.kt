package co.jfactory.konnect4.strategy

import co.jfactory.konnect4.model.CellColour
import co.jfactory.konnect4.model.GameState
import co.jfactory.konnect4.model.getBoard
import co.jfactory.konnect4.model.isWinningMove
import co.jfactory.konnect4.scoring.BoardScorer

/**
 * Use a Greedy Algorithm to pick the highest scoring position each turn.
 * This should be relatively easy to beat with a more intelligent strategy
 *
 * If a winning move is available take it
 * else if opponent has a winning move block it
 * else pick highest scoring legal move
 */
fun greedyMoveSelector(colour: CellColour, gameState: GameState): Int{
    val board = gameState.getBoard()
    val legalMoves = board.legalMoves()

    val winningMoves = legalMoves.filter { board.isWinningMove(it.first, it.second, colour) }
    if (winningMoves.size > 0) {
        println("${colour}: Playing winning move ${winningMoves.first()}")
        return winningMoves.first().first
    } else {
        val losingMoves = legalMoves.filter { board.isWinningMove(it.first, it.second, !colour) }
        if (losingMoves.size > 0) {
            println("${colour}: Blocking move ${losingMoves.first()}")
            return losingMoves.first().first
        } else {
            val boardScorer = BoardScorer(board)
            val bestMove = legalMoves.sortedByDescending { boardScorer.scorePosition(it.first, it.second, colour)}.first()
            println("${colour}: Playing Best move ${bestMove}")
            return bestMove.first
        }
    }
}
