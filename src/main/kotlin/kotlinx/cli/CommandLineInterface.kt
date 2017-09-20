package kotlinx.cli

open class CommandLineInterface(
        program: String,
        private val usage: String? = null,
        private val description: String? = null,
        private val epilogue: String? = null,
        addHelp: Boolean = true
) {
    private val usageBuilder: StringBuilder? =
            if (usage == null) StringBuilder("Usage: $program ") else null
    private val actualUsage get() = usageBuilder?.toString() ?: usage!!

    private val helpEntries = ArrayList<HelpEntry>()

    private val positionalArguments = ArrayList<PositionalArgument>()
    fun getPositionalArgumentsIterator(): ListIterator<PositionalArgument> = positionalArguments.listIterator()

    private val flagActions = HashMap<String, FlagAction>()
    private val flagValueActions = HashMap<String, ArgumentAction>()

    init {
        if (addHelp) {
            help()
        }
    }

    fun printHelp(helpPrinter: HelpPrinter) {
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

    fun setFlagAction(flag: String, flagAction: FlagAction) {
        if (flag in flagActions) error("Flag is already set: $flag")
        flagActions[flag] = flagAction
    }

    fun setFlagValueAction(flag: String, argumentAction: ArgumentAction) {
        if (flag in flagValueActions) error("Flag is already set: $flag")
        flagValueActions[flag] = argumentAction
    }

    fun getFlagAction(flag: String): FlagAction? =
            flagActions[flag]

    fun getFlagValueAction(flag: String): ArgumentAction? =
            flagValueActions[flag]
}