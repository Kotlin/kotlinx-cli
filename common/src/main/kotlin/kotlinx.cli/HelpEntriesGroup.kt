package kotlinx.cli

class HelpEntriesGroup(
        val parent: CommandLineInterface,
        private val description: String
) : CommandLineBuilder, HelpEntry {
    private val helpEntries = ArrayList<HelpEntry>()

    override fun addUsageEntry(entry: String) {
        parent.addUsageEntry(entry)
    }

    override fun addHelpEntry(helpEntry: HelpEntry) {
        helpEntries.add(helpEntry)
    }

    override fun addPositionalArgument(positionalArgument: PositionalArgument) {
        parent.addPositionalArgument(positionalArgument)
    }

    override fun setFlagAction(flag: String, action: Action) {
        parent.setFlagAction(flag, action)
    }

    override fun printHelp(helpPrinter: HelpPrinter) {
        helpPrinter.printText(description)
        for (helpEntry in helpEntries) {
            helpEntry.printHelp(helpPrinter)
        }
        helpPrinter.printSeparator()
    }
}


fun CommandLineInterface.helpEntriesGroup(description: String) =
        HelpEntriesGroup(this, description).also { addHelpEntry(it) }

fun HelpEntriesGroup.help(flags: List<String>, help: String, helpPrinter: HelpPrinter, exitAfterHelp: Boolean = true) =
        registerAction(object : FlagActionBase(flags, help) {
            override fun invoke() {
                parent.printHelp(helpPrinter)
                if (exitAfterHelp) {
                    throw HelpPrintedException()
                }
            }
        })

fun HelpEntriesGroup.help(flags: List<String>, help: String, exitAfterHelp: Boolean = true) =
        help(flags, help, parent.defaultHelpPrinter!!, exitAfterHelp)

fun HelpEntriesGroup.help(exitAfterHelp: Boolean = true) =
        help(listOf("-h", "--help"), "Prints help page", parent.defaultHelpPrinter!!, exitAfterHelp)