# kotlinx.cli

[![JetBrains incubator project](https://jb.gg/badges/incubator.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/kotlin/kotlin-dev/kotlinx.cli/images/download.svg)](https://bintray.com/kotlin/kotlin-dev/kotlinx.cli/_latestVersion)

Pure Kotlin implementation of a generic command-line parser.

* Declarative: describe what your commands and parameters are 
* Platform-agnostic: core library has no platform-specific dependencies and can be used in any Kotlin project 
* Hackable: build extensions on top of it however you like

`kotlinx.cli` can be used to create user-friendly and flexible command-line interfaces
for Kotlin/JVM, Kotlin/Native, and any other Kotlin console applications.
Program defines what arguments are expected.
`kotlinx.cli` will figure out how to parse those, reporting errors if the program arguments are invalid,
and also generate help and usage messages as well.

## Command line entities
There are 2 base entity: option and argument.

*Option* - command line entity started with some prefix (-/--) and can have value as next entity in command line string.

*Argument* - command line entity which role is connected only with its position.

Command line entities can be several types:
* ArgType.Boolean
* ArgType.Int
* ArgType.String
* ArgType.Double
* ArgType.Choice (value can be only from predefined list)

Custom types can be created.

### Example

```kotlin
import kotlinx.cli.*

fun produce(result: List<Double>, format: String, outputFileName: String?) {
    outputFileName.let {
        // Print to file.
        ...
    } ?: run {
        // Print to stdout.
        ...
    }
}

fun readFrom(inputFileName: String): String {
    ...
}

fun calculate(inputData: String, eps: Double, debug: Boolean = false): List<Double> {
    ...
}

fun main(args: Array<String>) {
    val parser = ArgParser("example")
    val input by parser.option(ArgType.String, shortName = "i", description = "Input file").required()
    val output by parser.option(ArgType.String, shortName = "o", description = "Output file name")
    val format by parser.option(ArgType.Choice(listOf("html", "csv", "pdf")), shortName = "f", 
    	description = "Format for output file").default("csv").multiple()
    val debug by parser.option(ArgType.Boolean, shortName = "d", description = "Turn on debug mode").default(false)
    val eps by parser.option(ArgType.Double, description = "Observational error").default(0.01)

    parser.parse(args)
    val inputData = readFrom(input)
    val result = calculate(inputData, eps, debug)
    format.forEach {
        produce(result, it, output)
    }
}
```

It's also possible to use arguments in current example.

```kotlin
...
    val input by parser.argument(ArgType.String, description = "Input file")
    val output by parser.argument(ArgType.String, description = "Output file name").optional()
```

Auto-generated help message for this example is
```
Usage: example options_list
Arguments: 
    input -> Input file { String }
    output -> Output file name (optional) { String }
Options: 
    --format, -f [csv] -> Format for output file { Value should be one of [html, csv, pdf] }
    --debug, -d [false] -> Turn on debug mode 
    --eps [0.01] -> Observational error { Double }
    --help, -h -> Usage info
```

## Subcommands

If application has rich command line interface and executes different actions with different arguments,
 subcommands can be useful.
 
```kotlin
@file:UseExperimental(ExperimentalCli::class)

import kotlinx.cli.*

fun main(args: Array<String>) {
	val parser = ArgParser("example")
    val output by parser.option(ArgType.String, "output", "o", "Output file")
    class Summary: Subcommand("summary") {
        val invert by option(ArgType.Boolean, "invert", "i", "Invert results")
        val addendums by argument(ArgType.Int, "addendums", description = "Addendums").vararg()
        var result: Int = 0

        override fun execute() {
            result = addendums.sum()
            result = if (invert!!) -1 * result else result
        }
    }
    class Multiply: Subcommand("mul") {
        val numbers by argument(ArgType.Int, description = "Addendums").vararg()
        var result: Int = 0

        override fun execute() {
            result = numbers.reduce{ acc, it -> acc * it }
        }
    }
    val summary = Summary()
    val multiple = Multiply()
    parser.subcommands(summary, multiple)

    parser.parse(args)
}
```

Then help information will be available for each subcommand separately.

In case of `example summary -h` help info will be
```
Usage: example summary options_list
Arguments: 
    addendums -> Addendums { Int }
Options: 
    --invert, -i -> Invert results 
    --help, -h -> Usage info 
```

In case of `example mul -h` help info will be
```
Usage: example mul options_list
Arguments: 
    numbers -> Addendums { Int }
Options: 
    --help, -h -> Usage info
```
 
