package kotlinx.cli

interface FlagAction {
    fun invoke()
}

interface ArgumentAction {
    fun invoke(argument: String)
}

