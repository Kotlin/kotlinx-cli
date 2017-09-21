package kotlinx.cli


abstract class FlagValueActionBase(
        val flags: List<String>,
        val valueSyntax: String,
        private val help: String
) : ArgumentAction, HelpEntry {

    private val syntax = "${flags.joinToString(", ")} $valueSyntax"

    override fun printHelp(helpPrinter: HelpPrinter) {
        helpPrinter.printEntry(syntax, help)
    }
}


abstract class FlagValueArgumentBase<T>(
        flags: List<String>,
        valueSyntax: String,
        help: String,
        initialValue: T
) : FlagValueActionBase(flags, valueSyntax, help), ArgumentValue<T> {

    protected var value = initialValue

    override fun getValue(thisRef: Any?, prop: Any?): T =
            value
}


fun <T : FlagValueActionBase> CommandLineInterface.registerAction(action: T): T {
    addUsageEntry("[${action.flags.first()} ${action.valueSyntax}]")
    addHelpEntry(action)
    for (flag in action.flags) {
        setFlagValueAction(flag, action)
    }
    return action
}


fun <T> CommandLineInterface.registerArgument(argument: FlagValueArgumentBase<T>): ArgumentValue<T> =
        argument.also { registerAction(it) }


fun CommandLineInterface.flagValueAction(
        flags: List<String>, valueSyntax: String, help: String,
        action: (String) -> Unit
) {
    registerAction(object : FlagValueActionBase(flags, valueSyntax, help) {
        override fun invoke(argument: String) {
            action(argument)
        }
    })
}


fun CommandLineInterface.flagValueAction(
        flag: String, valueSyntax: String, help: String,
        action: (String) -> Unit
) =
        flagValueAction(listOf(flag), valueSyntax, help, action)


fun <T> CommandLineInterface.flagValueArgument(
        flags: List<String>, valueSyntax: String, help: String,
        initialValue: T,
        mapping: (String) -> T
) =
        registerArgument(object : FlagValueArgumentBase<T>(flags, valueSyntax, help, initialValue) {
            override fun invoke(argument: String) {
                value = mapping(argument)
            }
        })


fun <T> CommandLineInterface.flagValueArgument(
        flag: String, valueSyntax: String, help: String,
        initialValue: T,
        mapping: (String) -> T
) =
        flagValueArgument(listOf(flag), valueSyntax, help, initialValue, mapping)


fun CommandLineInterface.flagValueArgument(flags: List<String>, valueSyntax: String, help: String) =
        flagValueArgument(flags, valueSyntax, help, null, { it })

fun CommandLineInterface.flagValueArgument(flag: String, valueSyntax: String, help: String) =
        flagValueArgument(listOf(flag), valueSyntax, help)

fun CommandLineInterface.flagValueArgument(flags: List<String>, valueSyntax: String, help: String, initialValue: String) =
        flagValueArgument(flags, valueSyntax, help, initialValue, { it })

fun CommandLineInterface.flagValueArgument(flag: String, valueSyntax: String, help: String, initialValue: String) =
        flagValueArgument(listOf(flag), valueSyntax, help, initialValue)

