# kotlinx.cli

[![JetBrains incubator project](https://jb.gg/badges/incubator.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)

Pure Kotlin implementation of a generic command-line parser.

* Declarative: describe what your commands and parameters are 
* Platform-agnostic: core library has no platform-specific dependencies and can be used in any Kotlin project 
* Hackable: build extensions on top of it however you like

`kotlinx.cli` can be used to create user-friendly and flexible command-line interfaces
for Kotlin/JVM, Kotlin/Native, and any other Kotlin console applications.
Program defines what arguments are expected.
`kotlinx.cli` will figure out how to parse those, reporting errors if the program arguments are invalid,
and also generate help and usage messages as well.

# Setup

For JVM:  (using `jitpack.io`)
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compile "com.github.kotlin.kotlinx~cli:kotlinx-cli-jvm:-SNAPSHOT"
}
```
**Note: you will need to clear gradle cache if you want to update your kotlinx.cli dependency**

## Example

```kotlin
package kotlinx.cli.examples

import kotlinx.cli.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    // Define command-line interface 
    val cli = CommandLineInterface("Example1")
    val integers by cli.positionalArgumentsList("N+", "Integers", minArgs = 1)
    val radix by cli.flagValueArgument("-r", "radix", "Input numbers radix", 10) { it.toInt() }
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
    val ints = integers.map { it.toInt(radix) }
    println("Args: ${args.asList()}")
    println("Integers: $ints")
    if (sum) println("Sum: ${ints.sum()}")
    if (max) println("Max: ${ints.max()}")
    if (min) println("Min: ${ints.min()}")
}
```

Running this program without arguments produces the following output:
```
Usage: Example1 [-h] N+ [-r radix] [--sum] [--max] [--min] 

-h, --help                Prints help
N+                        Integers
-r radix                  Input numbers radix
--sum                     Print sum
--max                     Print max
--min                     Print min
```

Now, do some real work: run it with arguments `-r 16 CAFE BABE DEAD BEEF --sum`
```
Args: [-r, 16, CAFE, BABE, DEAD, BEEF, --sum]
Integers: [51966, 47806, 57005, 48879]
Sum: 205656
```


