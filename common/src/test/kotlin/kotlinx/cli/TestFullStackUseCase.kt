package kotlinx.cli

import kotlin.test.Test
import kotlin.test.assertFailsWith

class TestFullStackUseCase {
    class FullStackClientCli : CommandLineInterface("Client") {
        val server by flagValueArgument("-s", "server", "Server to connect", "http://localhost:8080")
        val name by flagValueArgument("-n", "name", "User name", "CLI user")
        val command by flagValueArgument("-c", "command", "Command to issue", "status")
    }

    @Test
    fun testFullStackUseCase() {
        assertFailsWith<HelpPrintedException> {
            FullStackClientCli().parseArgs("-h")
        }
    }
}