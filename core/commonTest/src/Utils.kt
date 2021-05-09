package kotlinx.cli


class ArgParserWithoutExit(
    programName: String,
    useDefaultHelpShortName: Boolean = true,
    prefixStyle: OptionPrefixStyle = OptionPrefixStyle.LINUX,
    skipExtraArguments: Boolean = false,
    strictSubcommandOptionsOrder: Boolean = false
) : ArgParser(programName, useDefaultHelpShortName, prefixStyle, skipExtraArguments, strictSubcommandOptionsOrder) {
    override fun printError(message: String): Nothing {
        error(message)
    }

    override fun printStatusAndExit(message: String) {
        error(message)
    }
}

abstract class SubcommandWithoutExit(name: String, actionDescription: String) : Subcommand(name, actionDescription) {
    override fun printError(message: String): Nothing {
        error(message)
    }

    override fun printStatusAndExit(message: String) {
        error(message)
    }
}
