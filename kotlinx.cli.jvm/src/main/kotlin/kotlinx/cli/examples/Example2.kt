package kotlinx.cli.examples

import kotlinx.cli.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val cli = command {
        commandName = "Example2"

        // Define commandName-line interface
        val integers by positionalArgumentsList("N+", "Integers", minArgs = 1)
        val radix by flagValueArgument("-r", "radix", "Input numbers radix", 10) { it.toInt() }
        val sum by flagArgument("--sum", "Print sum")
        val max by flagArgument("--max", "Print max")
        val min by flagArgument("--min", "Print min")

        // main block is where you do something useful
        main {
            // Do something useful
            if (!sum || !max || !min) {
                exitProcess(1) // CommandLineContext::exitProcess() looks like standard kotlin
            }

            val ints = integers.map { it.toInt(radix) }
            println("Args: ${args.asList()}")
            println("Integers: $ints")
            if (sum) println("Sum: ${ints.sum()}")
            if (max) println("Max: ${ints.max()}")
            if (min) println("Min: ${ints.min()}")
        }
    }

    val exitCode = cli.run(args)
    exitProcess(exitCode)
}