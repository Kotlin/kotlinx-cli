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


fun <T : FlagValueActionBase> CommandLineBuilder.registerAction(action: T): T {
    addUsageEntry("[${action.flags.first()} ${action.valueSyntax}]")
    addHelpEntry(action)
    for (flag in action.flags) {
        setFlagAction(flag, action)
    }
    return action
}


fun <T> CommandLineBuilder.registerArgument(argument: FlagValueArgumentBase<T>): ArgumentValue<T> =
        argument.also { registerAction(it) }


fun CommandLineBuilder.flagValueAction(
        flags: List<String>, valueSyntax: String, help: String,
        action: (String) -> Unit
) {
    registerAction(object : FlagValueActionBase(flags, valueSyntax, help) {
        override fun invoke(argument: String) {
            action(argument)
        }
    })
}


fun CommandLineBuilder.flagValueAction(
        flag: String, valueSyntax: String, help: String,
        action: (String) -> Unit
) =
        flagValueAction(listOf(flag), valueSyntax, help, action)


fun <T> CommandLineBuilder.flagValueArgument(
        flags: List<String>, valueSyntax: String, help: String,
        initialValue: T,
        mapping: (String) -> T
) =
        registerArgument(object : FlagValueArgumentBase<T>(flags, valueSyntax, help, initialValue) {
            override fun invoke(argument: String) {
                value = mapping(argument)
            }
        })


fun <T> CommandLineBuilder.flagValueArgument(
        flag: String, valueSyntax: String, help: String,
        initialValue: T,
        mapping: (String) -> T
) =
        flagValueArgument(listOf(flag), valueSyntax, help, initialValue, mapping)


fun CommandLineBuilder.flagValueArgument(flags: List<String>, valueSyntax: String, help: String) =
        flagValueArgument(flags, valueSyntax, help, null, { it })

fun CommandLineBuilder.flagValueArgument(flag: String, valueSyntax: String, help: String) =
        flagValueArgument(listOf(flag), valueSyntax, help)

fun CommandLineBuilder.flagValueArgument(flags: List<String>, valueSyntax: String, help: String, initialValue: String) =
        flagValueArgument(flags, valueSyntax, help, initialValue, { it })

fun CommandLineBuilder.flagValueArgument(flag: String, valueSyntax: String, help: String, initialValue: String) =
        flagValueArgument(listOf(flag), valueSyntax, help, initialValue)

