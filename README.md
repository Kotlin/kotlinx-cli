# kotlinx.cli

[![JetBrains incubator project](http://jb.gg/badges/incubator.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
> TODO download link

Pure Kotlin implementation of a generic CLI parser.

* Describe command-line interfaces declaratively
* Use in arbitrary Kotlin project (no platform-specific dependencies)

`kotlinx.cli` can be used to create user-friendly and flexible command-line interfaces
for Kotlin/JVM, Kotlin/Native, and any other Kotlin console applications.
Program defines what arguments are expected.
`kotlinx.cli` will figure out how to parse those, reporting errors if the program arguments are invalid,
and also generate help and usage messages as well.

## Example

```kotlin
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
```

Running this program without arguments produces the following output:
```
Not enough positional arguments for N: 0, expected at least 1
Usage: Example1 -h N --sum --max --min 

-h, --help                Prints help
N                         Integers
--sum                     Print sum
--max                     Print max
--min                     Print min
```

Now, do some real work: run it with arguments `1 2 3 4 5 6 8 --sum`
```
Args: [1, 2, 3, 4, 5, 6, 8, --sum]
Integers: [1, 2, 3, 4, 5, 6, 8]
Sum: 29
```

