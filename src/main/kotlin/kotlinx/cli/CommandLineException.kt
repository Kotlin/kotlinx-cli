package kotlinx.cli

class CommandLineException(message: String) : RuntimeException(message)

class HelpPrintedException : RuntimeException()

class MissingArgumentException : RuntimeException()