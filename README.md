# kotlinx-cli

[![JetBrains incubator project](https://jb.gg/badges/incubator.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/kotlin/kotlinx/kotlinx.cli/images/download.svg)](https://bintray.com/kotlin/kotlinx/kotlinx.cli/_latestVersion)

Pure Kotlin implementation of a generic command-line parser.

* Declarative: describe what your commands and parameters are 
* Platform-agnostic: core library has no platform-specific dependencies and can be used in any Kotlin project 
* Hackable: build extensions on top of it however you like

`kotlinx-cli` can be used to create user-friendly and flexible command-line interfaces
for Kotlin/JVM, Kotlin/Native, and any other Kotlin console applications.
Program defines what arguments are expected.
`kotlinx-cli` will figure out how to parse those, reporting errors if the program arguments are invalid,
and also generate help and usage messages as well.

## Using in your projects

> Note that the library is experimental and the API is subject to change.

The library is published to [kotlinx](https://bintray.com/kotlin/kotlinx/kotlinx.cli) bintray repository.

### Gradle

Add the bintray repository:

```groovy
repositories {
    maven { url "https://kotlin.bintray.com/kotlinx" }
}
```

In Kotlin projects add the following dependency to the needed source set (it may be common or platform specific source set):

```groovy
kotlin {
    sourceSets {
        commonMain {
             dependencies {
                 implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.1")
             }
        }
    }
}
```

`kotlinx-cli` is also included in Kotlin/Native distribution as endorsed library so it's possible to use `kotlinx.cli`
in projects on Kotlin/Native without setting dependency to it. 
If `Gradle` is used to build project turning on endorsed libraries in Kotlin/Native is possible with
```kotlin
kotlin {
    linuxX64("linux") {
        compilations["main"].enableEndorsedLibs = true
    }
}
```

### !!! Important information
If `kotlinx-cli` is added to gradle project as dependency endorsed libraries in Kotlin/Native must be turned off! 
### Maven

Add the bintray repository:

```xml
<repositories>
    <repository>
        <id>kotlinx</id>
        <name>kotlinx</name>
        <url>https://kotlin.bintray.com/kotlinx/</url>
    </repository>
</repositories>
```

In Kotlin projects add the following dependency to the dependencies:

```xml
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>kotlinx-cli-jvm</artifactId>
    <version>0.3.1</version>
</dependency>
```
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

enum class Format {
    HTML,
    CSV,
    PDF
}

fun main(args: Array<String>) {
    val parser = ArgParser("example")
    val input by parser.option(ArgType.String, shortName = "i", description = "Input file").required()
    val output by parser.option(ArgType.String, shortName = "o", description = "Output file name")
    val format by parser.option(ArgType.Choice<Format>(), shortName = "f", 
    	description = "Format for output file").default(Format.CSV).multiple()
    val stringFormat by parser.option(ArgType.Choice(listOf("html", "csv", "pdf"), { it }), shortName = "sf", 
        description = "Format as string for output file").default("csv").multiple()
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
@file:OptIn(ExperimentalCli::class)

import kotlinx.cli.*

fun main(args: Array<String>) {
	val parser = ArgParser("example")
    val output by parser.option(ArgType.String, "output", "o", "Output file")
    class Summary: Subcommand("summary", "Calculate summary") {
        val invert by option(ArgType.Boolean, "invert", "i", "Invert results").default(false)
        val addendums by argument(ArgType.Int, "addendums", description = "Addendums").vararg()
        var result: Int = 0

        override fun execute() {
            result = addendums.sum()
            result = if (invert!!) -1 * result else result
        }
    }
    class Multiply: Subcommand("mul", "Multiply") {
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
    
The boolean property `strictSubcommandOptionsOrder` defines the allowed order of options and arguments for subcommands. 
When it is `false` (default), then the main program's options can be specified everywhere, even after the subcommand.
Otherwise, parameters can only be specified after the subcommands where they are defined. For example,

```kotlin
@file:OptIn(ExperimentalCli::class)

import kotlinx.cli.*

fun main(args: Array<String>) {
    val parser = ArgParser("example", strictSubcommandOptionsOrder = true)
    val output by parser.option(ArgType.String, "output", "o", "Output file")

    class Multiply: Subcommand("mul", "Multiply") {
        val numbers by argument(ArgType.Int, description = "Addendums").vararg()
        var result: Int = 0

        override fun execute() {
            result = numbers.reduce{ acc, it -> acc * it }
        }
    }
    val multiple = Multiply()
    parser.subcommands(summary, multiple)

    parser.parse(args)
}
```
`example -o out.txt mul 1 2 3 -o out.txt # OK`

`example mul 1 2 3 -o out.txt # fail in this case, but OK if strictSubcommandOptionsOrder is false`
