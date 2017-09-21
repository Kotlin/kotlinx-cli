package kotlinx.cli


class Subcommand(
        val cli: CommandLineInterface,
        val help: String,
        val action: () -> Unit
) {
    val name = cli.commandName
}


fun CommandLineInterface.subcommands(usage: String, helpCaption: String, vararg subcommands: Subcommand) {
    for (subcommand in subcommands) {
        setFlagAction(subcommand.name, object : CommandAction {
            override fun invoke(arguments: ListIterator<String>) {
                CommandLineParser(subcommand.cli).parseTokenized(arguments)
                subcommand.action()
            }
        })
    }

    addUsageEntry(usage)

    addHelpEntry(object : HelpEntry {
        override fun printHelp(helpPrinter: HelpPrinter) {
            helpPrinter.printText(helpCaption)
            for (subcommand in subcommands) {
                helpPrinter.printEntry("${subcommand.name} ...", subcommand.help)
            }
            helpPrinter.printSeparator()
        }
    })
}


fun subcommand(cli: CommandLineInterface, help: String, action: () -> Unit) =
        Subcommand(cli, help, action)
