package kotlinx.cli

open class CommandLineInterface(
        val commandName: String,
        private val usage: String? = null,
        private val description: String? = null,
        private val epilogue: String? = null,
        val defaultHelpPrinter: HelpPrinter? = SimpleHelpPrinter(24),
        val printHelpByDefault: Boolean = true,
        val argumentsAfterDoubleDashArePositional: Boolean = true
) {
    private val usageBuilder: StringBuilder? =
            if (usage == null) StringBuilder("Usage: $commandName ") else null
    private val actualUsage get() = usageBuilder?.toString() ?: usage!!

    private val helpEntries = ArrayList<HelpEntry>()

    private val positionalArguments = ArrayList<PositionalArgument>()
    fun getPositionalArgumentsIterator(): ListIterator<PositionalArgument> = positionalArguments.listIterator()

    private val flagActions = HashMap<String, Action>()

    init {
        if (defaultHelpPrinter != null) {
            help(listOf("-h", "--help"), "Prints help", defaultHelpPrinter, true)
        }
    }

    fun printHelp(helpPrinter: HelpPrinter? = defaultHelpPrinter) {
        if (helpPrinter == null) return

        with(helpPrinter) {
            begin()

            printText(actualUsage)
            printSeparator()

            description?.let {
                printText(it)
                printSeparator()
            }

            for (entry in helpEntries) {
                entry.printHelp(helpPrinter)
            }

            epilogue?.let {
                printSeparator()
                printText(it)
            }

            end()
        }
    }

    fun addUsageEntry(entry: String) {
        usageBuilder?.run {
            append(entry).append(" ")
        }
    }

    fun addHelpEntry(helpEntry: HelpEntry) {
        helpEntries.add(helpEntry)
    }

    fun addPositionalArgument(positionalArgument: PositionalArgument) {
        positionalArguments.add(positionalArgument)
    }

    private fun checkNewFlag(flag: String) {
        if (flag in flagActions) {
            error("Flag is already set: $flag")
        }
    }

    fun setFlagAction(flag: String, action: Action) {
        checkNewFlag(flag)
        flagActions[flag] = action
    }

    fun getFlagAction(flag: String): Action? =
            flagActions[flag]
}