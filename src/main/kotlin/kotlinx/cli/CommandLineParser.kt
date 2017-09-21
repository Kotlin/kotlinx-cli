package kotlinx.cli

class CommandLineParser(
        private val cli: CommandLineInterface
) {
    fun parse(args: Array<out String>) {
        parse(args.asList())
    }

    fun parse(args: List<String>) {
        if (args.isEmpty() && cli.printHelpByDefault) {
            cli.printHelp()
            throw HelpPrintedException()
        }

        try {
            doParse(args)
        }
        catch (e: HelpPrintedException) {
            throw e
        }
        catch (e: Throwable) {
            // TODO better error reporting
            e.message?.let { cli.defaultHelpPrinter?.printText(it) }
            cli.printHelp()
            throw e
        }
    }

    private lateinit var positionalsIterator: ListIterator<PositionalArgument>
    private var currentPositional: PositionalArgument? = null
    private var currentPositionalCount = 0

    private fun doParse(args: List<String>) {
        val argsIterator = tokenizeArgs(args).listIterator()

        positionalsIterator = cli.getPositionalArgumentsIterator()
        currentPositional = positionalsIterator.nextOrNull()
        currentPositionalCount = 0

        while (argsIterator.hasNext()) {
            val arg = argsIterator.next()

            if (cli.argumentsAfterDoubleDashArePositional && arg == "--") {
                while (argsIterator.hasNext()) {
                    handlePositionalArgument(argsIterator.next())
                }
                return
            }

            val flagAction = cli.getFlagAction(arg)
            if (flagAction != null) {
                flagAction.invoke()
                continue
            }

            val flagValueAction = cli.getFlagValueAction(arg)
            if (flagValueAction != null) {
                if (argsIterator.hasNext()) {
                    flagValueAction.invoke(argsIterator.next())
                    continue
                } else {
                    throw CommandLineException("No argument for flag $arg")
                }
            }

            handlePositionalArgument(arg)
        }

        currentPositional?.let {
            checkEnoughPositionals(it, currentPositionalCount)
        }
        while (positionalsIterator.hasNext()) {
            checkEnoughPositionals(positionalsIterator.next(), 0)
        }
    }

    private fun handlePositionalArgument(arg: String) {
        currentPositional.let { cp ->
            if (cp != null) {
                cp.action.invoke(arg)
                currentPositionalCount++
                if (currentPositionalCount >= cp.maxArgs) {
                    currentPositional = positionalsIterator.nextOrNull()
                    currentPositionalCount = 0
                }
            } else {
                throw CommandLineException("Unexpected positional argument: '$arg'")
            }
        }
    }

    private fun checkEnoughPositionals(positionalArgument: PositionalArgument, actualCount: Int) {
        if (actualCount < positionalArgument.minArgs) {
            throw CommandLineException(
                    "Not enough positional arguments for ${positionalArgument.name}: " +
                            "$actualCount, expected at least ${positionalArgument.minArgs}"
            )
        }
    }

    private fun tokenizeArgs(args: List<String>): List<String> {
        // TODO tokenize args according to parser options
        return args
    }
}