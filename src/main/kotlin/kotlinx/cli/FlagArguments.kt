package kotlinx.cli


abstract class FlagActionBase(
        val flags: List<String>,
        private val help: String
) : FlagAction, HelpEntry {

    init {
        if (flags.isEmpty()) error("At least one flag required")
    }

    private val syntax = flags.joinToString(", ")

    override fun printHelp(helpPrinter: HelpPrinter) {
        helpPrinter.printEntry(syntax, help)
    }
}


abstract class FlagArgumentBase<T>(
        flags: List<String>,
        help: String,
        initialValue: T
) : FlagActionBase(flags, help), ArgumentValue<T> {

    protected var value = initialValue

    override fun getValue(thisRef: Any?, prop: Any?): T =
            value
}


fun <T : FlagActionBase> CommandLineBuilder.registerAction(action: T): T {
    addUsageEntry("[${action.flags.first()}]")
    addHelpEntry(action)
    for (flag in action.flags) {
        setFlagAction(flag, action)
    }
    return action
}


fun <T> CommandLineBuilder.registerArgument(argument: FlagArgumentBase<T>): ArgumentValue<T> =
        registerAction(argument)


fun CommandLineBuilder.flagAction(flags: List<String>, help: String, action: () -> Unit) {
    registerAction(object : FlagActionBase(flags, help) {
        override fun invoke() {
            action()
        }
    })
}


fun CommandLineBuilder.flagAction(flag: String, help: String, action: () -> Unit) {
    flagAction(listOf(flag), help, action)
}


fun CommandLineBuilder.flagArgument(flag: String, help: String) =
        flagArgument(flag, help, false, true)


fun <T> CommandLineBuilder.flagArgument(flags: List<String>, help: String, initialValue: T, flagValue: T) =
        registerArgument(object : FlagArgumentBase<T>(flags, help, initialValue) {
            override fun invoke() {
                value = flagValue
            }
        })



fun <T> CommandLineBuilder.flagArgument(flag: String, help: String, initialValue: T, flagValue: T) =
        flagArgument(listOf(flag), help, initialValue, flagValue)


fun <T> CommandLineBuilder.foldFlagArguments(flags: List<String>, help: String, initialValue: T, fn: (T) -> T) =
        registerArgument(object : FlagArgumentBase<T>(flags, help, initialValue) {
            override fun invoke() {
                value = fn(value)
            }
        })


fun <T> CommandLineBuilder.foldFlagArguments(flag: String, help: String, initialValue: T, fn: (T) -> T) =
        foldFlagArguments(listOf(flag), help, initialValue, fn)


fun CommandLineInterface.help(flags: List<String>, help: String, helpPrinter: HelpPrinter, exitAfterHelp: Boolean = true) {
    registerAction(object : FlagActionBase(flags, help) {
        override fun invoke() {
            this@help.printHelp(helpPrinter)
            if (exitAfterHelp) {
                throw HelpPrintedException()
            }
        }
    })
}

fun CommandLineInterface.help(helpPrinter: HelpPrinter = SimpleHelpPrinter(24), exitAfterHelp: Boolean = true) {
    help(listOf("-h", "--help"), "Prints help page", helpPrinter, exitAfterHelp)
}