# konnect4client
Kotlin Client Library for the Connect4 REST API used by Leeds/York Code DOJOs


The creation of a Connect4 Client is very straightforward.
A class like this will create a client & play a game using the default strategy which just puts a counter in the 1st legal position it finds.

    class MyConnect4Client {
      companion object {
          @JvmStatic fun main(args: Array<String>) {
              val userName = "<<MyUserName>>"
              val password = "<<MyPassword>>"
              val client = Konnect4Client(userName, password)
              client.playGame()
          }
      }
    }

To make progress you will need to define a function that implements a better strategy.
This function needs to have the signature of (Player, GameState) -> Int where the return value identifies which column the counter will be placed in.
This function is injected into the moveStrategy property of the Konnect4Client & this will be used in place of the default strategy. 

    fun randomStategy(player: Player, gameState: GameState) : Int {
        val playerColour = gameState.getPlayerColour(player)
        val legalMoves = gameState.getLegalMoves()
        return legalMoves.first().first
    }

    class MyConnect4Client {
      companion object {
          @JvmStatic fun main(args: Array<String>) {
              val userName = "<<MyUserName>>"
              val password = "<<MyPassword>>"
              val client = Konnect4Client(userName, password, moveStrategy = ::randomStrategy)
              client.playGame()
          }
      }
    }

