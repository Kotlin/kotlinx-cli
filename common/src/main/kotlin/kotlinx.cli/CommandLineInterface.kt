package kotlinx.cli

open class CommandLineInterface(
        val commandName: String,
        private val usage: String? = null,
        private val description: String? = null,
        private val epilogue: String? = null,
        addHelp: Boolean = true,
        val defaultHelpPrinter: HelpPrinter? = SimpleHelpPrinter(24),
        val printHelpByDefault: Boolean = true,
        val argumentsAfterDoubleDashArePositional: Boolean = true,
        val shortTagPrefix: String? = null,
        val longTagPrefixes: List<String> = emptyList(),
        val longTagValueDelimiter: String? = null
) : CommandLineBuilder {
    private val usageBuilder: StringBuilder? =
            if (usage == null) StringBuilder("Usage: $commandName ") else null
    private val actualUsage get() = usageBuilder?.toString() ?: usage!!

    private val helpEntries = ArrayList<HelpEntry>()

    private val positionalArguments = ArrayList<PositionalArgument>()
    fun getPositionalArgumentsIterator(): ListIterator<PositionalArgument> = positionalArguments.listIterator()

    private val flagActions = HashMap<String, Action>()

    init {
        if (addHelp && defaultHelpPrinter != null) {
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

    override fun addUsageEntry(entry: String) {
        usageBuilder?.run {
            append(entry).append(" ")
        }
    }

    override fun addHelpEntry(helpEntry: HelpEntry) {
        helpEntries.add(helpEntry)
    }

    override fun addPositionalArgument(positionalArgument: PositionalArgument) {
        positionalArguments.add(positionalArgument)
    }

    private fun checkNewFlag(flag: String) {
        if (flag in flagActions) {
            error("Flag is already set: $flag")
        }
    }

    override fun setFlagAction(flag: String, action: Action) {
        checkNewFlag(flag)
        flagActions[flag] = action
    }

    fun getFlagAction(flag: String): Action? =
            flagActions[flag]
}