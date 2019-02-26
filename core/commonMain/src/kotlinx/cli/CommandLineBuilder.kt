package kotlinx.cli

interface CommandLineBuilder {
    fun addUsageEntry(entry: String)
    fun addHelpEntry(helpEntry: HelpEntry)
    fun addPositionalArgument(positionalArgument: PositionalArgument)
    fun setFlagAction(flag: String, action: Action)
}