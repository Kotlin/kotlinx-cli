package kotlinx.cli

class CommandLineParser internal constructor(
        private val cli: CommandLineInterface
) {
    fun parse(args: Array<out String>) {
        parse(args.asList())
    }

    fun parse(args: List<String>) {
        parseTokenized(tokenizeArgs(args).listIterator())
    }

    fun parseTokenized(argsIterator: ListIterator<String>) {
        if (!argsIterator.hasNext() && cli.printHelpByDefault) {
            cli.printHelp()
            throw HelpPrintedException()
        }

        try {
            doParse(argsIterator)
        } catch (e: StopParsingException) {
            throw e
        } catch (e: Throwable) {
            // TODO better error reporting
            e.message?.let { cli.defaultHelpPrinter?.printText(it) }
            cli.printHelp()
            throw e
        }
    }

    private lateinit var positionalsIterator: ListIterator<PositionalArgument>
    private var currentPositional: PositionalArgument? = null
    private var currentPositionalCount = 0

    private fun doParse(argsIterator: ListIterator<String>) {
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

            val action = cli.getFlagAction(arg)
            if (action == null) {
                handlePositionalArgument(arg)
            } else {
                try {
                    action.invoke(argsIterator)
                } catch (e: MissingArgumentException) {
                    throw CommandLineException("No argument for flag $arg")
                }
            }
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

    private fun tokenizeArgs(args: List<String>): List<String> =
            args.flatMap { tokenizeArg(it) }

    private fun tokenizeArg(arg: String): List<String> {
        if (cli.getFlagAction(arg) != null) return listOf(arg)

        if (cli.longTagValueDelimiter != null && cli.longTagPrefixes.any { arg.startsWith(it) }) {
            val k = arg.indexOf(cli.longTagValueDelimiter)
            if (k >= 0) {
                val longTag = arg.substring(0, k)
                val longValue = arg.substring(k + 1)
                return listOf(longTag, longValue)
            }
        }

        if (isShortTagPrefixed(arg)) {
            return tokenizeShortTags(arg)
        }

        return listOf(arg)
    }

    private fun tokenizeShortTags(arg: String): List<String> {
        val result = ArrayList<String>()
        for (i in 1 until arg.length) {
            val fullTag = "${cli.shortTagPrefix}${arg[i]}"
            val action = cli.getFlagAction(fullTag) ?: return listOf(arg)
            when (action) {
                is FlagAction -> result.add(fullTag)

                is ArgumentAction -> {
                    result.add(fullTag)
                    if (i < arg.lastIndex) {
                        result.add(arg.substring(i + 1))
                    }
                    return result
                }

                else -> error("Unexpected short tag action: $fullTag => $action")
            }
        }
        return result
    }

    private fun isShortTagPrefixed(arg: String) =
            cli.shortTagPrefix != null && arg.startsWith(cli.shortTagPrefix) &&
            cli.longTagPrefixes.none { arg.startsWith(it) }
}