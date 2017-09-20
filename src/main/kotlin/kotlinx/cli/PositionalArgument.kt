package kotlinx.cli

interface PositionalArgument {
    val name: String
    val minArgs: Int
    val maxArgs: Int
    val action: ArgumentAction
}