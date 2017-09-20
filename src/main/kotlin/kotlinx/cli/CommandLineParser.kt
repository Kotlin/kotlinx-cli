package kotlinx.cli

class CommandLineParser(
    private val cli: CommandLineInterface
) {
    fun parse(args: Array<out String>) {
        parse(args.asList())
    }

    fun parse(args: List<String>) {
        val argsIterator = tokenizeArgs(args).listIterator()

        val positionalsIterator = cli.getPositionalArgumentsIterator()
        var currentPositional = positionalsIterator.nextOrNull()
        var currentPositionalCount = 0

        while (argsIterator.hasNext()) {
            val arg = argsIterator.next()

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
                }
                else {
                    throw CommandLineException("No argument for flag $arg")
                }
            }

            if (currentPositional != null) {
                currentPositional.action.invoke(arg)
                currentPositionalCount++
                if (currentPositionalCount >= currentPositional.maxArgs) {
                    currentPositional = positionalsIterator.nextOrNull()
                    currentPositionalCount = 0
                }
            }
            else {
                throw CommandLineException("Unexpected positional argument: '$arg'")
            }
        }

        if (currentPositional != null) {
            checkEnoughPositionals(currentPositional, currentPositionalCount)
        }
        while (positionalsIterator.hasNext()) {
            checkEnoughPositionals(positionalsIterator.next(), 0)
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