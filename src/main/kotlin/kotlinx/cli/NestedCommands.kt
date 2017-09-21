package kotlinx.cli


fun CommandLineInterface.command(cli: CommandLineInterface, help: String, action: () -> Unit) {
    val command = cli.commandName

    setFlagAction(command, object : CommandAction {
        override fun invoke(arguments: ListIterator<String>) {
            CommandLineParser(cli).parseTokenized(arguments)
            action()
        }
    })
    addUsageEntry("[$command ...]")
    addHelpEntry(object : HelpEntry {
        override fun printHelp(helpPrinter: HelpPrinter) {
            helpPrinter.printEntry("$command ...", help)
        }
    })
}