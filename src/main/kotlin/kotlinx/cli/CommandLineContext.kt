package kotlinx.cli

/**
 * This class executes the main{} block and defines exitProcess so that Process::exitProcess isn't called
 * directly.
 */
class CommandLineContext {
    var statusCode: Int = 0

    fun exitProcess(status: Int): Int {
        throw CommandLineError(status)
    }
}