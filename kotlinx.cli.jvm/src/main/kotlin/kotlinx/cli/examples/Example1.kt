package kotlinx.cli.examples

import kotlinx.cli.CommandLineInterface
import kotlinx.cli.flagArgument
import kotlinx.cli.parse
import kotlinx.cli.positionalArgumentsList
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    // Create command-line interface
    val cli = CommandLineInterface("Example1")

    // Define command-line arguments
    val integers by cli.positionalArgumentsList("N", "Integers", minArgs = 1) { it.toInt() }
    val sum by cli.flagArgument("--sum", "Print sum")
    val max by cli.flagArgument("--max", "Print max")
    val min by cli.flagArgument("--min", "Print min")

    // Parse arguments or exit
    try {
        cli.parse(args)
    }
    catch (e: Exception) {
        exitProcess(1)
    }

    // Do something useful
    println("Args: ${args.asList()}")
    println("Integers: $integers")
    if (sum) println("Sum: ${integers.sum()}")
    if (max) println("Max: ${integers.max()}")
    if (min) println("Min: ${integers.min()}")
}
