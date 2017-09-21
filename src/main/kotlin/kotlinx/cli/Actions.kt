package kotlinx.cli

interface Action {
    fun invoke(arguments: ListIterator<String>)
}

interface FlagAction : Action {
    override fun invoke(arguments: ListIterator<String>) {
        invoke()
    }

    fun invoke()
}

interface ArgumentAction : Action {
    override fun invoke(arguments: ListIterator<String>) {
        if (arguments.hasNext())
            invoke(arguments.next())
        else
            throw MissingArgumentException()
    }

    fun invoke(argument: String)
}

interface CommandAction : Action