package kotlinx.cli

class CommandLineException(message: String) : RuntimeException(message)

open class StopParsingException : RuntimeException()

class HelpPrintedException : StopParsingException()

class MissingArgumentException : RuntimeException()

fun stopParsing() {
    throw StopParsingException()
}