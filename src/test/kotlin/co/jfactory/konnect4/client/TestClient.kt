package co.jfactory.konnect4.client

class TestClient {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val client = Konnect4Client("KotlinTestHarness", "kotlinRocks")
            client.playGame()
        }
    }
}