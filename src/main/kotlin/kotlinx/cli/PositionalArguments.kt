package kotlinx.cli


abstract class PositionalActionBase(
        override val name: String,
        private val help: String,
        override val minArgs: Int,
        override val maxArgs: Int
) : PositionalArgument, ArgumentAction, HelpEntry {

    override val action: ArgumentAction get() = this

    override fun printHelp(helpPrinter: HelpPrinter) {
        helpPrinter.printEntry(name, help)
    }
}


abstract class PositionalArgumentBase<T>(
        name: String,
        help: String,
        initialValue: T,
        minArgs: Int,
        maxArgs: Int
) : PositionalActionBase(name, help, minArgs, maxArgs), ArgumentValue<T> {

    protected var value: T = initialValue

    override fun getValue(thisRef: Any?, prop: Any?): T = value
}


abstract class SinglePositionalArgumentBase<T>(
        name: String,
        help: String,
        initialValue: T,
        minArgs: Int = 0
) : PositionalArgumentBase<T>(name, help, initialValue, minArgs, 1)


abstract class ListPositionalArgumentBase<T>(
        name: String,
        help: String,
        destination: MutableList<T>,
        minArgs: Int = 0,
        maxArgs: Int = Int.MAX_VALUE
) : PositionalActionBase(name, help, minArgs, maxArgs), ArgumentValue<List<T>> {

    protected val value = destination

    override fun getValue(thisRef: Any?, prop: Any?): List<T> = value
}


fun <T : PositionalActionBase> CommandLineBuilder.registerAction(action: T): T =
        action.also {
            addUsageEntry(it.name)
            addHelpEntry(it)
            addPositionalArgument(it)
        }


fun <T> CommandLineBuilder.registerArgument(positionalArgument: PositionalArgumentBase<T>): ArgumentValue<T> =
        registerAction(positionalArgument)


fun CommandLineBuilder.positionalAction(name: String, help: String, minArgs: Int = 0, maxArgs: Int = 1, action: (String) -> Unit) {
    registerAction(object : PositionalActionBase(name, help, minArgs, maxArgs) {
        override fun invoke(argument: String) {
            action(argument)
        }
    })
}


fun CommandLineBuilder.positionalArgument(name: String, help: String, minArgs: Int = 0) =
        positionalArgument(name, help, minArgs, { it })


fun CommandLineBuilder.positionalArgument(name: String, help: String, initialValue: String, minArgs: Int = 0) =
        positionalArgument(name, help, initialValue, minArgs, { it })


fun <T : Any> CommandLineBuilder.positionalArgument(name: String, help: String, minArgs: Int = 0, mapping: (String) -> T) =
        registerArgument(object : SinglePositionalArgumentBase<T?>(name, help, null, minArgs) {
            override fun invoke(argument: String) {
                value = mapping(argument)
            }
        })


fun <T : Any> CommandLineBuilder.positionalArgument(name: String, help: String, initialValue: T, minArgs: Int = 0, mapping: (String) -> T) =
        registerArgument(object : SinglePositionalArgumentBase<T>(name, help, initialValue, minArgs) {
            override fun invoke(argument: String) {
                value = mapping(argument)
            }
        })


fun CommandLineBuilder.positionalArgumentsList(
        name: String, help: String,
        destination: MutableList<String> = ArrayList(),
        minArgs: Int = 0, maxArgs: Int = Int.MAX_VALUE
): ArgumentValue<List<String>> =
        positionalArgumentsList(name, help, destination, minArgs, maxArgs, { it })


fun <T> CommandLineBuilder.positionalArgumentsList(
        name: String, help: String,
        destination: MutableList<T> = ArrayList(),
        minArgs: Int = 0, maxArgs: Int = Int.MAX_VALUE,
        mapping: (String) -> T
): ArgumentValue<List<T>> =
        registerAction(object : ListPositionalArgumentBase<T>(name, help, destination, minArgs, maxArgs) {
            override fun invoke(argument: String) {
                value.add(mapping(argument))
            }
        })


fun <T> CommandLineBuilder.foldPositionalArguments(
        name: String, help: String,
        initialValue: T,
        minArgs: Int = 0, maxArgs: Int = Int.MAX_VALUE,
        fn: (T, String) -> T
) =
        registerArgument(object : PositionalArgumentBase<T>(name, help, initialValue, minArgs, maxArgs) {
            override fun invoke(argument: String) {
                value = fn(value, argument)
            }
        })
