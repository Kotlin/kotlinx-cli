package kotlinx.cli

class CommandLineApplication: CommandLineInterface("") {
    private lateinit var exec: CommandLineContext.(args: Array<String>) -> Unit

    /**
     * The main fun signature is intended to look like standard main fun so that it is easy to adopt
     */
    infix fun main(func: CommandLineContext.(args: Array<String>) -> Unit) {
        exec = func
    }

    fun run(args: Array<String>): Int {
        return try {
            // parse the command line arguments
            parse(args)
            CommandLineContext().apply { exec(args) }.statusCode
        } catch (exitProcess: CommandLineError) {
            exitProcess.status
        } catch (throwable: Throwable) {
            1
        }
    }
}

fun command(func: CommandLineApplication.() -> Unit): CommandLineApplication =
        CommandLineApplication().apply(func)