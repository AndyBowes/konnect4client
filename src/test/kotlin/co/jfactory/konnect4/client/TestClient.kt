package co.jfactory.konnect4.client

import co.jfactory.konnect4.strategy.greedyMoveSelector
import co.jfactory.konnect4.strategy.minimaxStategy

class TestClient {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val client = Konnect4Client("KotlinTestHarness", "kotlinRocks",
                    pollPause = 10,
                    moveStrategy = ::minimaxStategy)
            client.playGame()
        }
    }
}

